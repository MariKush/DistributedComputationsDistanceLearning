package lab;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private Random rand = new Random();;

    private final int threadsNumber = 4;

    //filling the matrix with random values
    public void fill(int[] A) {
        for (int i = 0; i < Task.getDIMENSION() * Task.getDIMENSION(); i++)
            A[i] = rand.nextInt(5);
    }

    public Main() {
        int[] A = new int[Task.getDIMENSION() * Task.getDIMENSION()];
        int[] B = new int[Task.getDIMENSION() * Task.getDIMENSION()];
        int[] C = new int[Task.getDIMENSION() * Task.getDIMENSION()];
        fill(A);
        fill(B);
        long time = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadsNumber);
        forkJoinPool.invoke(new Task(A, B, C));
        System.out.println("DIMENSION [" + Task.getDIMENSION() + " x " + Task.getDIMENSION() + "]: "  + (System.currentTimeMillis() - time) + " ms");
    }

    public static void main(String[] args) {
        new Main();
    }
}