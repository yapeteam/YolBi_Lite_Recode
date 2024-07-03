package cn.yapeteam.yolbi.antileak.check;

import cn.yapeteam.yolbi.antileak.AntiLeak;
import com.sun.tools.attach.VirtualMachine;

import java.util.Arrays;

public class JvmProcessCheck {
    // 定义一个字符串数组，用于存放可能泄露信息的进程名
  private static final String[] badProcess = {
            "dump", "dumper", "packetlog", "logger", "recaf", "jbyte", "bytecode", "decompile", "log"
    };

  // 检查是否有进程名包含可能泄露信息的字符串
  public static void check() {
      if (hasBadProcess()) {
          // 如果有，则调用AntiLeak的doCrash方法
          AntiLeak.instance.doCrash();
      }
  }

  // 检查是否有进程名包含可能泄露信息的字符串
  private static boolean hasBadProcess() {
      // 获取所有正在运行的进程信息
      return VirtualMachine.list().stream().anyMatch(descriptor -> {
          // 将进程名称转换为小写并去除首尾空格
          final String name = descriptor.displayName().toLowerCase().trim();

          // 检查进程名是否包含可能泄露信息的字符串
          return Arrays.stream(badProcess).anyMatch(name::contains);
      });
  }
}
