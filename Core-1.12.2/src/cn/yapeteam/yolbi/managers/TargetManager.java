package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.impl.combat.AntiBot;
import cn.yapeteam.yolbi.module.impl.combat.Target;
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
    private static Target targetModule = null;
    private static AntiBot antiBotModule = null;

    public static List<Entity> getTargets(double range) {
        if (targetModule == null)
            targetModule = YolBi.instance.getModuleManager().getModule(Target.class);
        if (antiBotModule == null)
            antiBotModule = YolBi.instance.getModuleManager().getModule(AntiBot.class);
        return mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(
                        entity -> (targetModule.getPlayers().getValue() && entity instanceof EntityPlayer &&
                                !(targetModule.getNotTeamMates().getValue() && PlayerUtil.sameTeam((EntityPlayer) entity))) ||
                                (targetModule.getAnimals().getValue() && entity instanceof EntityAnimal) ||
                                (targetModule.getMobs().getValue() && entity instanceof EntityMob) ||
                                (targetModule.getVillagers().getValue() && entity instanceof EntityVillager)
                )
                // not ourselves
                .filter(entity -> entity != mc.player)
                // no dead entities
                .filter(Entity::isEntityAlive)
                // must be in distance
                .filter(entity -> mc.player.getDistanceToEntity(entity) <= range)
                // not bots
                .filter(entity -> !(antiBotModule.isEnabled() && BotManager.bots.contains(entity)))
                // sort by distance
                .sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceToEntity(entity)))
                .collect(Collectors.toList());
    }
}
