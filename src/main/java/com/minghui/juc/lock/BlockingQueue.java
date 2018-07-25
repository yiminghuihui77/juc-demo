package com.minghui.juc.lock;

import java.util.Iterator;

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

    Iterator<E> iterator();
}
