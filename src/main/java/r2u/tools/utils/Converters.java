package r2u.tools.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Converters {
    public static ArrayList<String> convertObject2StringArrayList(List<Object> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        //Converto oggetti in stringhe
        for (Object object : list) {
            arrayList.add(object.toString());
        }
        return arrayList;
    }

    public static HashMap<String, Boolean> convertArrayList2HashMap(ArrayList<String> objectList) {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String object : objectList) {
            map.put(object.split("=")[0], Boolean.valueOf(object.split("=")[1]));
        }
        return map;
    }
}
