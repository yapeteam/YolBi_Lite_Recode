package net.montoyo.mcef.utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {
    private static final String HEX = "0123456789abcdef";

    /**
     * Clamps d between min and max.
     *
     * @param d   The value to clamp.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static double clamp(double d, double min, double max) {
        if (d < min)
            return min;
        else if (d > max)
            return max;
        else
            return d;
    }

    /**
     * Extracts a ZIP archive into a folder.
     *
     * @param zip The ZIP archive file to extract.
     * @param out The output directory for the ZIP content.
     * @return true if the extraction was successful.
     */
    public static boolean extract(File zip, File out) {
        ZipInputStream zis;

        try {
            zis = new ZipInputStream(new FileInputStream(zip));
        } catch (FileNotFoundException e) {
            Log.error("Couldn't extract %s: File not found.", zip.getName());
            e.printStackTrace();
            return false;
        }

        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory())
                    continue;

                File dst = new File(out, ze.getName());
                delete(dst);
                mkdirs(dst);

                FileOutputStream fos = new FileOutputStream(dst);
                byte[] data = new byte[65536];
                int read;

                while ((read = zis.read(data)) > 0)
                    fos.write(data, 0, read);

                close(fos);
            }

            return true;
        } catch (FileNotFoundException e) {
            Log.error("Couldn't extract a file from %s. Maybe you're missing some permissions?", zip.getName());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.error("IOException while extracting %s.", zip.getName());
            e.printStackTrace();
            return false;
        } finally {
            close(zis);
        }
    }

    /**
     * Returns the SHA-1 checksum of a file.
     *
     * @param fle The file to be hashed.
     * @return The hash of the file or null if an error occurred.
     */
    public static String hash(File fle) {
        FileInputStream fis;

        try {
            fis = new FileInputStream(fle);
        } catch (FileNotFoundException e) {
            Log.error("Couldn't hash %s: File not found.", fle.getName());
            e.printStackTrace();
            return null;
        }

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.reset();

            int read = 0;
            byte buffer[] = new byte[65536];

            while ((read = fis.read(buffer)) > 0)
                sha.update(buffer, 0, read);

            byte digest[] = sha.digest();
            String hash = "";

            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xFF;
                int left = b >>> 4;
                int right = b & 0x0F;

                hash += HEX.charAt(left);
                hash += HEX.charAt(right);
            }

            return hash;
        } catch (IOException e) {
            Log.error("IOException while hashing file %s", fle.getName());
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            Log.error("Holy crap this shouldn't happen. SHA-1 not found!!!!");
            e.printStackTrace();
            return null;
        } finally {
            close(fis);
        }
    }


    /**
     * Renames a file using a string.
     *
     * @param src  The file to rename.
     * @param name The new name of the file.
     * @return the new file or null if it failed.
     */
    public static File rename(File src, String name) {
        File ret = new File(src.getParentFile(), name);

        if (src.renameTo(ret))
            return ret;
        else
            return null;
    }

    /**
     * Makes sure that the directory in which the file is exists.
     * If this one doesn't exist, i'll be created.
     *
     * @param f The file.
     */
    public static void mkdirs(File f) {
        File p = f.getParentFile();
        if (!p.exists())
            p.mkdirs();
    }

    /**
     * Tries to delete a file in an advanced way.
     * Does a warning in log if it couldn't delete it neither rename it.
     *
     * @param f The file to be deleted.
     * @see #delete(File)
     */
    public static void delete(String f) {
        delete(new File(f));
    }

    /**
     * Tries to delete a file in an advanced way.
     * Does a warning in log if it couldn't delete it neither rename it.
     *
     * @param f The file to be deleted.
     * @see #delete(String)
     */
    public static void delete(File f) {
        if (!f.exists() || f.delete())
            return;

        File mv = new File(f.getParentFile(), "deleteme" + ((int) (Math.random() * 100000.d)));
        if (f.renameTo(mv)) {
            if (!mv.delete())
                mv.deleteOnExit();

            return;
        }

        Log.warning("Couldn't delete file! If there's any problems, please try to remove it yourself. Path: %s", f.getAbsolutePath());
    }


    /**
     * Calls "close" on the specified object without throwing any exceptions.
     * This is usefull with input and output streams.
     *
     * @param o The object to call close on.
     */
    public static void close(Object o) {
        try {
            o.getClass().getMethod("close").invoke(o);
        } catch (Throwable t) {
        }
    }

    /**
     * Same as {@link Files#isSameFile(Path, Path)} but if an {@link IOException} is thrown,
     * return false.
     *
     * @param p1 Path 1
     * @param p2 Path 2
     * @return true if the paths are the same, false if they are not or if an exception is thrown during the comparison
     */
    public static boolean isSameFile(Path p1, Path p2) {
        try {
            return Files.isSameFile(p1, p2);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Same as {@link System#getenv(String)}, but if no such environment variable is
     * defined, will return an empty string instead of null.
     *
     * @param name Name of the environment variable to get
     * @return The value of this environment variable (may be empty but never null)
     */
    public static String getenv(String name) {
        String ret = System.getenv(name);
        return ret == null ? "" : ret;
    }
}
