package com.minghui.juc.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore测试用例
 *
 * 代表有个计数信号量，可控制某个资源最多同时被访问的线程个数
 *
 * 设置为1：就是让线程按顺序访问
 *
 * @author minghui.y
 * @create 2018-07-23 17:57
 **/
public class SemaphoreDemo {

    public static void main(String[] args) {

        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        //创建技术信号量
        Semaphore semaphore = new Semaphore(3, true);

        //创建5个任务
        for (int i = 0; i < 5; i++) {
            threadPool.execute(() -> {
                try {
                    //获取许可
                    semaphore.acquire();

                    //执行任务
                    System.out.println(Thread.currentThread().getName() + "]占用资源，已有" + (3 - semaphore.availablePermits()) + "个线程持有许可..");

                    //休眠随机秒数
                    try {
                        Thread.sleep((long)(Math.random() * 10000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //释放许可
                    semaphore.release();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
