package lab;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class Task extends RecursiveAction {

    //the dimension of the matrix
    private static final int DIMENSION = 3000;

    private int[] A, B, C;
    private int ID;

    Task(int[] a, int[] b, int[] c) {
        this(a, b, c, -100);
    }

    Task(int[] a, int[] b, int[] c, int id) {
        A = a;
        B = b;
        C = c;
        ID = id;
    }

    void multiplication(int[] a, int[] b, int[] c, int k) {
        for (int i = 0; i < DIMENSION; i++)
            for (int j = 0; j < DIMENSION; j++)
                C[k * DIMENSION + i] += A[k * DIMENSION + j] * B[j * DIMENSION + i];
    }

   public static int getDIMENSION(){
        return DIMENSION;
    }

    @Override
    protected void compute() {
        if (ID < 0) {
            ArrayList tasks = new ArrayList<Task>();
            for (int i = 0; i < DIMENSION; i++)
                tasks.add(new Task(A, B, C, i));
            invokeAll(tasks);
        } else multiplication(A, B, C, ID);
    }


}