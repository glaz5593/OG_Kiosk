package il.co.activeview.og_kiosk.request;

import android.content.Intent;

import il.co.activeview.og_kiosk.Json;

/**
 * Created by משה on 17/02/2019.
 */

public class RequestBrodcastManager {

    public final static String EXTRA_REQUEST = "Request";
    public final static String EXTRA_REQUEST_PACK = "REQUEST_PACK";
    public final static String EXTRA_TARGET_ID = "TargetId";

    public final static String Action_ADD_REQUEST = "il.co.activeview.serverservice.ADD_REQUEST";
    public final static String Action_GET_REQUEST_List = "il.co.activeview.serverservice.GET_REQUEST_List";
    public final static String Action_REQUEST_PACK = "il.co.activeview.serverservice.Action_REQUEST_PACK";

    static RequestBrodcastManager mInstance;
    public RequestHash hash;

    public static RequestBrodcastManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestBrodcastManager();
        }

        return mInstance;
    }

    private RequestBrodcastManager() {
        hash = new RequestHash();
    }

    public Intent AskRequestList(int targetId) {
        Intent i = new Intent(Action_GET_REQUEST_List);
        i.putExtra(EXTRA_TARGET_ID, targetId);
        return i;
    }

    public Intent AddRequest(Request request) {
        Intent i = new Intent(Action_ADD_REQUEST);
        i.putExtra(EXTRA_TARGET_ID, Json.toString(request));
        return i;
    }
}
