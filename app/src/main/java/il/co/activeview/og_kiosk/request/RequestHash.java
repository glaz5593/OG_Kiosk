package il.co.activeview.og_kiosk.request;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by משה on 17/02/2019.
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

    public Request getRequest(UUID uid) {
        synchronized (map) {
            if (!map.containsKey(uid)) {
                return null;
            }
        }
        return map.get(uid);
    }

    public UUID[] getRequestsByTarget(int targetId){
        ArrayList<UUID> resList = new ArrayList<>();
        synchronized (map) {
             for (Request r : map.values()) {
                if (r.target.id == targetId) {
                    resList.add(r.uid);
                }
            }
        }

        return resList.toArray(new UUID[resList.size()]);
    }

    public Intent getRequestAsIntent(UUID uid) {
        Request r = getRequest(uid);
        if(r == null){
            return null;
        }

        Intent i=new Intent() ;
        i.putExtra(RequestBrodcastManager.EXTRA_REQUEST,r);
        return i;
    }

    public Request getRequestByIntent(Intent intent) {
        Request r = (Request) intent.getSerializableExtra(RequestBrodcastManager.EXTRA_REQUEST);
        return r;
    }

    public Intent getIntentForTarget(int targetId) {
        Intent i=new Intent() ;
        UUID[] array= getRequestsByTarget(targetId);
        if(array.length==0){
            return  null;
        }

        i.putExtra(RequestBrodcastManager.EXTRA_UID_ARRAY,array);
        return i;
    }


    public UUID[] getUidArrayByIntent(Intent intent) {
        UUID[] res = (UUID[]) intent.getSerializableExtra(RequestBrodcastManager.EXTRA_UID_ARRAY);
        return res;
    }
}
