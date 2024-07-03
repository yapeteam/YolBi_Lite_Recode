package cn.yapeteam.builder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
                String entry_name = root_dir + (root_dir.isEmpty() ? "" : "/") + path.substring(file.getParent().length()).replace("\\", "/").substring(1);
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

    private static final String mingw_url = "https://github.com/brechtsanders/winlibs_mingw/releases/download/11.2.0-10.0.0-ucrt-r1/winlibs-x86_64-posix-seh-gcc-11.2.0-mingw-w64ucrt-10.0.0-r1.zip";

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

    private static final String MINGW_PATH = "Loader/dll/mingw";

    private static void checkMinGW() throws Exception {
        String os_name = System.getProperty("os.name").toLowerCase();
        if (os_name.contains("windows") && !os_name.contains("linux")) {
            File mingw_dir = new File(MINGW_PATH);
            if (mingw_dir.exists()) {
                System.out.println("MinGW already exists");
                return;
            }
            mingw_dir.mkdirs();
            System.out.println("Downloading MinGW...");
            downloadFile(mingw_url, new File(mingw_dir, "mingw.zip"));
            System.out.println("Extracting MinGW...");
            ZipInputStream input = new ZipInputStream(Files.newInputStream(Paths.get(mingw_dir.getAbsolutePath(), "mingw.zip")));
            ZipEntry entry_in;
            while ((entry_in = input.getNextEntry()) != null) {
                if (entry_in.isDirectory()) continue;
                String entry_name = entry_in.getName();
                if (entry_name.startsWith("module-info.class")) continue;
                if (entry_name.startsWith("META-INF/")) continue;
                File entry_file = new File(mingw_dir, entry_name);
                entry_file.getParentFile().mkdirs();
                FileOutputStream output = new FileOutputStream(entry_file);
                copyStream(output, input);
                output.close();
            }
            input.close();
            System.out.println("MinGW extracted");
        } else {
            System.out.println("Platform not supported");
            throw new RuntimeException("Platform not supported");
        }
    }

    private static void buildDLL() throws Exception {
        File dir = new File("Loader/dll/build");
        dir.mkdirs();
        System.out.println("Building DLL...");
        String gcc_path =new File("C:\\MinGW\\bin\\gcc.exe").getAbsolutePath(); //new File("Loader/dll/mingw/mingw64/bin/gcc.exe").getAbsolutePath();
        Terminal terminal = new Terminal(dir, null);
        terminal.execute(new String[]{gcc_path, "-c", "../src/dll/Main.c", "-o", "Main.o"});
        terminal.execute(new String[]{gcc_path, "-c", "../src/dll/ReflectiveLoader.c", "-o", "ReflectiveLoader.o"});
        terminal.execute(new String[]{gcc_path, "-c", "../src/dll/utils.c", "-o", "utils.o"});
        terminal.execute(new String[]{gcc_path, "-shared", "Main.o", "ReflectiveLoader.o", "utils.o", "-o", "libinjection.dll"});

        terminal.execute(new String[]{gcc_path, "-c", "../src/inject/GetProcAddressR.c", "-o", "GetProcAddressR.o"});
        terminal.execute(new String[]{gcc_path, "-c", "../src/inject/LoadLibraryR.c", "-o", "LoadLibraryR.o"});
        terminal.execute(new String[]{gcc_path, "-c", "../src/inject/Inject.c", "-o", "Inject.o"});
        terminal.execute(new String[]{gcc_path, "-shared", "GetProcAddressR.o", "LoadLibraryR.o", "Inject.o", "-o", "libapi.dll"});
    }

    public static void main(String[] args) throws Exception {
        checkMinGW();
        buildDLL();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("YBuild.xml");
        Element root = document.getDocumentElement();
        String root_dir = root.getElementsByTagName("rootdir").item(0).getTextContent();
        String output_dir = root.getElementsByTagName("output").item(0).getTextContent();
        Element artifacts = (Element) root.getElementsByTagName("artifacts").item(0);
        for (int i = 0; i < artifacts.getElementsByTagName("artifact").getLength(); i++) {
            Element artifact = (Element) artifacts.getElementsByTagName("artifact").item(i);
            String artifact_name = artifact.getAttribute("name");
            String artifact_binary = artifact.getElementsByTagName("binary").item(0).getTextContent();
            Element includes = (Element) artifact.getElementsByTagName("includes").item(0);
            File output_file = new File(output_dir, artifact_name);
            boolean ignored = output_file.getParentFile().mkdirs();
            System.out.printf("building artifact %s...%n", artifact_name);
            ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(output_file.toPath()));
            traverseFiles(new File(artifact_binary), file -> {
                String path = file.toString().replace("\\", "/");
                String entry_name = path.substring(artifact_binary.length()).replace("\\", "/").substring(1);
                ZipEntry entry = new ZipEntry(entry_name);
                try {
                    output.putNextEntry(entry);
                    output.write(readStream(Files.newInputStream(file.toPath())));
                    output.closeEntry();
                } catch (IOException ignored1) {
                }
            });
            List<Node> includes_list = new ArrayList<>();
            for (int j = 0; j < includes.getChildNodes().getLength(); j++) {
                Node include = includes.getChildNodes().item(j);
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
        }
        System.out.println("BUILD SUCCESS");
    }
}
