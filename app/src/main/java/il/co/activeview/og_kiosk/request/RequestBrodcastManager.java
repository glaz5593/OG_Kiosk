package il.co.activeview.og_kiosk.request;

/**
 * Created by משה on 17/02/2019.
 */

public class RequestBrodcastManager {
    public final static int TYPE_REQUEST = 1;
    public final static int TYPE_GET_REQUEST = 2;
    public final static int TYPE_UID_ARRAY = 3;
    public final static int TYPE_ASK_UID = 4;
    public final static int TYPE_HAS_UID = 5;

    public final static String EXTRA_REQUEST = "Request";
    public final static String EXTRA_REQUEST_UID = "RequestUid";
    public final static String EXTRA_UID_ARRAY = "UidArray";
    public final static String EXTRA_TARGET_ID = "TargetId";

    public final static String Action_REQUEST = "il.co.activeview.serverservice.REQUEST";
    public final static String Action_GET_REQUEST_List = "il.co.activeview.serverservice.GET_REQUEST_List";
    public final static String Action_UID_ARRAY = "il.co.activeview.serverservice.UID_ARRAY";
    public final static String Action_ASK_UID = "il.co.activeview.serverservice.ASK_UID";
    public final static String Action_HAS_UID = "il.co.activeview.serverservice.HAS_UID";

    static RequestBrodcastManager mInstance;
    public RequestHash hash;
    public static RequestBrodcastManager getInstance() {
        if(mInstance==null){
            mInstance=new RequestBrodcastManager();
        }

        return  mInstance;
    }

    private RequestBrodcastManager(){
        hash =new RequestHash();
    }
}
