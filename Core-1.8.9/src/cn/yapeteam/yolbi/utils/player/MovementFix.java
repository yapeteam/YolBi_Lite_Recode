package cn.yapeteam.yolbi.utils.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovementFix {
    OFF("Off"),
    NORMAL("Razer"),
    TRADITIONAL("Traditional"),
    BACKWARDS_SPRINT("Backwards Sprint");

    final String name;

    @Override
    public String toString() {
        return name;
    }
}
