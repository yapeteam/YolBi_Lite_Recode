package cn.yapeteam.yolbi.event.impl.render;

import net.minecraft.entity.EntityLivingBase;

public class EntityDamageEvent {
    private final EntityLivingBase attacker;
    private final EntityLivingBase target;

    public EntityDamageEvent(EntityLivingBase attacker, EntityLivingBase target) {
        this.attacker = attacker;
        this.target = target;
    }

    public EntityLivingBase getAttacker() {
        return attacker;
    }

    public EntityLivingBase getTarget() {
        return target;
    }
}
