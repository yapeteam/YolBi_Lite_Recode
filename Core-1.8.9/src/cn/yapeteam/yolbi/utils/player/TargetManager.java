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
        return mc.theWorld.loadedEntityList.stream()
                // must be a player, not a sheep or something else
                .filter(entity -> entity instanceof EntityPlayer)
                // not ourselves
                .filter(entity -> entity != mc.thePlayer)
                // no dead entities
                .filter(entity -> !BotManager.bots.contains(entity))
                // must be in distance
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range)
                .filter(entity -> !entity.isInvisibleToPlayer(mc.thePlayer))
                // sort using distance
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                // return a list
                .collect(Collectors.toList());
    }
}
