package cn.yapeteam.builder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import proguard.ProGuard;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("SameParameterValue")
public class Builder {
    private static void copyStream(OutputStream os, InputStream is) throws IOException {
        int len;
        byte[] bytes = new byte[4096];
        while ((len = is.read(bytes)) != -1)
            os.write(bytes, 0, len);
    }

    private static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    public interface Action {
        void execute(File file);
    }

    public static void traverseFiles(File folder, Action action) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    traverseFiles(file, action);
                else action.execute(file);
            }
        }
    }

    private static void disposeInclude(Node node, ZipOutputStream output, String root_dir) {
        boolean is_root = true;
        for (char c : root_dir.toCharArray()) {
            if (c != '/' && c != '.') {
                is_root = false;
                break;
            }
        }
        if (is_root) root_dir = "";
        Node attr = node.getAttributes().item(0);
        switch (node.getNodeName()) {
            case "dir": {
                if (attr == null) {
                    System.out.println("dir " + node.getTextContent());
                    File dir = new File(node.getTextContent());
                    String parent = dir.getParent();
                    String root = parent != null ? parent : "/";
                    String finalRoot_dir = root_dir;
                    traverseFiles(dir, file -> {
                        String path = file.toString();
                        System.out.println(path);
                        String entry_name = root.length() > 1 ? finalRoot_dir + path.substring(root.length()).replace("\\", "/").substring(1) : finalRoot_dir + path.replace("\\", "/");
                        ZipEntry entry = new ZipEntry(entry_name);
                        try {
                            output.putNextEntry(entry);
                            output.write(readStream(Files.newInputStream(file.toPath())));
                            output.closeEntry();
                        } catch (IOException ignored) {
                        }
                    });
                } else {
                    root_dir = root_dir + "/" + attr.getNodeValue();
                    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                        Node child = node.getChildNodes().item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE)
                            disposeInclude(child, output, root_dir);
                    }
                }
            }
            break;
            case "files": {
                System.out.println("files " + node.getTextContent());
                File dir = new File(node.getTextContent());
                String root = node.getTextContent();
                String finalRoot_dir = root_dir;
                traverseFiles(dir, file -> {
                    String path = file.toString();
                    String entry_name = finalRoot_dir + (finalRoot_dir.isEmpty() ? "" : "/") + path.substring(root.length()).replace("\\", "/").substring(1);
                    ZipEntry entry = new ZipEntry(entry_name);
                    try {
                        output.putNextEntry(entry);
                        output.write(readStream(Files.newInputStream(file.toPath())));
                        output.closeEntry();
                    } catch (IOException ignored) {
                    }
                });
            }
            break;
            case "file": {
                System.out.println("file " + node.getTextContent());
                File file = new File(node.getTextContent());
                String path = file.toString();
                String entry_name = root_dir + (root_dir.isEmpty() ? "" : "/") + path.substring(file.getParent() != null ? file.getParent().length() : 0).replace("\\", "/").substring(1);
                ZipEntry entry = new ZipEntry(entry_name);
                try {
                    output.putNextEntry(entry);
                    output.write(readStream(Files.newInputStream(file.toPath())));
                    output.closeEntry();
                } catch (IOException ignored) {
                }
            }
            break;
            case "extract": {
                try {
                    System.out.println("extract " + node.getTextContent());
                    String path = node.getTextContent();
                    ZipInputStream input = new ZipInputStream(Files.newInputStream(Paths.get(path)));
                    ZipEntry entry_in;
                    while ((entry_in = input.getNextEntry()) != null) {
                        if (entry_in.isDirectory()) continue;
                        String entry_name = entry_in.getName();
                        if (entry_name.startsWith("module-info.class")) continue;
                        if (entry_name.startsWith("META-INF/")) continue;
                        if (entry_name.startsWith(root_dir))
                            entry_name = entry_name.substring(root_dir.length());
                        if (entry_name.startsWith("/"))
                            entry_name = entry_name.substring(1);
                        ZipEntry entry_out = new ZipEntry(root_dir + (root_dir.isEmpty() ? "" : "/") + entry_name);
                        output.putNextEntry(entry_out);
                        copyStream(output, input);
                        output.closeEntry();
                    }
                } catch (IOException ignored) {
                }
            }
            break;
            case "archive": {
                String name = attr.getNodeValue();
                System.out.println("archive " + name);
                ZipEntry entry = new ZipEntry(root_dir + (root_dir.isEmpty() ? "" : "/") + name);
                try {
                    output.putNextEntry(entry);
                    ZipOutputStream output_inner = new ZipOutputStream(output);
                    output_inner.setMethod(ZipOutputStream.DEFLATED);
                    output_inner.setLevel(Deflater.BEST_COMPRESSION);
                    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                        Node child = node.getChildNodes().item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE)
                            disposeInclude(child, output_inner, "/");
                    }
                    output_inner.finish();
                    output.closeEntry();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    private static void downloadFile(String file_url, File file) throws Exception {
        SSLUtils.ignoreSsl();
        URL url = new URL(file_url);
        URLConnection connection = url.openConnection();
        int totalFileSize = connection.getContentLength();
        FileOutputStream outputFile = new FileOutputStream(file);
        int blockSize = 1024 * 1024;
        byte[] buffer = new byte[blockSize];
        int bytesRead;
        int downloadedBytes = 0;
        ProcessBar progressBar = new ProcessBar(100);
        while (downloadedBytes < totalFileSize) {
            int bytesToRead = Math.min(blockSize, totalFileSize - downloadedBytes);
            InputStream inputStream = connection.getInputStream();
            bytesRead = inputStream.read(buffer, 0, bytesToRead);
            if (bytesRead == -1) break;
            outputFile.write(buffer, 0, bytesRead);
            downloadedBytes += bytesRead;
            progressBar.update((int) (((float) downloadedBytes / totalFileSize) * 100));
        }
        outputFile.close();
    }

    private static boolean advanced_mode;
    // advanced_mode:需要安装ollvm进行编译兼混淆
    // 1.https://github.com/llvm/llvm-project/releases/tag/llvmorg-17.0.6
    // 2.https://github.com/DreamSoule/ollvm17/releases/tag/17.0.6
    // 3.https://winlibs.com/#download-release
    // 4.安装LLVM-17.0.6-win64.exe后
    // 5.将下载的ollvm的三个文件覆盖到LLVM/bin目录下
    // 6.将LLVM/bin目录添加到环境变量PATH中
    // 7.将下载的mingw覆盖到LLVM目录下
    // <unknown-file>:0: syntax error 不用管

    private static void generateHeaderFromClass(File clazz, File header, String field_name) throws IOException {
        InputStream is = Files.newInputStream(clazz.toPath());
        int value;
        int size = 0;
        StringBuilder string = new StringBuilder();
        string.append(String.format("const unsigned char %s[] = {", field_name));
        while ((value = is.read()) != -1) {
            string.append(value).append(",");
            size++;
        }
        string.append("};\n");
        is.close();
        string.append(String.format("const jsize %s_size = ", field_name)).append(size).append(";\n");
        Files.write(header.toPath(), string.toString().getBytes());
    }

    private static void buildDLL() throws Exception {
        File dir = new File("Loader/dll/build");
        boolean ignored = dir.mkdirs();
        System.out.println("Building DLL...");
        generateHeaderFromClass(new File("out/production/Builder/cn/yapeteam/builder/Unzip.class"), new File("Loader/dll/src/dll/unzip.h"), "unzip_data");
        Terminal terminal = new Terminal(dir, null);
        if (advanced_mode) {
            String target = "--target=x86_64-w64-mingw";
            terminal.execute(new String[]{"clang-cl",
                    "-mllvm", "-fla", "-mllvm", "-bcf", "-mllvm", "-bcf_prob=80",
                    "-mllvm", "-bcf_loop=2", "-mllvm", "-sobf", "-mllvm", "-icall",
                    "-mllvm", "-sub", "-mllvm", "-sub_loop=2", "-mllvm", "-igv",
                    target, "-c", "../src/dll/Main.c", "-o", "Main.o",});
            terminal.execute(new String[]{"clang", target, "-c", "../src/dll/ReflectiveLoader.c", "-o", "ReflectiveLoader.o"});
            terminal.execute(new String[]{"clang-cl",
                    "-mllvm", "-fla", "-mllvm", "-bcf", "-mllvm", "-bcf_prob=80",
                    "-mllvm", "-bcf_loop=2", "-mllvm", "-sobf", "-mllvm", "-icall",
                    "-mllvm", "-sub", "-mllvm", "-sub_loop=2", "-mllvm", "-split",
                    "-mllvm", "-split_num=5", "-mllvm", "-igv",
                    target, "-c", "../src/dll/utils.c", "-o", "utils.o"});
            terminal.execute(new String[]{"clang", target, "-shared", "Main.o", "ReflectiveLoader.o", "utils.o", "-o", "libinjection.dll"});

            terminal.execute(new String[]{"clang-cl",
                    "-mllvm", "-fla", "-mllvm", "-bcf", "-mllvm", "-bcf_prob=80",
                    "-mllvm", "-bcf_loop=2", "-mllvm", "-sobf", "-mllvm", "-icall",
                    "-mllvm", "-sub", "-mllvm", "-sub_loop=2", "-mllvm", "-split",
                    "-mllvm", "-split_num=1", "-mllvm", "-igv",
                    target, "-c", "../src/inject/GetProcAddressR.c", "-o", "GetProcAddressR.o"});
            terminal.execute(new String[]{"clang-cl",
                    "-mllvm", "-fla", "-mllvm", "-bcf", "-mllvm", "-bcf_prob=80",
                    "-mllvm", "-bcf_loop=2", "-mllvm", "-sobf", "-mllvm", "-icall",
                    "-mllvm", "-sub", "-mllvm", "-sub_loop=2", "-mllvm", "-split",
                    "-mllvm", "-split_num=1", "-mllvm", "-igv",
                    target, "-c", "../src/inject/LoadLibraryR.c", "-o", "LoadLibraryR.o"});
            terminal.execute(new String[]{"clang-cl",
                    "-mllvm", "-fla", "-mllvm", "-bcf", "-mllvm", "-bcf_prob=80",
                    "-mllvm", "-bcf_loop=2", "-mllvm", "-sobf", "-mllvm", "-icall",
                    "-mllvm", "-sub", "-mllvm", "-sub_loop=2", "-mllvm", "-split",
                    "-mllvm", "-split_num=1", "-mllvm", "-igv",
                    target, "-c", "../src/inject/Inject.c", "-o", "Inject.o"});
            terminal.execute(new String[]{"clang",
                    target, "-shared", "GetProcAddressR.o", "LoadLibraryR.o", "Inject.o", "-o", "libapi.dll"});
        } else {
            terminal.execute(new String[]{"gcc.exe", "-c", "../src/dll/Main.c", "-o", "Main.o"});
            terminal.execute(new String[]{"gcc.exe", "-c", "../src/dll/ReflectiveLoader.c", "-o", "ReflectiveLoader.o"});
            terminal.execute(new String[]{"gcc.exe", "-c", "../src/dll/utils.c", "-o", "utils.o"});
            terminal.execute(new String[]{"gcc.exe", "-shared", "Main.o", "ReflectiveLoader.o", "utils.o", "-o", "libinjection.dll"});

            terminal.execute(new String[]{"gcc.exe", "-c", "../src/inject/GetProcAddressR.c", "-o", "GetProcAddressR.o"});
            terminal.execute(new String[]{"gcc.exe", "-c", "../src/inject/LoadLibraryR.c", "-o", "LoadLibraryR.o"});
            terminal.execute(new String[]{"gcc.exe", "-c", "../src/inject/Inject.c", "-o", "Inject.o"});
            terminal.execute(new String[]{"gcc.exe", "-shared", "GetProcAddressR.o", "LoadLibraryR.o", "Inject.o", "-o", "libapi.dll"});
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) return;
        advanced_mode = args[0].equals("release");
        System.setSecurityManager(new NoExitSecurityManager());
        buildDLL();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("YBuild.xml");
        Element root = document.getDocumentElement();
        String root_dir = root.getElementsByTagName("rootdir").item(0).getTextContent();
        String output_dir = root.getElementsByTagName("output").item(0).getTextContent();
        Element build = (Element) root.getElementsByTagName("build").item(0);
        for (int i = 0; i < build.getChildNodes().getLength(); i++) {
            Node node = build.getChildNodes().item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                switch (element.getTagName()) {
                    case "artifact": {
                        String artifact_name = element.getAttribute("name");
                        String artifact_id = artifact_name.substring(0, artifact_name.lastIndexOf("."));
                        File output_file = new File(output_dir, artifact_name);
                        boolean ignored = output_file.getParentFile().mkdirs();
                        Node proguard_cfg = element.getAttributes().getNamedItem("proguard-config");
                        Node mosey_cfg = element.getAttributes().getNamedItem("mosey-config");
                        Node launch4j_cfg = element.getAttributes().getNamedItem("launch4j-config");
                        System.out.printf("building artifact %s...%n", artifact_name);
                        ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(output_file.toPath()));
                        List<Node> includes_list = new ArrayList<>();
                        for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                            Node include = element.getChildNodes().item(j);
                            output.setMethod(ZipOutputStream.DEFLATED);
                            output.setLevel(Deflater.BEST_COMPRESSION);
                            if (include.getNodeType() == Node.ELEMENT_NODE)
                                includes_list.add(include);
                        }
                        for (int j = 0; j < includes_list.size(); j++) {
                            Node include = includes_list.get(j);
                            disposeInclude(include, output, root_dir + "/");
                            System.out.printf("artifact %s: included %s, %s of %s%n", artifact_name, include.getNodeName(), j + 1, includes_list.size());
                        }
                        output.close();
                        if (!advanced_mode) break;
                        if (proguard_cfg != null) {
                            File build_dir = new File(output_dir, artifact_id);
                            if (build_dir.exists())
                                deleteFileByStream(build_dir.getAbsolutePath());
                            boolean ignored0 = build_dir.mkdirs();
                            File tobe_proguard = new File(build_dir, artifact_name);
                            File artifact_file = new File(output_dir, artifact_name);
                            copyStream(Files.newOutputStream(tobe_proguard.toPath()), Files.newInputStream(artifact_file.toPath()));
                            try {
                                ProGuard.main(new String[]{"@" + proguard_cfg.getNodeValue()});
                            } catch (ExitException ignored1) {
                            }
                        }
                        if (mosey_cfg != null)
                            rip.hippo.mosey.Main.main(new String[]{String.format("-config%s", mosey_cfg.getNodeValue())});
                        if (launch4j_cfg != null) {
                            Terminal terminal = new Terminal(new File("."), null);
                            terminal.execute(new String[]{"launch4jc", launch4j_cfg.getNodeValue()});
                        }
                        break;
                    }
                    case "native-obfuscate": {
                        if (!advanced_mode) break;
                        Node artifact = element.getAttributes().getNamedItem("artifact");
                        Node black = element.getAttributes().getNamedItem("black");
                        Node white = element.getAttributes().getNamedItem("white");
                        String artifact_name = artifact.getNodeValue();
                        String artifact_id = artifact_name.substring(0, artifact_name.lastIndexOf("."));
                        File build_dir = new File(output_dir, artifact_id);
                        if (build_dir.exists())
                            deleteFileByStream(build_dir.getAbsolutePath());
                        boolean ignored = build_dir.mkdirs();
                        if (black != null)
                            generateList(new File(build_dir, "blacklist.txt"), black);
                        if (white != null)
                            generateList(new File(build_dir, "whitelist.txt"), white);
                        String custom_lib_dir = artifact_id + "_yolbi";
                        String[] obf_args = new String[black != null || white != null ? 7 : 5];
                        obf_args[0] = "./" + output_dir + "/" + artifact_name;
                        obf_args[1] = "./" + output_dir + "/" + artifact_id;
                        if (black != null) {
                            obf_args[2] = "-b";
                            obf_args[3] = "./" + output_dir + "/" + artifact_id + "/blacklist.txt";
                            obf_args[4] = "-p";
                            obf_args[5] = "STD_JAVA";
                            obf_args[6] = "--custom-lib-dir=" + custom_lib_dir;
                        } else if (white != null) {
                            obf_args[2] = "-w";
                            obf_args[3] = "./" + output_dir + "/" + artifact_id + "/whitelist.txt";
                            obf_args[4] = "-p";
                            obf_args[5] = "STD_JAVA";
                            obf_args[6] = "--custom-lib-dir=" + custom_lib_dir;
                        } else {
                            obf_args[2] = "-p";
                            obf_args[3] = "STD_JAVA";
                            obf_args[4] = "--custom-lib-dir=" + custom_lib_dir;
                        }
                        System.out.println("Args: " + Arrays.toString(obf_args));
                        try {
                            by.radioegor146.Main.main(obf_args);
                        } catch (ExitException ignored0) {
                        }
                        File obf_out_dir = new File(build_dir, "cpp");
                        File obf_out_file = new File(build_dir, artifact_name);
                        File native_file = buildNative(obf_out_dir);
                        File new_artifact_file = new File(output_dir, artifact_name);
                        ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(new_artifact_file.toPath()));
                        output.setMethod(ZipOutputStream.DEFLATED);
                        output.setLevel(Deflater.BEST_COMPRESSION);
                        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(obf_out_file.toPath()))) {
                            ZipEntry entry_in;
                            while ((entry_in = input.getNextEntry()) != null) {
                                ZipEntry entry_out = new ZipEntry(entry_in);
                                output.putNextEntry(entry_out);
                                copyStream(output, input);
                                output.closeEntry();
                            }
                            output.putNextEntry(new ZipEntry(custom_lib_dir + "/x64-windows.dll"));
                            copyStream(output, Files.newInputStream(native_file.toPath()));
                            output.closeEntry();
                        }
                        output.close();
                        break;
                    }
                }
            }
        }
        System.out.println("BUILD SUCCESS");
    }

    public static void deleteFileByStream(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(Builder::deleteDirectoryStream);
        }
    }

    private static void deleteDirectoryStream(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File buildNative(File dir) throws Exception {
        boolean ignored = dir.mkdirs();
        File output = new File(dir, "build");
        File src = new File(dir, "output");
        if (!output.exists())
            output.mkdirs();
        copyStream(Files.newOutputStream(new File(dir, "jni.h").toPath()), Objects.requireNonNull(Builder.class.getResourceAsStream("/jni.h")));
        copyStream(Files.newOutputStream(new File(dir, "jni_md.h").toPath()), Objects.requireNonNull(Builder.class.getResourceAsStream("/jni_md.h")));
        copyStream(Files.newOutputStream(new File(dir, "jvmti.h").toPath()), Objects.requireNonNull(Builder.class.getResourceAsStream("/jvmti.h")));
        if (advanced_mode) {
            Terminal terminal = new Terminal(output, null);
            String target = "--target=x86_64-w64-mingw";
            ArrayList<String> binaries = new ArrayList<>();
            terminal.execute(new String[]{"clang-cl", target, "-c", "../native_jvm.cpp", "-o", "native_jvm.o"});
            terminal.execute(new String[]{"clang-cl", target, "-c", "../native_jvm_output.cpp", "-o", "native_jvm_output.o"});
            terminal.execute(new String[]{"clang-cl", target, "-c", "../string_pool.cpp", "-o", "string_pool.o"});
            binaries.add("native_jvm.o");
            binaries.add("native_jvm_output.o");
            binaries.add("string_pool.o");
            for (String file : Objects.requireNonNull(src.list())) {
                if (file.endsWith(".cpp")) {
                    terminal.execute(new String[]{"clang-cl",
                            "-mllvm", "-bcf",
                            "-mllvm", "-bcf_loop=1", "-mllvm", "-sobf", "-mllvm", "-icall",
                            "-mllvm", "-sub", "-mllvm", "-split",
                            "-mllvm", "-split_num=2",
                            target, "-c", "../output/" + file, "-o", file.substring(0, file.lastIndexOf(".")) + ".o"});
                    binaries.add(file.replace(".cpp", ".o"));
                }
            }
            String[] linkArgs = new String[1 + 1 + 1 + binaries.size() + 4 + 1 + 1];
            linkArgs[0] = "clang++";
            linkArgs[1] = target;
            linkArgs[2] = "-shared";
            for (int i = 0; i < binaries.size(); i++)
                linkArgs[3 + i] = binaries.get(i);
            linkArgs[3 + binaries.size()] = "-Wl,-Bstatic,--whole-archive";
            linkArgs[4 + binaries.size()] = "-lwinpthread";
            linkArgs[5 + binaries.size()] = "-Wl,--no-whole-archive";
            linkArgs[6 + binaries.size()] = "-Wl,-Bdynamic";
            linkArgs[7 + binaries.size()] = "-o";
            linkArgs[8 + binaries.size()] = "native.dll";
            terminal.execute(linkArgs);
        } else {
            Terminal terminal = new Terminal(output, null);
            String compiler = "g++.exe";
            ArrayList<String> binaries = new ArrayList<>();
            terminal.execute(new String[]{compiler, "-c", "../native_jvm.cpp", "-o", "native_jvm.o"});
            terminal.execute(new String[]{compiler, "-c", "../native_jvm_output.cpp", "-o", "native_jvm_output.o"});
            terminal.execute(new String[]{compiler, "-c", "../string_pool.cpp", "-o", "string_pool.o"});

            binaries.add("native_jvm.o");
            binaries.add("native_jvm_output.o");
            binaries.add("string_pool.o");
            for (String file : Objects.requireNonNull(src.list())) {
                if (file.endsWith(".cpp")) {
                    String out = file.substring(0, file.lastIndexOf(".")) + ".o";
                    terminal.execute(new String[]{compiler, "-c", "../output/" + file, "-o", out});
                    binaries.add(out);
                }
            }
            String[] linkArgs = new String[1 + 1 + binaries.size() + 4 + 1 + 1];
            linkArgs[0] = compiler;
            linkArgs[1] = "-shared";
            for (int i = 0; i < binaries.size(); i++)
                linkArgs[2 + i] = binaries.get(i);
            linkArgs[2 + binaries.size()] = "-Wl,-Bstatic,--whole-archive";
            linkArgs[3 + binaries.size()] = "-lwinpthread";
            linkArgs[4 + binaries.size()] = "-Wl,--no-whole-archive";
            linkArgs[5 + binaries.size()] = "-Wl,-Bdynamic";
            linkArgs[6 + binaries.size()] = "-o";
            linkArgs[7 + binaries.size()] = "native.dll";
            terminal.execute(linkArgs);
        }
        return new File(output, "native.dll");
    }

    private static void generateList(File file, Node exclude) throws IOException {
        ArrayList<String> exclude_list = new ArrayList<>();
        if (exclude != null) {
            String[] values = exclude.getNodeValue().split(",");
            for (String value : values) exclude_list.add(value.trim());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String exclude_name : exclude_list)
                writer.write(exclude_name.replace(".", "/") + "\n");
        }
    }

    public static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    public static class ExitException extends SecurityException {
        private static final long serialVersionUID = 1L;
        public final int status;

        public ExitException(int status) {
            super("ignore");
            this.status = status;
        }
    }
}
