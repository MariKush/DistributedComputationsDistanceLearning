package com.company;

public class ReadWriteLock {
    private int writerWait; //number of waiting writers
    private int readerActive; //number of active readers
    private int writerActive; //number of active writers

    public ReadWriteLock() {
        writerWait = 0;
        readerActive = 0;
        writerActive = 0;
    }

    synchronized private boolean startWrite2() {
        if (readerActive != 0) return false;
        writerWait--;
        writerActive = 1;
        notifyAll();
        return true;
    }

    public void startWrite() throws InterruptedException {
        synchronized (this) {
            writerWait++;
        }
        while (!startWrite2()) {
            synchronized (this) {
                this.wait(10);
            }
        }

    }

    synchronized public void endWrite() {
        writerActive = 0;
    }

    synchronized private boolean startRead2() {
        if (writerActive == 1 || writerWait > 0)
            return false;
        readerActive++;
        notifyAll();
        return true;
    }

    public void startRead() throws InterruptedException {
        while (!startRead2()) {
            synchronized (this) {
                this.wait(10);
            }
        }
    }

    synchronized public void endRead() {
        readerActive--;
    }
}
