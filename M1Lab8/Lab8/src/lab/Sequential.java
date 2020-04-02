package lab;

import mpi.MPI;

// Послідовний алгоритм
public class Sequential {
    public static void calculate(String[] args, int dimension) {
        /*
        Будь-яка прикладна MPI-програма починається з виклику функції ініціалізації MPI: функції MPI.Init. В результаті
        виконання цієї функції створюється група процесів, в яку поміщаються всі процеси-додатки, і створюється область
        зв'язку, що описується визначеним комунікатором MPI.COMM_WORLD. Ця область зв'язку об'єднує всі процеси-додатки.
        */
        MPI.Init(args);

        // Номер процесу
        int processNumber = MPI.COMM_WORLD.Rank();

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");
        long time = 0L;

        // Початкове наповнення виконується процесом номер 0
        if (processNumber == 0) {
            A.fillRandom(5);
            B.fillRandom(5);
            time = System.currentTimeMillis();
        }

        // Перемноження
        for (int i = 0; i < A.width; i++) {
            for (int j = 0; j < B.height; j++) {
                for (int k = 0; k < A.height; k++) {
                    C.matrix[i * A.width + j] += A.matrix[i * A.width + k] * B.matrix[k * B.width + j];
                }
            }
        }

        // Повідомлення результатів
        if (processNumber == 0) {
            System.out.print("Sequential algorithm [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms");
        }

        MPI.Finalize();
    }
}