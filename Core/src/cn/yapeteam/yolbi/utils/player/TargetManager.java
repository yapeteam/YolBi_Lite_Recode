package cn.yapeteam.yolbi.utils.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.yapeteam.yolbi.utils.IMinecraft.mc;

public class TargetManager{
    public static List<Entity> getTargets(final double range) {
        return mc.theWorld.loadedEntityList.stream()
                // must be a player, not a sheep or something else
                .filter(entity -> entity instanceof EntityLivingBase)
                // not ourselves
                .filter(entity -> entity != mc.thePlayer)
                // no dead entities
                .filter(Entity::isEntityAlive)
                // must be in distance
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range)
                // sort using distance
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                // return a list
                .collect(Collectors.toList());
    }
}
