package ru.crew4dev.celllogger.db.converters;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedHashSet;
import java.util.Set;

import androidx.room.TypeConverter;

public class ArrayConverter {
    @TypeConverter
    public static Set<String> fromString(String value) {
        Set<String> listdata = new LinkedHashSet<>();
        try {
            JSONArray array = new JSONArray(value);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    listdata.add(array.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listdata;
    }

    @TypeConverter
    public static String fromArrayList(Set<String> list) {
        JSONArray array = new JSONArray();
        for (String item : list) {
            array.put(item);
        }
        return array.toString();
    }
}