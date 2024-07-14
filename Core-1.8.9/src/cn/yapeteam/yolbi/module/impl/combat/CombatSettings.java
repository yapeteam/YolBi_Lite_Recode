package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import lombok.Getter;

@Getter
public class CombatSettings extends Module {
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue notTeamMates = new BooleanValue("No Team Mates", players::getValue, true);
    private final BooleanValue animals = new BooleanValue("Animals", true);
    private final BooleanValue mobs = new BooleanValue("Mobs", true);
    private final BooleanValue villagers = new BooleanValue("Villagers", true);
    // for weapons
    private final BooleanValue rod = new BooleanValue("Rod as Weapon", true);
    private final BooleanValue axe = new BooleanValue("Axe as Weapon", false);
    private final BooleanValue stick = new BooleanValue("Stick as Weapon", false);

    public CombatSettings() {
        super("Combat Settings", ModuleCategory.COMBAT);
        addValues(players, notTeamMates, animals, mobs, villagers, rod, axe, stick);
    }

    @Override
    protected void onEnable() {
        setEnabled(false);
    }
}
