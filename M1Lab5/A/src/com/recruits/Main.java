package com.recruits;

public class Main {
    public static void main(String[] args) {
        final int threadNum = 4;
        final Formation formation = new Formation(240, threadNum);
        for(int i = 0; i < threadNum; ++i) {
            new Thread(new Worker(formation, i*60, (i+1)*60 - 1)).start();
        }
    }
}
