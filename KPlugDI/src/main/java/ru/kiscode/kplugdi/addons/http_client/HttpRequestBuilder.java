package ru.kiscode.kplugdi.addons.http_client;

import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

public class HttpRequestBuilder {

    public enum RequestMethod {
        GET, POST, PUT, DELETE
    }

    private HttpClient client;
    private String urlString;
    private RequestMethod method = RequestMethod.GET;
    private String contentType = "text/plain";
    private Object body;
    private Method callbackMethod;
    private Object callbackObject;
    private int priority = 0, connectTimeout = 5000, readTimeout = 5000, byteArrayLength = 32 * 1024;

    public HttpRequestBuilder(@NonNull HttpClient client) {
        this.client = client;
    }

    public HttpRequestBuilder setUrl(String urlString) {
        if (urlString == null) {
            throw new IllegalArgumentException("urlString cannot be null");
        }
        if (urlString.startsWith("/")) {
            urlString = urlString.substring(1);
        }
        if (urlString.endsWith("/")) {
            throw new IllegalArgumentException("urlString should not end with /");
        }
        this.urlString = client.getServer() + urlString;
        return this;
    }

    public HttpRequestBuilder setMethod(RequestMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }
        this.method = method;
        return this;
    }

    public HttpRequestBuilder setContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType cannot be null");
        }
        this.contentType = contentType;
        return this;
    }

    public HttpRequestBuilder setBody(Object body) {
        if (body == null) {
            throw new IllegalArgumentException("body cannot be null");
        }
        this.body = body;
        return this;
    }

    public HttpRequestBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public HttpRequestBuilder setConnectTimeout(int connectTimeout) {
        if (connectTimeout <= 0) {
            return this;
        }
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpRequestBuilder setReadTimeout(int readTimeout) {
        if (readTimeout <= 0) {
            return this;
        }
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpRequestBuilder setCallbackMethod(Method callbackMethod) {
        this.callbackMethod = callbackMethod;
        return this;
    }

    public HttpRequestBuilder setCallbackObject(Object callbackObject) {
        this.callbackObject = callbackObject;
        return this;
    }

    public HttpRequestBuilder setByteArrayLength(int byteArrayLength) {
        this.byteArrayLength = byteArrayLength;
        return this;
    }

    public void sentAsync() {
        client.sentRequestAsync(buildRequest());
    }

    public Object sentSync() {
        return client.sentRequestSync(buildRequest());
    }

    private HttpRequest buildRequest() {
        return HttpRequest
                .builder()
                .urlString(urlString)
                .method(method.name())
                .contentType(contentType)
                .body(objectToBytes(body))
                .callbackMethod(callbackMethod)
                .callbackObject(callbackObject)
                .priority(priority)
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .byteArrayLength(byteArrayLength)
                .build();
    }

    private byte[] objectToBytes(Object obj) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();

            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }


}
