package cn.yapeteam.yolbi.managers;

import cn.yapeteam.yolbi.utils.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TargetManager implements IMinecraft {
    public static List<Entity> getTargets(double range) {
        return mc.world.loadedEntityList.stream()
                // must be a player, not a sheep or somethin
                .filter(entity -> entity instanceof EntityPlayer)
                // not ourselfs
                .filter(entity -> entity != mc.player)
                // no dead entities
                .filter(Entity::isEntityAlive)
                // must be in distance
                .filter(entity -> mc.player.getDistanceToEntity(entity) <= range)
                // no bots
                .filter(entity -> BotManager.bots.contains(entity))
                // sort usin distance
                .sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceToEntity(entity)))
                // return a list
                .collect(Collectors.toList());
    }
}
