package cn.yapeteam.builder;

import java.util.stream.Stream;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public class ProcessBar {
    private static final char incomplete = '\u2591', complete = '\u2588';
    public final int total;
    private final StringBuilder builder;

    public ProcessBar(int total) {
        this.total = total;
        builder = new StringBuilder();
        Stream.generate(() -> incomplete).limit(total).forEach(builder::append);
    }

    public void update(int progress) {
        if (progress > 0)
            builder.replace(progress - 1, progress, String.valueOf(complete));
        String progressBar = "\r" + builder;
        String percent = " " + progress + "%";
        System.out.print(progressBar + percent);
        if (progress == total)
            System.out.println();
    }
}
