package cn.yapeteam.yolbi.antileak;

import cn.yapeteam.yolbi.antileak.check.JvmProcessCheck;
import cn.yapeteam.yolbi.antileak.check.OSCheck;
import cn.yapeteam.yolbi.antileak.check.VMCheck;
import cn.yapeteam.yolbi.antileak.confusion.GenFakeIP;
import cn.yapeteam.yolbi.antileak.confusion.GenRandomString;
import cn.yapeteam.yolbi.antileak.utils.HwidUtils;
import cn.yapeteam.yolbi.antileak.utils.encode.AESEncode;

public class AntiLeak {
    public static final AntiLeak instance = new AntiLeak();
    public boolean isPremium = true; // TODO premium版本区分
    public String userName = GenRandomString.generateRandomString(); // TODO user注册
    public String latestVersion = GenRandomString.generateRandomString(); // TODO 最新版本获取
    public String hwidUrl = GenFakeIP.generateRandomIP(4);
    public String latestVersionUrl = GenFakeIP.generateRandomIP(4);
    public String ENCODE_KEY = "XXDoYou_LoveMeXX";

    static {
        System.loadLibrary("AntiLeak");
    }

    private native String getHwid();
    private native void crash();
    private native boolean checkVM(); // native的虚拟机检测

    // TODO 应该在主类里调用此方法
    public void init() {
        // os check
        OSCheck.check();

        // native check
        if (checkVM()) {
            doCrash();
        }

        // java checks
        VMCheck.check();
        JvmProcessCheck.check();

        if (userName.length() != 32 || latestVersion.length() != 32) {
            doCrash();
        }

        if (isPremium) {
            hwidUrl = ""; // TODO 验证服务器
            latestVersionUrl = ""; // TODO 最新版本信息服务器
        } else {
            hwidUrl = "not premium";
            latestVersionUrl = "not premium";
        }

        try {
            HwidUtils.hwid = AESEncode.encode(getHwid(), ENCODE_KEY);
        } catch (Exception e) {
            doCrash();
        }
    }

   // 抑制所有警告
 @SuppressWarnings("all")
    public void doCrash() {
        // 执行垃圾回收
        Runtime.getRuntime().gc();
        // 设置随机硬件ID
        HwidUtils.hwid = GenRandomString.generateRandomString();
        // 设置随机用户名
        userName = GenRandomString.generateRandomString();
        // 设置随机版本号
        latestVersion = GenRandomString.generateRandomString();
        // 设置是否为VIP
        isPremium = false;
        // 设置随机IP地址
        hwidUrl = GenFakeIP.generateRandomIP(4);
        latestVersionUrl = GenFakeIP.generateRandomIP(4);
        // 设置加密密钥
        ENCODE_KEY = "FuckYouCracker";
        // 执行垃圾回收
        Runtime.getRuntime().gc();
        try {
            // 执行崩溃
            crash();
            // 再次执行崩溃
            doCrash();
        } catch (Exception e) {
            // 执行崩溃
            crash();
            // 再次执行崩溃
            doCrash();
        }
    }

    public static void main(String[] args) {
        instance.init();
        System.out.println(HwidUtils.hwid);
    }
}
