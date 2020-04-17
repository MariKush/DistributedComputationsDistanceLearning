package main

import (
	"fmt"
	"math/rand"
	"time"
)

const SMOKERS_NUM = 3


func smoker(component int, semaphoreForSmoking chan bool, semaphoreForDealing chan bool, table* []bool){
	for{
		<-semaphoreForSmoking
		fmt.Println("Checking table #", component,"...")
		if !(*table)[component]{

			fmt.Println("🚬 #", component,"...")
			time.Sleep(time.Second)
			for i := range *table{
				(*table)[i] = false
			}
			semaphoreForDealing<-true
		}
	}
}

func middle(semaphoreForSmoking chan bool, semaphoreForDealing chan bool, table* []bool){
	for {
		<- semaphoreForDealing
		fmt.Println("New items")
		var first, second= getCigaretteStuff()
		(*table)[first] = true
		(*table)[second] = true
		for i := 0; i < SMOKERS_NUM; i++ {
			semaphoreForSmoking <- true
		}
	}
}

func getCigaretteStuff() (int, int) {
	stuff1 := rand.Intn(3)
	stuff2 := rand.Intn(3);
	for stuff2 == stuff1 {
		stuff2 = rand.Intn(3);
	}

	return stuff1, stuff2;
}

func main(){

	var table = make([]bool, SMOKERS_NUM, SMOKERS_NUM)
	var semaphoreForSmoking = make(chan bool, 3)
	var semaphoreForDealing = make(chan bool, 1)
	semaphoreForDealing <- true
	go middle(semaphoreForSmoking, semaphoreForDealing, &table)
	for i := 0; i < SMOKERS_NUM; i++{
		go smoker(i, semaphoreForSmoking, semaphoreForDealing, &table)
	}
	fmt.Scanln();
}
