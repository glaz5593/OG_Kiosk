package il.co.activeview.og_kiosk;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by משה on 20/02/2019.
 */

public class Json {
    public static String toString(Object value){
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson g = gsonBuilder.create();
            return  g.toJson(value);
        }catch (Exception ex){
            Log.e("Json","failed to parse to json string" + value,ex);
            return "";
        }
    }

    public static <T> T toObject(String responseString, Class<T> valueType)  {
        if(responseString == null || responseString.length() == 0){
            return null;
        }

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson g = gsonBuilder.create();
            return  g.fromJson(responseString,valueType);
        }catch (Exception e){
            Log .e("Json","failed to parse to :"+valueType.getClass().getName() + "/n" +responseString+ "/n",e);
        }

        return null;
    }
}
