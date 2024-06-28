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

    protected static void sendMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }
}
