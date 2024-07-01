package cn.yapeteam.yolbi.antileak;

import cn.yapeteam.yolbi.antileak.check.JvmProcessCheck;
import cn.yapeteam.yolbi.antileak.check.OSCheck;
import cn.yapeteam.yolbi.antileak.confusion.GenFakeIP;
import cn.yapeteam.yolbi.antileak.confusion.GenRandomString;
import cn.yapeteam.yolbi.antileak.utils.HwidUtils;

public class AntiLeak {
    public static final AntiLeak instance = new AntiLeak();
    public boolean isPremium = true; // TODO premium版本区分
    public String userName = GenRandomString.generateRandomString(); // TODO user注册
    public String latestVersion = GenRandomString.generateRandomString(); // TODO 最新版本获取
    public String hwidUrl = GenFakeIP.generateRandomIP(4);
    public String latestVersionUrl = GenFakeIP.generateRandomIP(4);
    public final String ENCODE_KEY = "XXDoYou_LoveMeXX";

    // TODO 应该在主类里调用此方法
    public void init() {
        // checks
        OSCheck.check();
        JvmProcessCheck.check();

        if (userName.length() != 32 || latestVersion.length() != 32) {
            crash();
        }

        if (isPremium) {
            hwidUrl = ""; // TODO 验证服务器
            latestVersionUrl = ""; // TODO 最新版本信息服务器
        } else {
            hwidUrl = "not premium";
            latestVersionUrl = "not premium";
        }

        try {
            HwidUtils.getHwid();
        } catch (Exception e) {
            crash();
        }
    }

    @SuppressWarnings("all")
    public void crash() {
        // TODO 更好的崩溃操作
        try {
            crash();
        } catch (Exception e) {
            crash();
        }
    }
}
