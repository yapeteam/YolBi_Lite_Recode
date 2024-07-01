package cn.yapeteam.yolbi.antileak.check;

import cn.yapeteam.yolbi.antileak.AntiLeak;
import cn.yapeteam.yolbi.antileak.utils.os.OSUtils;

public class OSCheck {
    public static void check() {
        if (OSUtils.getPlatform() != OSUtils.OS.WINDOWS) {
            AntiLeak.instance.crash();
        }
    }
}
