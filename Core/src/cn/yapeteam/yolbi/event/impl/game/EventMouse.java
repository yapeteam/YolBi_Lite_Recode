package cn.yapeteam.yolbi.event.impl.game;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventMouse extends Event {
    private final int button;
    private final boolean pressed;
}
