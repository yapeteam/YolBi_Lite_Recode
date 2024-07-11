package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import lombok.Getter;

@Getter
public class Target extends Module {
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue animals = new BooleanValue("Animals", true);
    private final BooleanValue mobs = new BooleanValue("Mobs", true);
    private final BooleanValue villagers = new BooleanValue("Villagers", true);

    public Target() {
        super("Target", ModuleCategory.COMBAT);
        addValues(players, animals, mobs, villagers);
    }

    @Override
    protected void onEnable() {
        setEnabled(false);
    }
}
