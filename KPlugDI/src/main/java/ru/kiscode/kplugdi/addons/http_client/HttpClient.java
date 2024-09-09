package ru.kiscode.kplugdi.addons.http_client;

import lombok.Getter;
import ru.kiscode.kplugdi.annotations.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Http клиент для общения с backend. Есть возможность использовать
 * его для работы с любыми другими серверами.
 */
@Component
public class HttpClient {

    @Getter
    private String server;
    private int threads;
    private Queue<HttpRequest> queue;
    private List<Thread> threadsList;

    public HttpClient() {
    }

    /**
     * Создать http клиент.
     * @param server домен сервра куда будите обращаться. (<a href="http://test.ru/">...</a>)
     * @param threads количество потоков для обработки ответов на асинхронные запросы (советую не более пяти)
     */
    public HttpClient(String server, int threads) {
        this.server = server;
        this.threads = threads;
        threadsList = new ArrayList<>();
        queue = new PriorityQueue<>(getDefaultComparator());
        startThreads();
    }

    /**
     * Создать http клиент.
     * @param server домен сервра куда будите обращаться. (<a href="http://test.ru/">...</a>)
     * @param threads количество потоков для обработки ответов на асинхронные запросы (советую не более пяти)
     * @param comparator компаратор для формирования приоритетной очереди, по дефолту приретет задаёт поле класса HttpRequest
     */
    public HttpClient(String server, int threads, Comparator<HttpRequest> comparator) {
        this.server = server;
        this.threads = threads;
        threadsList = new ArrayList<>();
        queue = new PriorityQueue<>(comparator);
        startThreads();
    }

    /**
     * Асинхронный запрос
     * @param request запрос
     */
    public void sentRequestAsync(HttpRequest request) {
        queue.add(request);
    }

    /**
     * Синхронный запрос с ответом
     * @param request запрос
     * @return ответ сервера
     */
    public Object sentRequestSync(HttpRequest request) {
        HttpRequestSender sender = new HttpRequestSender(request.getByteArrayLength());
        return sender.sentRequest(request);
    }

    private void executeMethods(Object result, HttpRequest request) {
        if (result == null) {
            return;
        }
        if (request.getCallbackMethod() == null || request.getCallbackObject() == null) {
            return;
        }
        try {
            request.getCallbackMethod().setAccessible(true);
            if (request.getCallbackMethod().getParameterTypes().length == 0) {
                request.getCallbackMethod().invoke(request.getCallbackObject());
            } else if (request.getCallbackMethod().getParameterTypes().length == 1) {
                request.getCallbackMethod().invoke(request.getCallbackObject(), result);
            } else if (request.getCallbackMethod().getParameterTypes().length == 2) {
                request.getCallbackMethod().invoke(request.getCallbackObject(), result, request);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void startThreads() {
        new Thread(() -> {
            while (true) {
                while (!queue.isEmpty()) {
                    if (threadsList.size() < threads) {
                        Thread thread = new Thread(() -> {
                            HttpRequest request = queue.poll();
                            Object response = sentRequestSync(Objects.requireNonNull(request));
                            executeMethods(response, request);
                            threadsList.remove(Thread.currentThread());
                        });
                        thread.start();
                        threadsList.add(thread);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Comparator<HttpRequest> getDefaultComparator() {
        return (o1, o2) -> o2.getPriority() - o1.getPriority();
    }

}
