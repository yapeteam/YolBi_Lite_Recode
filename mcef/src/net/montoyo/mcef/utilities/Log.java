package net.montoyo.mcef.utilities;

import cn.yapeteam.loader.logger.Logger;

/**
 * A set of functions to log messages into the MCEF log channel.
 *
 * @author montoyo
 */
public class Log {
    private static String convert(String str) {
        return "[MCEF] " + str.replace("%s", "{}").replace("%d", "{}");
    }

    public static void info(String what, Object... data) {
        Logger.info(convert(what), data);
    }

    public static void warning(String what, Object... data) {
        Logger.warn(convert(what), data);
    }

    public static void error(String what, Object... data) {
        Logger.error(convert(what), data);
    }

    public static void exception(Throwable t) {
        Logger.exception(t);
    }
}
