package cn.yapeteam.ymixin.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StringUtil {
    //public static String[] split(@NotNull String str, String splitter) {
    //    if (!str.contains(splitter))
    //        return new String[]{};
    //    ArrayList<String> result = new ArrayList<>();
    //    StringBuilder stringBuilder = new StringBuilder();
    //    StringBuilder passed = new StringBuilder();
    //    for (int i = 0; i < str.length() - (splitter.length() - 1); i++) {
    //        StringBuilder sb = new StringBuilder();
    //        for (int j = i; j < i + splitter.length(); j++)
    //            sb.append(str.charAt(j));
    //        if (sb.toString().equals(splitter)) {
    //            result.add(stringBuilder.toString());
    //            passed.append(stringBuilder);
    //            passed.append(splitter);
    //            stringBuilder = new StringBuilder();
    //            i += splitter.length();
    //        }
    //        if (i < str.length() - 1)
    //            stringBuilder.append(str.charAt(i));
    //    }
    //    String last = str.replace(passed.toString(), "");
    //    if (!last.isEmpty())
    //        result.add(last);
    //    return result.toArray(new String[0]);
    //}

    public static String[] split(@NotNull String str, String splitter) {
        if (!str.contains(splitter))
            return new String[]{};

        ArrayList<String> result = new ArrayList<>();
        int startIndex = 0;
        int splitterLength = splitter.length();

        while (true) {
            int index = str.indexOf(splitter, startIndex);
            if (index == -1) {
                result.add(str.substring(startIndex));
                break;
            }
            result.add(str.substring(startIndex, index));
            startIndex = index + splitterLength;
        }

        return result.toArray(new String[0]);
    }
}
