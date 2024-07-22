package cn.yapeteam.yolbi.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class PlayerUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static void sendMessage(String msg) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("\247b[Cloudy]\247r " + msg));
        }
    }
}
