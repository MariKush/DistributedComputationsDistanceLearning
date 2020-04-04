using System;
using System.Diagnostics;
using System.Threading.Tasks;

class MultiplyMatrices
{


    #region Helper_Methods
    static int[,] fill(int rows, int cols)
    {
        int[,] matrix = new int[rows, cols];

        Random r = new Random();
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                matrix[i, j] = r.Next(5);
            }
        }
        return matrix;
    }
    #endregion

    #region Parallel_Loop
    static void Multiply(int[,] A, int[,] B, int[,] C)
    {
        int ACols = A.GetLength(1);
        int BCols = B.GetLength(1);
        int ARows = A.GetLength(0);


        Parallel.For(0, ARows, i =>
        {
           
            for (int j = 0; j < BCols; j++)
            {
                int t = 0;
                for (int k = 0; k < ACols; k++)
                {
                    t += A[i, k] * B[k, j];
                }
                C[i, j] = t;
            }
        });
    }
    #endregion


    #region Main
    static void Main(string[] args)
    {
        
        unsafe
        {

        }
        int DIMENSION = 3000;
       

        int[,] A = fill(DIMENSION, DIMENSION);
        int[,] B = fill(DIMENSION, DIMENSION);
        int[,] C = new int[DIMENSION, DIMENSION];

        Stopwatch time = new Stopwatch();
        time.Start();
        Multiply(A, B, C);
        time.Stop();
        Console.WriteLine("DIMENSION [{0}x{0}]: {1} ms", DIMENSION, time.ElapsedMilliseconds);
        Console.ReadLine();
    }
    #endregion

}