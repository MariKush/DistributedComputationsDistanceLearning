package main

import (
	"fmt"
	"math/rand"
	"time"
)

const DIMENSION = 3000

func multiplication(i, n int, c chan int, A, B, C *[DIMENSION * DIMENSION]int) {
	for ; i < n; i++ {
		for j := 0; j < DIMENSION; j++ {
			sum := 0
			for k := 0; k < DIMENSION; k++ {
				sum += (*A)[i*DIMENSION+k] * (*B)[k*DIMENSION+j]
			}
			(*C)[i*DIMENSION+j] = sum
		}
	}
	c <- 1
}

func main() {
	c := make(chan int, DIMENSION)
	var A [DIMENSION * DIMENSION]int
	var B [DIMENSION * DIMENSION]int
	var C [DIMENSION * DIMENSION]int

	r := rand.New(rand.NewSource(99))
	for i := 0; i < DIMENSION*DIMENSION; i++ {
		A[i] = r.Intn(5)
		B[i] = r.Intn(5)
		C[i] = 0
	}
	startTime := time.Now()
	for i := 0; i < DIMENSION; i++ {
		go multiplication(i, i+1, c, &A, &B, &C)
	}
	for i := 0; i < DIMENSION; i++ {
		<-c
	}

	fmt.Print("DIMENSION [", DIMENSION,  "x", DIMENSION, "]: ", int(time.Now().Sub(startTime).Seconds()*1000), " ms")
}
