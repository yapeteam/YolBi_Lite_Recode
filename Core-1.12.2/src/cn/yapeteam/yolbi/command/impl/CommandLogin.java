package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.oauth.login.MicrosoftLogin;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.command.AbstractCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class CommandLogin extends AbstractCommand {

    public CommandLogin() {
        super("login");
    }

    private static final Field session;

    static {
        try {
            session = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "session", null, Mapper.Type.Field));
            session.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        MicrosoftLogin.setUpdateStatusConsumer(AbstractCommand::printMessage);
    }

    private Thread loginThread = null;

    @Override
    public void process(String[] args) {
        if (loginThread == null) {
            loginThread = new Thread(() -> {
                try {
                    if (new MicrosoftLogin().login().login((name, uuid, access_token, refresh_token) -> {
                        try {
                            printMessage(name);
                            printMessage(uuid.toString().replace("-", ""));
                            printMessage(access_token);
                            session.set(Minecraft.getMinecraft(), new Session(name, uuid.toString().replace("-", ""), access_token, "msa"));
                        } catch (IllegalAccessException e) {
                            Logger.exception(e);
                        }
                    })) {
                        printMessage("Login Successfully");
                    }
                } catch (Exception e) {
                    Logger.exception(e);
                }
                loginThread = null;
            });
            loginThread.start();
        }
    }
}
