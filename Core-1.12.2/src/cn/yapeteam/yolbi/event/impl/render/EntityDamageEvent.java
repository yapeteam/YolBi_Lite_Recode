package cn.yapeteam.yolbi.event.impl.render;

import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;

@Getter
public class EntityDamageEvent {
    private final EntityLivingBase attacker;
    private final EntityLivingBase target;

    public EntityDamageEvent(EntityLivingBase attacker, EntityLivingBase target) {
        this.attacker = attacker;
        this.target = target;
    }
}
