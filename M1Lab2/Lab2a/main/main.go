package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

const (
	N int = 10
	M int = 15
	BEES = 3
)

func generateMatrix() [][]bool {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	matrix := make([][]bool, N)
	for row := range matrix {
		matrix[row] = make([]bool, M)
	}

	matrix[r.Intn(N)][r.Intn(M)] = true
	return matrix
}

func drawMatrix(matrix [][]bool) {
	for i := range matrix {
		for j := range matrix[i] {
			if matrix[i][j] {
				fmt.Print("🐾 ")
			} else {
				fmt.Print(" _ ")
			}
		}
		fmt.Println()
	}
}

func sectorToString(sector []bool) string {
	res := "|"
	for element := range sector {
		if sector[element] {
			res += "🐝 "
		} else {
			res += " _ "
		}
	}
	res += "|"
	return res
}

func findBear(id int,
	waitGroup *sync.WaitGroup,
	sectors <-chan []bool,
	isBearFound chan bool) {

	defer waitGroup.Done()

	for sector := range sectors {
		select {

		case <-isBearFound:
			isBearFound <- true
			return
		default:
			fmt.Println("Bees group ", id, " is searching in sector", sectorToString(sector))

			for index, element := range sector {
				time.Sleep(time.Microsecond * 100)
				if element {
					fmt.Println("Bees group ", id, " found bear in sector ",
						sectorToString(sector), " on ", index,
						"step.\nThe bear was punished!")
					fmt.Println("Bees group ", id, " returned")
					isBearFound <- true
					return
				}
			}
		}
		fmt.Println("Bees squad ", id, " returned")
	}
}

func main() {
	var matrix = generateMatrix()
	drawMatrix(matrix)
	sectors := make(chan []bool, N)
	isBearFound := make(chan bool, 1)

	var waitGroup sync.WaitGroup

	for i := 0; i < BEES; i++ {
		waitGroup.Add(1)
		go findBear(i, &waitGroup, sectors, isBearFound)
	}

	for i := 0; i < N; i++ {
		sectors <- matrix[i]
	}

	close(sectors)
	waitGroup.Wait()

}