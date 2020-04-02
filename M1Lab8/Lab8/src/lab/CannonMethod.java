package lab;

import mpi.Cartcomm;
import mpi.MPI;

import java.util.Arrays;

// Метод Кенона
public class CannonMethod {
    // Координати поточного потоку в декартовій решітці потоків
    private static int[] gridCoords = new int[2];

    // Комунікатор для рядків потоків
    private static Cartcomm ColComm;

    // Комунікатор для ствпчиків потоків
    private static Cartcomm RowComm;

    // Розподілення блоків між потоками
    private static void matrixScatter(int[] matrix, int[] matrixBlock, int dimension, int blockSize) {
        int[] matrixRow = new int[blockSize * dimension];
        if (gridCoords[1] == 0)
            ColComm.Scatter(matrix, 0, blockSize * dimension, MPI.INT, matrixRow, 0, blockSize * dimension, MPI.INT, 0);
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(matrixRow, i * dimension, matrixRow.length);
            int[] subRowRes = new int[blockSize];

            RowComm.Scatter(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, matrixBlock, i * blockSize, blockSize);
        }
    }

    public static void calculate(String[] args, int dimension) {
        MPI.Init(args);

        int processNumber = MPI.COMM_WORLD.Rank();
        int threadsNumber = MPI.COMM_WORLD.Size();
        int gridSize = (int) Math.sqrt(threadsNumber);

        if (threadsNumber != gridSize * gridSize) {
            if (processNumber == 0)
                System.out.println("Cannon's method [" + dimension + "x" + dimension + "]: Number of processes must be a perfect square");
            MPI.Finalize();
            return;
        }

        // Комунікатор для декартової решітки потоків
        Cartcomm gridComm;

        // Розмір блоку
        int blockSize = dimension / gridSize;

        // Виділення кожному з потоків місця для зберігання блоків з кожної матриці
        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");

        int[] BlockA = new int[blockSize * blockSize];
        int[] BlockB = new int[blockSize * blockSize];
        int[] BlockC = new int[blockSize * blockSize];

        long time = 0L;

        if (processNumber == 0) {
            A.fillRandom(5);
            B.fillRandom(5);
            time = System.currentTimeMillis();
        }

        // Потреба у фіксації виміру решітки потоків
        boolean[] subdims = new boolean[2];

        /*
        Створення комунікатора COMM_CART з декартовою топологією з процесів комунікатора COMM_WORLD. Перший параметр
        задає розмірність одержуваної декартовой решітки, другий - це логічний масив, що визначає, чи є решітка
        періодичною (значення false) уздовж кожного виміру. reorder - логічний параметр, що визначає, що при значенні
        true системі дозволено змінювати порядок нумерації потоків для оптимізації розподілу потоків по фізичним
        процесорам використовуваного паралельного комп'ютера.
        */
        gridComm = MPI.COMM_WORLD.Create_cart(new int[]{gridSize, gridSize}, new boolean[]{false, false}, true);

        // Визначення декартових координат для кожного потоку
        gridCoords = gridComm.Coords(processNumber);

        // Створення комунікаторів для кожного з рядків решітки потоків
        subdims[1] = true;
        RowComm = gridComm.Sub(subdims);

        // Створення комунікаторів для кожного зі стовпчиків решітки потоків
        subdims[0] = true;
        subdims[1] = false;
        ColComm = gridComm.Sub(subdims);

        // Розподілення задач для потоків декартової решітки
        matrixScatter(A.matrix, BlockA, dimension, blockSize);
        matrixScatter(B.matrix, BlockB, dimension, blockSize);

        /*
        Для кожного рядка і декартової решітки потоків (крім першого рядка) виконується циклічний зсув блоків матриці A
        на (i - 1) позицій вліво (тобто в напрямку зменшення номерів стовпців).
        */
        if (gridCoords[0] != 0) {
            int nextProcess = gridCoords[1] - gridCoords[0];
            if (nextProcess < 0)
                nextProcess += gridSize;
            RowComm.Sendrecv_replace(BlockA, 0, blockSize * blockSize, MPI.INT, nextProcess, 0, MPI.ANY_SOURCE, 0);
        }

        /*
        Для кожного стовпця j декартової решітки потоків (крім першого стовпця) виконується циклічний зсув блоків
        матриці B на (j - 1) позицій вгору (тобто в напрямку зменшення номерів рядків).
        */
        if (gridCoords[1] != 0) {
            int nextProcess = gridCoords[0] - gridCoords[1];
            if (nextProcess < 0) nextProcess += gridSize;
            ColComm.Sendrecv_replace(BlockB, 0, blockSize * blockSize, MPI.INT, nextProcess, 1, MPI.ANY_SOURCE, 1);
        }

        // Встановлення бар'єру
        MPI.COMM_WORLD.Barrier();

        // Блоки матриць A і B, які містяться в процесі (i, j) перемножуються, і результат додається до матриці Сij
        for (int i = 0; i < blockSize; i++)
            for (int j = 0; j < blockSize; j++)
                for (int k = 0; k < blockSize; k++)
                    BlockC[i * blockSize + j] += BlockA[i * blockSize + k] * BlockB[k * blockSize + j];

        /*
        Для кожного рядка виконується циклічне пересилання блоків матриці A, які містяться в кожному потоці цього рядка,
        в напрямку зменшення номерів стовпців.
        */
        for (int iter = 0; iter < gridSize - 1; iter++) {
            int nextProcess = gridCoords[1] - 1;
            if (nextProcess < 0)
                nextProcess += gridSize;
            RowComm.Sendrecv_replace(BlockA, 0, blockSize, MPI.INT, nextProcess, 0, MPI.ANY_SOURCE, 0);

            nextProcess = gridCoords[0] - 1;
            if (nextProcess < 0)
                nextProcess += gridSize;

            /*
            Для кожного стовпця виконується циклічне пересилання блоків матриці B, які містяться в кожному потоці цього
            стовпця, в напрямку зменшення номерів рядків.
            */
            ColComm.Sendrecv_replace(BlockB, 0, blockSize, MPI.INT, nextProcess, 1, MPI.ANY_SOURCE, 1);

            for (int i = 0; i < blockSize; i++)
                for (int j = 0; j < blockSize; j++)
                    for (int k = 0; k < blockSize; k++)
                        BlockC[i * blockSize + j] += BlockA[i * blockSize + k] * BlockB[k * blockSize + j];
        }

        // Результат
        int[] resultRow = new int[dimension * blockSize];
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(BlockC, i * blockSize, BlockC.length);
            int[] subRowRes = new int[gridSize * blockSize];

            RowComm.Gather(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, resultRow, i * dimension, gridSize * blockSize);
        }

        if (gridCoords[1] == 0)
            ColComm.Gather(resultRow, 0, blockSize * dimension, MPI.INT, C.matrix, 0, blockSize * dimension, MPI.INT, 0);

        if (processNumber == 0) {
            System.out.print("Cannon's method [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms\n");
        }
        MPI.Finalize();
    }
}