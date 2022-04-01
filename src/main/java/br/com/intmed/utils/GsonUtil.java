package br.com.intmed.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class GsonUtil {
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create(); //objeto gson
    public static final JsonParser jsonParser = new JsonParser(); //objeto jsonParser
}