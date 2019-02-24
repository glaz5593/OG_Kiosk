package il.co.activeview.og_kiosk.request;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import il.co.activeview.og_kiosk.Json;

/**
 * Created by moshe on 17/02/2019.
 */

public class RequestHash {
    public RequestHash() {
        map = new HashMap<>();
    }

    public HashMap<UUID, Request> map;

    public void addRequest(Request request) {
        synchronized (map) {
            if (request == null || request.uid == null || map.containsKey(request.uid)) {
                return;
            }
            map.put(request.uid, request);
        }
    }

    public RequestPackage getRequestPackage(int targetId) {
        RequestPackage res=new RequestPackage();
        synchronized(map) {
            for (Request request:map.values()){
                if(request.target.id== targetId){
                    res.requests.add(request);
                }
            }
        }
        return  res;
    }
}
