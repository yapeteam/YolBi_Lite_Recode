package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.impl.combat.AntiBot;
import cn.yapeteam.yolbi.module.impl.combat.Target;
import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TargetManager implements IMinecraft {
    private static Target targetModule = null;

    public static List<Entity> getTargets(double range) {
        if (targetModule == null)
            targetModule = YolBi.instance.getModuleManager().getModule(Target.class);
        return mc.theWorld.loadedEntityList.stream()
                .filter(
                        entity -> (targetModule.getPlayers().getValue() && entity instanceof EntityPlayer) ||
                                (targetModule.getAnimals().getValue() && entity instanceof EntityAnimal) ||
                                (targetModule.getMobs().getValue() && entity instanceof EntityMob) ||
                                (targetModule.getVillagers().getValue() && entity instanceof EntityVillager)
                )
                // not ourselves
                .filter(entity -> entity != mc.thePlayer)
                // no dead entities
                .filter(Entity::isEntityAlive)
                // must be in distance
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range)
                // not bots
                .filter(entity -> !BotManager.bots.contains(entity) && YolBi.instance.getModuleManager().getModule(AntiBot.class).isEnabled())
                // sort by distance
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                .collect(Collectors.toList());
    }
}
