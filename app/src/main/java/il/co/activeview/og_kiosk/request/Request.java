package il.co.activeview.og_kiosk.request;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by משה on 17/02/2019.
 */

public class Request implements Serializable{
    public UUID uid;
    public RequestSource source;
    public RequestTarget target;
    public String data;
    public String module;
    public String function;
}
