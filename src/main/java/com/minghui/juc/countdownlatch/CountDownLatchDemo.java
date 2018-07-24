package com.minghui.juc.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用CountDownLatch模拟一个学生考试流程
 *
 * @author minghui.y
 * @create 2018-07-23 21:46
 **/
public class CountDownLatchDemo {

    private static volatile AtomicInteger enterCount = new AtomicInteger(0);

    public static void main(String[] args) {

        //假设学生个数50人
        int studentNum = 50;
        CountDownLatch examBegin = new CountDownLatch(1);
        CountDownLatch examEnd = new CountDownLatch(studentNum);
        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(studentNum);
        //
        for (int i = 0; i < studentNum; i++) {
            threadPool.execute(() -> {
                //学生进入考场
                System.out.println("[" + Thread.currentThread().getName() + "]考生进入考场，已有" + (enterCount.addAndGet(1)) + "个考生在等待考试开始...");

                try {
                    //等考试始开始
                    examBegin.await();

                    //考试开始，考生答题
                    System.out.println("考试开始，考生" + Thread.currentThread().getName() + "正在答题...");
                    Thread.sleep((long) (Math.random() * 10000));
                    //考生交答卷
                    System.out.println("考生" + Thread.currentThread().getName() + "结束考试，交卷...");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    examEnd.countDown();
                }
            });
        }

        //主线程任务
        System.out.println("考官[" + Thread.currentThread().getName() +  "]等待考生入场，考试即将开始...");
        //死循环直到全部考生入场
        while (enterCount.get() != studentNum) {}
        //所有考生入场，考试开始
        System.out.println("所有考生已入场，考试开始，叮铃铃铃铃...");
        examBegin.countDown();

        //等待考试结束，即所有考生都交卷
        try {
            examEnd.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //考试结束
        System.out.println("考试结束，叮铃铃铃铃...");

    }

}
