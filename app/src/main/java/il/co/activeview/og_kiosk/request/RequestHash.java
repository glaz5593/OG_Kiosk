package il.co.activeview.og_kiosk.request;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import il.co.activeview.og_kiosk.Json;
import il.co.activeview.og_kiosk.UUID_List;

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

    public void remove(UUID_List list) {
        synchronized(map) {
            for (UUID uid: list){
                if(map.containsKey(uid)){
                   map.remove(uid);
                }
            }
        }
    }
}
