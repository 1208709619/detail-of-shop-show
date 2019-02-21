package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单例线程池
 */
public class RequestProcessorThreadPool {

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);


    public RequestProcessorThreadPool(){
        RequestQueue requestQueue = RequestQueue.getInstance();

        for (int i=0;i<10;i++){
            ArrayBlockingQueue queue = new ArrayBlockingQueue<Request>(100);
            requestQueue.addQueue(queue);
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

    private static class Singleton{
        private static RequestProcessorThreadPool instance;

        static {
            instance = new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance(){
            return instance;
        }
    }

    public static RequestProcessorThreadPool getInstance(){
        return Singleton.getInstance();
    }

    public static void init(){
        getInstance();
    }
}
