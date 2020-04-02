package lab;

import mpi.Cartcomm;
import mpi.MPI;

import java.util.Arrays;

// Алгоритм Фокса
public class FoxMethod {
    // Координати поточного потоку в декартовій решітці потоків
    private static int[] gridCoords = new int[2];

    // Комунікатор для рядків потоків
    private static Cartcomm ColComm;

    // Комунікатор для стовпчиків потоків
    private static Cartcomm RowComm;

    // Розподілення блоків між потоками
    private static void matrixScatter(int[] matrix, int[] matrixBlock, int dimension, int blockSize) {
        // Допоміжний масив, розрахований на кількість елементів у одному рядку блоків (потоків)
        int[] matrixRow = new int[blockSize * dimension];

        /*
        Спочатку матриця розділяється на горизонтальні смуги. Ці смуги розподіляються на процеси, що становлять
        нульовий стовпець решітки потоків. Далі кожна смуга розділяється на блоки між потоками, які складають рядки
        решітки потоків.
        */
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

        // Кількість блоків по горизонталі та вертикалі
        int gridSize = (int) Math.sqrt(threadsNumber);

        if (threadsNumber != gridSize * gridSize) {
            if (processNumber == 0)
                System.out.println("Fox's method[" + dimension + "x" + dimension + "]: Number of processes must be a perfect square");
            MPI.Finalize();
            return;
        }

        // Комунікатор для декартової решітки потоків
        Cartcomm gridComm;

        // Розмір блоку
        int blockSize = dimension / gridSize;

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");

        // Виділення кожному з потоків місця для зберігання блоків з кожної матриці та додаткового блоку матриці А
        int[] BlockA = new int[blockSize * blockSize];
        int[] BlockB = new int[blockSize * blockSize];
        int[] BlockC = new int[blockSize * blockSize];
        int[] tempBlockA = new int[blockSize * blockSize];

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

        // Визначення декартових координат для кожного процесу
        gridCoords = gridComm.Coords(processNumber);

        // Створення комунікаторів для кожного з рядків решітки потоків
        subdims[1] = true;
        RowComm = gridComm.Sub(subdims);

        // Створення комунікаторів для кожного зі стовпчиків решітки потоків
        subdims[0] = true;
        subdims[1] = false;
        ColComm = gridComm.Sub(subdims);

        // Розподілення задач для потоків декартової решітки
        matrixScatter(A.matrix, tempBlockA, dimension, blockSize);
        matrixScatter(B.matrix, BlockB, dimension, blockSize);

        // Паралельний алгоритм Фокса множення матриць
        for (int iter = 0; iter < gridSize; iter++) {
            // Передавання блоків матриці А по рядкам декартової решітки

            // Визначається індекс потоку, який повинен здійснити розсилання
            int pivot = (gridCoords[0] + iter) % gridSize;

            // Копіювання блоку матриці А, який передається іншим потокам
            if (gridCoords[1] == pivot)
                if (blockSize * blockSize >= 0)
                    System.arraycopy(tempBlockA, 0, BlockA, 0, blockSize * blockSize);

            /*
            Розсилання блоку вздовж рядка здійснюється за допомогою функції MPI_Bcast (використовується комунікатор
            RowComm, визначений для набору потоків кожного рядка решітки окремо).
            */
            RowComm.Bcast(BlockA, 0, blockSize * blockSize, MPI.INT, pivot);

            /*
            Отриманий в результаті пересилання блок матриці A і блок матриці B, який міститься в потоці (i, j),
            перемножуються, і результат додається до матриці Сij.
            */
            for (int i = 0; i < blockSize; i++)
                for (int j = 0; j < blockSize; j++)
                    for (int k = 0; k < blockSize; k++)
                        BlockC[i * blockSize + j] += BlockA[i * blockSize + k] * BlockB[k * blockSize + j];

            /*
            Циклічний зсув блоків матриці В вздовж стовпчиків декартової решітки потоків. Визначення сусідів згори та
            знизу для кожного процеса (визначається перша координата сусіда).
            */
            int nextProcess = gridCoords[0] + 1;
            if (gridCoords[0] == gridSize - 1)
                nextProcess = 0;
            int previousProcess = gridCoords[0] - 1;
            if (gridCoords[0] == 0)
                previousProcess = gridSize - 1;

            /*
            Кожен потік передає свій блок потоку того самого стовпчика решітки потоків з номером nextProcess та отримує
            блок від потоку того самого стовпчика решітки потоків з номером previousProcess.
            */
            ColComm.Sendrecv_replace(BlockB, 0, blockSize * blockSize, MPI.INT, nextProcess, 0, previousProcess, 0);
        }

        int[] resultRow = new int[dimension * blockSize];
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(BlockC, i * blockSize, BlockC.length);
            int[] subRowRes = new int[gridSize * blockSize];

            // Збирання блоків даних, які надсилаються усіма потоками групи RowComm, в один масив потоку з номером root
            RowComm.Gather(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            if (gridSize * blockSize >= 0)
                System.arraycopy(subRowRes, 0, resultRow, i * dimension, gridSize * blockSize);
        }

        if (gridCoords[1] == 0)
            ColComm.Gather(resultRow, 0, blockSize * dimension, MPI.INT, C.matrix, 0, blockSize * dimension, MPI.INT, 0);

        if (processNumber == 0) {
            System.out.print("Fox's method [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms");
        }
        MPI.Finalize();
    }
}