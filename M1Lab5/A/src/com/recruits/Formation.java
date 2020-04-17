package com.recruits;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

public class Formation {
    private enum Side {
        L, R
    }
    private Side[] formation;
    private final int soldiersNum;
    private boolean stable;
    private final CyclicBarrier barrier;

    public Formation(int soldiersNum, int threadNum) {
        this.barrier = new CyclicBarrier(threadNum,
                ()-> {stable = isStable();
                print();
        });
        this.soldiersNum = soldiersNum;
        formation = new Side[soldiersNum];
        for(int i = 0; i < soldiersNum; ++i) {
            int side = new Random().nextInt(2);
            if (side == 0) {
                formation[i] = Side.L;
            }
            else {
                formation[i] = Side.R;
            }
        }
    }

    public boolean getStable() {
        return this.stable;
    }

    public boolean isStable() {
        for(int i = 0; i < soldiersNum - 2; ++i) {
            if(formation[i] == Side.R && formation[i+1] == Side.L) {
                return false;
            }
        }
        return true;
    }

    private void rotate(int i, int j) {
        Side iSide = formation[i];
        formation[i] = formation[j];
        formation[j] = iSide;
    }

    private boolean lookAtEachOther(int left, int right) {
        return formation[left] == Side.R && formation[right] == Side.L;
    }

    public void checkFormation(int from, int to) throws BrokenBarrierException, InterruptedException {
        if(from > 0) {
            synchronized (formation) {
                if (lookAtEachOther(from - 1, from)) {
                    rotate(from - 1, from);
                }
            }
        }
        for(int i = from; i < to; ++i) {
            if(lookAtEachOther(i, i+1)) {
                rotate(i, i+1);
            }
        }
        if(to < soldiersNum-1) {
            synchronized (formation) {
                if (lookAtEachOther(to, to+1)) {
                    rotate(to, to+1);
                }
            }
        }
        barrier.await();
    }

    public void print() {
        System.out.println(Arrays.toString(formation));
    }
}
