package shared.network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String action;
    private final Object[] params;

    public Request(String action, Object... params) {
        this.action = action;
        this.params = params;
    }

    public String getAction() {
        return action;
    }

    public Object getParam(int index) {
        return params[index];
    }
}
