package ru.kiscode.kplugdi.addons.http_client;


import ru.kiscode.kplugdi.annotations.Bean;
import ru.kiscode.kplugdi.annotations.BeanConfiguration;

@BeanConfiguration
public class HttpClientConfigurator {

    /**
     * Стандартный http клиент для загрузки аддонов.
     * @return клиент
     */
    @Bean
    public HttpClient defaultHttpClient() {
        return new HttpClient("http://localhost:8080/", 5);
    }

}
