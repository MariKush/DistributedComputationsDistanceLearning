package com.recruits;

public class CyclicBarrier {
    private int number;
    private Runnable runnable;
    private int arrived = 0;

    public CyclicBarrier(int number) {
        this.number = number;
    }

    public CyclicBarrier(int number, Runnable runnable) {
        this.number = number;
        this.runnable = runnable;
    }

    public synchronized void await() throws InterruptedException {
        if(arrived == number-1) {
            arrived = 0;
            runnable.run();
            notifyAll();
        }
        else {
            arrived++;
            wait();
        }
    }
}
