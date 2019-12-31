package com.cinemaled.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * xml转json工具
 * created by fred
 * on 2019/12/6
 */
public class XmlParser {
    public static String xmlToJson(String response) {
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(response);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
        return jsonObj.toString();
    }
}
