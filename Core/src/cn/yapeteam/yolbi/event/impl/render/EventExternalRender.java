package cn.yapeteam.yolbi.event.impl.render;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class EventExternalRender extends Event {
    private final Graphics graphics;
}
