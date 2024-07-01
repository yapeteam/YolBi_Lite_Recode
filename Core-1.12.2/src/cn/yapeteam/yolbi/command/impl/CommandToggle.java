package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.command.AbstractCommand;
import cn.yapeteam.yolbi.module.Module;

public class CommandToggle extends AbstractCommand {
    public CommandToggle() {
        super("toggle");
    }

    @Override
    public void process(String[] args) {
        if (args.length == 1) {
            Module module = YolBi.instance.getModuleManager().getModuleByName(args[0]);
            if (module != null) {
                module.toggle();
                sendMessage("Toggled " + module.getName());
            } else sendMessage("Module not found " + args[0]);
        }
    }
}