package cn.yapeteam.yolbi.utils.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class Vector2f {
    public float x, y;

    public float angle(Vector2f nextPos) {
        float dx = nextPos.x - x;
        float dy = nextPos.y - y;
        return (float) Math.atan2(dy, dx);
    }

    public float distance(Vector2f nextPos) {
        float dx = nextPos.x - x;
        float dy = nextPos.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
