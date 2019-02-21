package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 */
public class RequestProcessorThread implements Callable<Boolean> {

    /**
     * 自己监控的内存队列
     */
    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue){
        this.queue=queue;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            while (true){
                Request request = queue.take();

                System.out.println("==========日志=========；工作线程处理请求，商品id="+request.getProductId());
                request.process();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
