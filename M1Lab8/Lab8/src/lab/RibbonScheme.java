package lab;

import mpi.MPI;

// Стрічкова схема
public class RibbonScheme {
    public static void calculate(String[] args, int dimension) {
        MPI.Init(args);

        // Номер процесу
        int processNumber = MPI.COMM_WORLD.Rank();

        // Кількість потоків
        int threadsNumber = MPI.COMM_WORLD.Size();

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");
        long time = 0L;

        if (processNumber == 0) {
            B.fillRandom(5);
            A.fillRandom(5);
            time = System.currentTimeMillis();
        }

        /*
        В даному алгоритмі матриці розбиваються на неперервні послідовності рядків (стрічки). Нижче кожен процес
        використовується для обчислення однієї стрічки результуючого добутку матриць A і B. В цьому випадку процес
        повинен мати доступ до відповідної стрічки матриці A і всієї матриці B. Оскільки одночасне зберігання всієї
        матриці B у всіх процесах паралельної програми вимагає надмірних витрат пам'яті, обчислення організовуються
        таким чином, щоб у кожен момент часу процеси містили лише частину елементів матриці B (одну стрічку), а доступ
        до решти забезпечувався б за допомогою передачі повідомлень.
        */
        int lineHeight = dimension /  threadsNumber;

        /*
        Розмір буфера відповідає загальному розміру матриці, розділеному на кількість потоків (наприклад, якщо матриця
        має розмірність 100 * 100, а потоків - 4, тоді кожен буфер міститиме (lineHeight = 100 / 4 = 25) 25 * 100 = 2500
        елементів. Кожному потоку виділяється по три буфера.
        */
        int[] bufferA = new int[lineHeight * dimension];
        int[] bufferB = new int[lineHeight * dimension];
        int[] bufferC = new int[lineHeight * dimension];

        /*
        Scatter розподіляє частини задачі по всіх потоках з комунікатора (якщо потоків 4, то кожному дістається четверта
        частина кожної матриці. Перший параметр - звідки розподіляються дані, другий - зсув, третій - скільки припаде на
        один потік, четвертий - тип даних (ціле), п'ятий - приймаючий буфер, далі - його розмір та тип даних, останній
        параметр - потік, що розподіляє дані.
        */
        MPI.COMM_WORLD.Scatter(A.matrix, 0, lineHeight * dimension, MPI.INT, bufferA, 0, lineHeight * dimension, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(B.matrix, 0, lineHeight * dimension, MPI.INT, bufferB, 0, lineHeight * dimension, MPI.INT, 0);

        // Для кожного потоку визначається його наступник та попередник для циклічного обміну даними
        int nextProcess = (processNumber + 1) %  threadsNumber;
        int previousProcess = processNumber - 1;
        if (previousProcess < 0)
            previousProcess =  threadsNumber - 1;
        int prevDataNum = processNumber;

        // Стрічкове перемноження матриць
        for (int p = 0; p <  threadsNumber; p++) {
            for (int i = 0; i < lineHeight; i++)
                for (int j = 0; j < dimension; j++)
                    for (int k = 0; k < lineHeight; k++)
                        /*
                        Виконується множення стрічки матриці A на стрічку матриці B, що містяться в даному потоці, і
                        результат записується у відповідний елемент стрічки с результуючої матриці.
                        */
                        bufferC[i * dimension + j] += bufferA[prevDataNum * lineHeight + i * dimension + k] * bufferB[k * dimension + j];
            prevDataNum -= 1;
            if (prevDataNum < 0)
                prevDataNum =  threadsNumber - 1;

            /*
            Виконується циклічне пересилання стрічки з матриці B у сусідні процеси (напрямок пересилки - за зростанням
            рангів процесів).
            */
            MPI.COMM_WORLD.Sendrecv_replace(bufferB, 0, lineHeight * dimension, MPI.INT, nextProcess, 0, previousProcess, 0);
        }

        /*
        Після завершення циклу в кожному процесі буде міститися стрічка с, рівна одній з стрічок добутку A і B.
        Залишається переслати їх головному процесу.
        */
        MPI.COMM_WORLD.Gather(bufferC, 0, lineHeight * dimension, MPI.INT, C.matrix, 0, lineHeight * dimension, MPI.INT, 0);

        if (processNumber == 0) {
            System.out.print("Ribbon scheme [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms");
        }
        MPI.Finalize();
    }
}