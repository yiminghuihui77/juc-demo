package com.minghui.juc.lock.impl;

import com.minghui.juc.lock.BlockingQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    /**
     * 链表头节点
     */
    private Node<E> head;

    /**
     * 链表尾节点
     */
    private Node<E> end;

    /**
     * 链表容量
     */
    private final int capacity;

    private AtomicInteger count = new AtomicInteger(0);

    /**
     * 入队锁
     */
    private ReentrantLock putLock = new ReentrantLock();

    /**
     * 非满条件
     */
    private Condition notFull = putLock.newCondition();

    /**
     * 出队锁
     */
    private ReentrantLock takeLock = new ReentrantLock();

    /**
     * 非空条件
     */
    private Condition notEmpty = takeLock.newCondition();

    /**
     * 构造方法
     * 默认容量Integer.MAX_VALUE
     */
    public SimpleBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public SimpleBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
//        head = end = new Node<>(null);
    }

    /**
     * 入队
     * @param node
     */
    private void enqueue(Node<E> node) {
        //第一次入队
        if (count.get() == 0) {
            head = end = node;
            return;
        }
       end = end.next = node;
    }

    /**
     * 出队
     * @return
     */
    private E dequeue() {
        Node<E> h = head;
        head = head.next;
        h.next = null;
        return h.data;
    }

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            takeLock.unlock();
        }
    }

    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            putLock.unlock();
        }

    }

    void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }

    void fullyUnlock() {
        takeLock.unlock();
        putLock.unlock();
    }


    /**
     * 阻塞的入队
     * @param e
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean put(E e) throws InterruptedException {
        if (e == null) {
            throw new IllegalArgumentException("不能插入空值");
        }
        //创建新节点
        Node<E> newNode = new Node<>(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        int c = -1;
        //加锁
        putLock.lock();
        try {
            //当队列满了就让当前线程阻塞
            while (count.get() == capacity) {
                notFull.await();
            }
            //入队
            enqueue(newNode);
            //计数值自增，返回自增前的值
            c = count.getAndIncrement();
            //自增后若没有达到容量值，则唤醒
            if ((c + 1) < capacity) {
                notFull.signal();
            }

        } finally {
            putLock.unlock();
        }
        //第一次插入，说明当前已经不为空
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public E take() throws InterruptedException {
        final ReentrantLock takeLock = this.takeLock;
        final AtomicInteger count = this.count;
        int c = -1;
        E data;
        //加锁
        takeLock.lock();
        try {
            //判断是否为空，为空则阻塞
            while (count.get() == 0) {
                notEmpty.await();
            }
            data = dequeue();
            c = count.getAndDecrement();
            //判断取出后是否不为空
            if (c > 1) {
                notEmpty.signal();
            }

        } finally {
            takeLock.unlock();
        }
        //取出前达到容量值，说明取出后未达到，唤醒
        if (c == capacity) {
            signalNotFull();
        }
        return data;
    }

    /**
     * 链表节点数量
     * @return
     */
    @Override
    public int getSize() {
        return count.get();
    }

    /**
     * 迭代器模式，返回一个迭代器
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new itr();
    }

    private class itr implements Iterator<E>{

        private Node<E> currentNode;
        private Node<E> lastNode;
        private E currentData;

        itr() {
            fullyLock();
            try {
                currentNode = head;
                if (head != null) {
                    currentData = head.data;
                }
            } finally {
                fullyUnlock();
            }
        }


        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public E next() {
            fullyLock();
            try {
                //判断当前节点是否为空
                if (currentNode == null) {
                    throw new NoSuchElementException();
                }
                lastNode = currentNode;
                E data = currentData;
                currentNode = currentNode.next;
                currentData = currentNode == null ? null : currentNode.data;
                return data;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public void remove() {

        }


    }


}
