package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.managers.BotManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class TargetManager {
    public static List<Entity> getTargets(final double range) {
        return mc.world.loadedEntityList.stream()
                // must be a player, not a sheep or something else
                .filter(entity -> entity instanceof EntityPlayer)
                // not ourselves
                .filter(entity -> entity != mc.player)
                // no dead entities
                .filter(entity -> !BotManager.bots.contains(entity))
                // must be in distance
                .filter(entity -> mc.player.getDistanceToEntity(entity) <= range)
                .filter(entity -> !entity.isInvisibleToPlayer(mc.player))
                // sort using distance
                .sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceToEntity(entity)))
                // return a list
                .collect(Collectors.toList());
    }
}
