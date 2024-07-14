import lombok.var;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main1 {
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private static final Map<String, String> methodMap = new HashMap<>();
    private static final Map<String, String> fieldMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        var vanilla = new String(readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/forge.srg"))), StandardCharsets.UTF_8);
        var method = new String(readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/methods.csv"))), StandardCharsets.UTF_8);
        var field = new String(readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/fields.csv"))), StandardCharsets.UTF_8);
        vanilla = vanilla.replace(String.valueOf((char) 13), "");
        method = method.replace(String.valueOf((char) 13), "");
        field = field.replace(String.valueOf((char) 13), "");
        for (String s : method.split("\n")) {
            String[] arr = s.split(",");
            methodMap.put(arr[0], arr[1]);
        }
        for (String s : field.split("\n")) {
            String[] arr = s.split(",");
            fieldMap.put(arr[0], arr[1]);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : vanilla.split("\n")) {
            String[] arr = s.split(" ");
            String searge, friendly;
            switch (arr[0]) {
                case "FD:":
                    String[] arr2 = arr[2].split("/");
                    searge = arr2[arr2.length - 1];
                    friendly = fieldMap.get(searge);
                    if (friendly != null)
                        s = s.replace(searge, friendly);
                    sb.append(s).append("\n");
                    break;
                case "MD:":
                    String[] arr3 = arr[3].split("/");
                    searge = arr3[arr3.length - 1];
                    friendly = methodMap.get(searge);
                    if (friendly != null)
                        s = s.replace(searge, friendly);
                    sb.append(s).append("\n");
                    break;
                case "CL:":
                default:
                    sb.append(s).append("\n");
                    break;
            }
        }
        Files.write(new File("vanilla.srg").toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
