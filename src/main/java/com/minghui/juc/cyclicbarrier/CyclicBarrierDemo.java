package com.minghui.juc.cyclicbarrier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用CyclicBarrier模拟考试流程
 *
 * @author minghui.y
 * @create 2018-07-23 22:33
 **/
public class CyclicBarrierDemo {

    private static volatile AtomicInteger  enterCount = new AtomicInteger(0);

    public static void main(String[] args) {

        int studentNum = 50;

        ExecutorService threadPool = Executors.newFixedThreadPool(studentNum + 1);
        //同步工具
        CountDownLatch examBegin = new CountDownLatch(1);
        CyclicBarrier examEnd = new CyclicBarrier(studentNum, () -> {
           //最后一个到达屏障点的线程执行一个共有的任务
           System.out.println(Thread.currentThread().getName() + "]最后一个到达屏障点，开始执行共有任务...");
           try {
               Thread.sleep((long) (Math.random() * 10000));
           } catch (Exception e) {
               e.printStackTrace();
           }
            System.out.println(Thread.currentThread().getName() + "]最后一个到达屏障点，共有任务执行结束...");
        });

        for (int i = 0; i < studentNum; i++) {
            threadPool.execute( () -> {
                System.out.println(Thread.currentThread().getName() + "考生进入考场，已有" + (enterCount.addAndGet(1)) + "个考生正在等待考试开始...");
                try {
                    examBegin.await();

                    //考试开始答题
                    System.out.println("考试开始，" + Thread.currentThread().getName() + "考生正在答题...");
                    Thread.sleep((long)(Math.random() * 10000));
                    System.out.println(Thread.currentThread().getName() + "考生交卷，等待结束考试...");
                    examEnd.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        //主线程任务
        System.out.println(Thread.currentThread().getName() + "主考官等待考生入场，考试即将开始...");
        while (enterCount.get() != studentNum) {}
        //所有考生已入场，考试开始
        System.out.println("所有考生都已入场，考试开始，叮铃铃铃...");
        examBegin.countDown();
        //主线程执行等待
        try {
            examEnd.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);

    }
}
