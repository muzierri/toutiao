package com.nowcoder;

import jdk.nashorn.internal.ir.Block;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 15:30 2019/7/26
 * @Modified By
 */
class Producer implements Runnable {
    private BlockingQueue<String> q;

    public Producer(BlockingQueue q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + q.take());//q里没东西就卡着
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyThread extends Thread {
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);//1000ms
                System.out.println(String.format("T%d:%d", tid, i));
            }
//            Thread.sleep(10000);
//            System.out.println(String.format("T%d", tid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MultiThread {
    public static void testThread() {
        for (int i = 0; i < 10; i++) {
            MyThread myThread = new MyThread(i);
            //myThread.start();
        }

        for (int i = 0; i < 10; i++) {
            final int tid = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);//1000ms
                            System.out.println(String.format("T2%d:%d", tid, i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);//1000ms
                    System.out.println(String.format("T3%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);//1000ms
                    System.out.println(String.format("T4%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }


    public static void testBlockingQueue() {
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }

    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void sleep(int mills) {
        try {
            //Thread.sleep(new Random().nextInt(mills));
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建10条线程分别加10次
    public static void testWithAtomic() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        sleep(1000);
                        //incrementAndGet是原子方式，获得值增加1然后设置成新的值的操作不会被中断，保证了线程安全。
                        System.out.println(Thread.currentThread().getName()+":"+atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    public static void testWithOutAtomic() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        sleep(1000);
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }

    //测试原子性
    public static void testAtomic() {
        testWithAtomic();
        //testWithOutAtomic();
    }


    //线程池任务框架
    public static void testExecutor() {
        //ExecutorService service = Executors.newSingleThreadExecutor();//单线程
        ExecutorService service = Executors.newFixedThreadPool(2);//创建2条线程
        service.submit(new Runnable() {//提交任务
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Executor1: " + i);
                }
            }
        });
        service.submit(new Runnable() {//提交任务
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Executor2: " + i);
                }
            }
        });
        service.shutdown();//关闭

    }

    public static void testFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);
                return 1;//一秒后返回1
                //throw new IllegalArgumentException("异常");
            }
        });
        service.shutdown();

        try {
            //System.out.println(future.get());//阻塞的等待返回结果
            System.out.println(future.get(100,TimeUnit.MILLISECONDS));//100ms未获取则抛超时异常
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //main方法
    public static void main(String[] args) {
        //estThread();
        //testSynchronized();
        //testBlockingQueue();
        //testAtomic();
        testExecutor();
        //testFuture();
    }
}
