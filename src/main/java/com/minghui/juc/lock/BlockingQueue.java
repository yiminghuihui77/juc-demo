package com.minghui.juc.lock;

/**
 * 阻塞队列接口
 *
 * @author minghui.y
 * @create 2018-07-24 9:29
 **/
public interface BlockingQueue<E> {

    boolean put(E e) throws InterruptedException;

    E take() throws InterruptedException;

    int getSize();

}
