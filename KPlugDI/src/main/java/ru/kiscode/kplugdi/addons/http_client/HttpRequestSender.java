package ru.kiscode.kplugdi.addons.http_client;

import lombok.NoArgsConstructor;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.bean.BeanScope;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@NoArgsConstructor
public class HttpRequestSender {

    private int byteArrayLength;

    public HttpRequestSender(int byteArrayLength) {
        this.byteArrayLength = byteArrayLength;
    }

    public Object sentRequest(HttpRequest request) {
        Object response = null;

        try {
            URL url = new URL(request.getUrlString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setConnectTimeout(request.getConnectTimeout());
            connection.setReadTimeout(request.getReadTimeout());

            if (request.getMethod().equalsIgnoreCase("POST")) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", request.getContentType());
                if (request.getBody() != null) {
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(request.getBody());
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            response = parseResponse(connection);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private Object parseResponse(HttpURLConnection connection) {

        Object response;

        String contentType = connection.getHeaderField("Content-Type");

        try {
            if (contentType != null && contentType.startsWith("text")) {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String inputLine;

                while ((inputLine = inputReader.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }

                inputReader.close();
                response = responseBuilder.toString();
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                response = byteArrayOutputStream.toByteArray();
            }

            return response;
        }catch (IOException e) {
            return null;
        }
    }

}
