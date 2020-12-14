package rs.hooloovoo.test.resources.http;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {
    public Integer code = null;
    public String codeMessage = null;
    public String body = null;
    public Map<String, String> headers = new HashMap<String, String>();

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
