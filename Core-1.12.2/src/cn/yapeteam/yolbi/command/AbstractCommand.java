package cn.yapeteam.yolbi.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

@Getter
@AllArgsConstructor
public abstract class AbstractCommand {
    private final String key;

    public abstract void process(String[] args);

    public static void printMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }
}
