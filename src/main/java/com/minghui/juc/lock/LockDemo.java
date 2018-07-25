package com.minghui.juc.lock;

import com.minghui.juc.lock.impl.SimpleBlockingQueue;

import java.util.Iterator;

/**
 * ReentrantLock实现阻塞队列
 *
 * 特点：当队列已满或为空时的入队和出队操作会使得当前线程阻塞
 *
 * 参照：LinkedBlockingQueue
 *
 * @author minghui.y
 * @create 2018-07-24 8:41
 **/
public class LockDemo {

    public static void main(String[] args) throws Exception {

        //创建一个阻塞队列
        SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<>(10);

        //入队
        queue.put("1");

        queue.put("2");
        queue.put("3");
        queue.put("4");
        queue.put("5");
        queue.put("6");
        queue.put("7");
        queue.put("8");
        queue.put("9");
        queue.put("10");
        //取出队首
        System.out.println("取出队首" + queue.take());
        //执行这一条造成线程阻塞，因为队列已经满了
        queue.put("11");

        Iterator<String> iterator = queue.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
