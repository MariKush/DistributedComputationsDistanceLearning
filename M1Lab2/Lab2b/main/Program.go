package main

import (
	"fmt"
	"math/rand"
	"time"
)

type StolenProperty struct {
	index int
	price int
}

var chan1 chan StolenProperty = make(chan StolenProperty)
var chan2 chan StolenProperty = make(chan StolenProperty)

var r = rand.New(rand.NewSource(time.Now().UnixNano()))

func Ivanov() {
	i := 1
	for ; ; {
		stolenProperty := StolenProperty{i, 0}
		n := r.Intn(3000) + 1000
		time.Sleep(time.Duration(n) * time.Millisecond)
		stolenProperty.price = r.Intn(50)
		fmt.Println("Ivanov: steal ", stolenProperty)
		chan1 <- stolenProperty
		i = i + 1

	}
}

func Pertov() {
	for ; ; {
		stolenProperty := StolenProperty{0, 0}
		stolenProperty = <-chan1

		n := r.Intn(3000) + 2000
		time.Sleep(time.Duration(n) * time.Millisecond)

		fmt.Println("\t\tPetrov: transport ", stolenProperty)
		chan2 <- stolenProperty

	}
}

func Necheporchuk() {
	var sum = 0
	for ; ; {
		stolenProperty := <-chan2

		n := r.Intn(1000) + 500
		time.Sleep(time.Duration(n) * time.Millisecond)

		sum = sum + stolenProperty.price
		fmt.Println("\t\t\t\t\tNecheporchuk: calculated ", stolenProperty, "total price: ", sum)
	}
}

func main() {
	go Ivanov()
	go Pertov()
	go Necheporchuk()

	fmt.Scanln()

}
