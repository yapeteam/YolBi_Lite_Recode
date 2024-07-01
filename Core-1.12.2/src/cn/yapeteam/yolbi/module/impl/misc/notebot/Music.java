package cn.yapeteam.yolbi.module.impl.misc.notebot;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Setter
@Getter
public class Music {
    String name;
    ArrayList<MusicNote> notes;

    public Music() {
    }

    public Music(String name, ArrayList<MusicNote> notes) {
        this.name = name;
        this.notes = notes;
    }
}