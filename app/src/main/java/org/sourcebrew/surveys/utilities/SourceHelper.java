package org.sourcebrew.surveys.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by John on 12/23/2017.
 */

public class SourceHelper {

    public static JSONObject getRaw(Context context) {

            JSONObject jsonObject = null;
            InputStream in = context.getResources().openRawResource(R.raw.survey);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                StringBuilder b = new StringBuilder();
                String str = "";
                while ((str=reader.readLine()) != null) {
                    b.append(str);
                }
                jsonObject = new JSONObject(b.toString());
            } catch (IOException e) {
               // e.printStackTrace();
            } catch (JSONException e) {
               // e.printStackTrace();
            }
            return jsonObject;
    }

    public static JSONObject getObject(JSONObject parent, String value) {
        try {
            return parent.getJSONObject(value);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getArray(JSONObject parent, String value) {
        try {
            return parent.getJSONArray(value);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getArray(JSONArray json, int index) {
        try {
            return json.getJSONArray(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getArrayObject(JSONArray json, int index) {
        try {
            return json.getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getArrayObject(JSONObject json, String name, int index) {
        try {
            JSONArray obj = getArray(json, name);
            return obj.getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getString(JSONObject json, String value, String defaultValue) {
        String result = defaultValue;
        try {
            result = json.getString(value);
        } catch (JSONException e) {

        }
        return result;
    }

    public static String getString(JSONArray json, String value, int row) {
        String result = "";
        JSONObject obj = getArrayObject(json, row);
        if (obj != null)
            result = getString(obj, value, "");
        return result;
    }

    public static int getInt(JSONObject json, String value, int defaultValue) {
        int result = defaultValue;
        try {
            result = json.getInt(value);
        } catch (JSONException e) {

        }
        return result;
    }

    public static boolean isArray(JSONArray json){
        return json.length() != 0;
    }
}
