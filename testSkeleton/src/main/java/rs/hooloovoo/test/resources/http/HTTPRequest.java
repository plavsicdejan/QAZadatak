package rs.hooloovoo.test.resources.http;

import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HTTPRequest {
    private String url = "";
    private String method = HTTPMethod.GET.getValue();
    private String body = "";
    private String charset = "UTF-8";
    private Map<String,String> headers = new HashMap<String, String>();
    private class HttpDeleteWithBody extends HttpPost {

        public HttpDeleteWithBody(String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public HTTPRequest setMethod(HTTPMethod method) {
        this.method = method.getValue();
        return this;
    }

    public String getBody() {
        return body;
    }

    public HTTPRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> geRequestHeaders() {
        return headers;
    }

    public void setRequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public HTTPRequest setContentType(String value) {
        this.headers.put("Content-Type", value);
        return this;
    }
    public HTTPRequest(String url)  {
        this.url = url;
    }
    private void addHeaderFields(HttpUriRequest request) {
        Set<String> keys = this.headers.keySet();
        for(String key:keys) request.setHeader(key, this.headers.get(key));
    }
    private HTTPResponse getHTTPResponse(HttpResponse res) throws Exception {
        HTTPResponse response = new HTTPResponse();
        response.code =  res.getStatusLine().getStatusCode();
        response.codeMessage = res.getStatusLine().getReasonPhrase();
        //StringBuffer response_body = new StringBuffer();
        String body = "";
        if(res.getEntity()!= null) {
            InputStreamReader isr = new InputStreamReader(res.getEntity().getContent(), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            // Next two lines should be replaced with third line
            //String line = "";
            //while((line = br.readLine()) != null) response_body.append(line);
            body = br.lines().collect(Collectors.joining("\n"));
        }
        response.body = body;
        //response.body = response_body.toString();
        Header caching = res.getFirstHeader("Cache-Control");
//        if(caching != null) response.cache = caching.getValue();
        for(Header header:res.getAllHeaders()) response.headers.put(header.getName(), header.getValue());
        return response;
    }

    public HTTPResponse sendRequest() throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse res = null;

        if(this.method.equals(HTTPMethod.GET.getValue())) {
            HttpGet request = new HttpGet(this.url);
            this.addHeaderFields(request);
            res = client.execute(request);
        }

        if(this.method.equals(HTTPMethod.POST.getValue())) {
            HttpPost request = new HttpPost(this.url);
            this.addHeaderFields(request);
            request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName(this.charset))));
            res = client.execute(request);
        }

        if(this.method.equals(HTTPMethod.PUT.getValue())) {
            HttpPut request = new HttpPut(this.url);
            this.addHeaderFields(request);
            request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName(this.charset))));
            res = client.execute(request);
        }

        if(this.method.equals(HTTPMethod.DELETE.getValue())) {
            HttpDeleteWithBody request = new HttpDeleteWithBody(this.url);
            this.addHeaderFields(request);
            request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName(this.charset))));
            res = client.execute(request);
        }
        return this.getHTTPResponse(res);
    }
}
