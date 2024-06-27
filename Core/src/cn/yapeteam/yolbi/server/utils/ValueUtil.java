package cn.yapeteam.yolbi.server.utils;

import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class ValueUtil {
    public static JsonArray getAllSubValuesAsJson(ModeValue<?> modeValue) {
        JsonArray jsonArray = new JsonArray();
        for (Object mode : modeValue.getModes())
            jsonArray.add(new JsonPrimitive(mode.toString()));
        return jsonArray;
    }
}
