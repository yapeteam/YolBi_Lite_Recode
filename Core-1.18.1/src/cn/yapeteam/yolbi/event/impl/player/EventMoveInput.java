package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventMoveInput extends Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
}
