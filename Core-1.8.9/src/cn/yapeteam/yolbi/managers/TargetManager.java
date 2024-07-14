package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.impl.combat.AntiBot;
import cn.yapeteam.yolbi.module.impl.combat.CombatSettings;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TargetManager implements IMinecraft {
    private static CombatSettings combatSettingsModule = null;
    private static AntiBot antiBotModule = null;

    public static List<Entity> getTargets(double range) {
        if (combatSettingsModule == null)
            combatSettingsModule = YolBi.instance.getModuleManager().getModule(CombatSettings.class);
        if (antiBotModule == null)
            antiBotModule = YolBi.instance.getModuleManager().getModule(AntiBot.class);
        return mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(
                        entity -> (combatSettingsModule.getPlayers().getValue() && entity instanceof EntityPlayer &&
                                !(combatSettingsModule.getNotTeamMates().getValue() && PlayerUtil.sameTeam((EntityPlayer) entity))) ||
                                (combatSettingsModule.getAnimals().getValue() && entity instanceof EntityAnimal) ||
                                (combatSettingsModule.getMobs().getValue() && entity instanceof EntityMob) ||
                                (combatSettingsModule.getVillagers().getValue() && entity instanceof EntityVillager)
                )
                // not ourselves
                .filter(entity -> entity != mc.thePlayer)
                // no dead entities
                .filter(Entity::isEntityAlive)
                // must be in distance
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range)
                // not bots
                .filter(entity -> !(antiBotModule.isEnabled() && BotManager.bots.contains(entity)))
                // sort by distance
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                .collect(Collectors.toList());
    }
}
