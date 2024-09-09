package ru.kiscode.kplugdi.addons.http_client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HttpRequest {

    private String urlString;
    private String method;
    private String contentType;
    private byte[] body;
    private Method callbackMethod;
    private Object callbackObject;
    private int priority;
    private int readTimeout;
    private int connectTimeout;
    private int byteArrayLength = 32 * 1024;
}
