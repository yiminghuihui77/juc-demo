package com.minghui.juc.lock.impl;

import com.minghui.juc.lock.BlockingQueue;

/**
 * 自定义简易阻塞队列
 *
 * @author minghui.y
 * @create 2018-07-24 9:34
 **/
public class SimpleBlockingQueue<E> implements BlockingQueue<E> {

    /**
     * 静态内部类：定义链表的节点
     * @param <E>
     */
    static class Node<E> {

        E data;

        Node<E> next;

        Node(E e) {
            data = e;
        }

    }




    @Override
    public boolean put(E e) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
