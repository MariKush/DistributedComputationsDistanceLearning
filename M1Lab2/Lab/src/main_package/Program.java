package main_package;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Program{
    static volatile ArrayList<Fighter> fighters = new ArrayList<>();

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 150; i++) {
            Fighter fighter = new Fighter(i, random.nextInt()%100);
            fighters.add(fighter);
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Challenge challenge = new Challenge(0,fighters.size());
        int winnerID = forkJoinPool.invoke(challenge);
        System.out.println("The winner is " + winnerID);
    }
}
