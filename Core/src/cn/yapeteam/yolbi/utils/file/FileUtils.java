package cn.yapeteam.yolbi.utils.file;


import java.io.*;

public class FileUtils {
    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static byte[] readBinaryPath(String path) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                System.err.println("Resource not found: " + path);
                return null; // Early exit if resource is not found
            }
            byte[] buffer = new byte[4096]; // Use a buffer for efficient reading
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null or handle accordingly on IOException
        }
    }

    public static String readPath(String path) {
        StringBuilder builder = new StringBuilder();

        InputStream is = null;
        try {
            is = FileUtils.class.getResourceAsStream(path);
            if (is == null) return null;

            int b;
            while ((b = is.read()) != -1) {
                builder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    public static void createDir(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
