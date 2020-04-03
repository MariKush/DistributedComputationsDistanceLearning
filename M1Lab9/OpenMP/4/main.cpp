#include <iostream>
#include <omp.h>
#include <conio.h>
#include <intrin.h>
#include <iomanip>
#include <chrono>
#include <stdlib.h>
#include <ctime>
#include <math.h>

using namespace std;

const int DIMENSION = 3000;

void fill(int *A) {
	for (int i = 0; i < DIMENSION; i++)
		for (int j = 0; j < DIMENSION; j++)
			A[i * DIMENSION + j] = rand() % 5;
}

int main() {

	srand(time(NULL));

	int *A = new int[DIMENSION*DIMENSION];
	int *B = new int[DIMENSION*DIMENSION];
	int *C = new int[DIMENSION*DIMENSION];

	fill(A);
	fill(B);

	clock_t start_time = clock();

	int j, k;

#pragma omp parallel for private(j,k)

	for (int i = 0; i < DIMENSION; i++)
		for (k = 0; k < DIMENSION; k++) {
			int sum = 0;
			for (j = 0; j < DIMENSION; j++)
				sum += A[i * DIMENSION + j] * B[j * DIMENSION + k];
			C[i * DIMENSION + k] = sum;
		}

	std::cout << "DIMENSION [" << DIMENSION << "x" << DIMENSION << "]: " << int(clock() - start_time) << " ms" << endl;

	delete[] A;
	delete[] B;
	delete[] C;

	system("pause");

	return 0;
}