package rs.hooloovoo.test.resources.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class Mapper {

    public static <T> T mapFromJSON(String json, Class<T> destination) {
        return new GsonBuilder().setLenient().create().fromJson(json, destination);
    }
    public static <T> List<T> mapFromJsonArray(String json, Class<T[]> destination) {
        return Arrays.asList(new Gson().fromJson(json, destination));
    }

}
