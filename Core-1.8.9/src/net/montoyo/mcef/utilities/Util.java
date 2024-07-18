package net.montoyo.mcef.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Util {
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
