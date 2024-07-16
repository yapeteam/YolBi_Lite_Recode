package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.AbstractCommand;
import cn.yapeteam.yolbi.module.Module;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class CommandBind extends AbstractCommand {
    public CommandBind() {
        super("bind");
    }

    @Override
    public void process(String[] args) {
        if (args.length == 2) {
            Module module = YolBi.instance.getModuleManager().getModuleByName(args[0]);
            if (module == null) {
                printMessage("Module not found " + args[0]);
                return;
            }
            int code = 0;
            try {
                for (Field field : Keyboard.class.getFields()) {
                    if (field.getName().startsWith("KEY_") && field.getName().substring(4).equalsIgnoreCase(args[1])) {
                        code = field.getInt(null);
                        break;
                    }
                }
            } catch (Exception e) {
                Logger.exception(e);
            }
            if (code == 0)
                printMessage("Key not found " + args[1]);
            module.setKey(code);
            printMessage("Bind " + module.getName() + " to Key " + code);
        }
    }
}
