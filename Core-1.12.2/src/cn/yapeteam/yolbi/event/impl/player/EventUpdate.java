package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventUpdate extends Event {
    public enum Type {
        Pre, Post
    }


    private final Type type;

    public boolean isPre() {
        return type == Type.Pre;
    }

    public boolean isPost() {
        return type == Type.Post;
    }
}