package com.company;

public class IsTripReader implements Runnable {
    private Graph graph;

    public IsTripReader(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void run() {
        boolean isTrip = false;
        String from = "Kharkiv";
        String to = "Kyiv";
        isTrip = graph.isTrip(from, to);
        if(isTrip) {
            System.out.println("There is a trip between " + from + " and " + to);
        }
        else {
            System.out.println("There is no trip between " + from + " and " + to);
        }
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        isTrip = false;
        from = "Lviv";
        to = "Odesa";
        isTrip = graph.isTrip(from, to);
        if(isTrip) {
            System.out.println("There is a trip between " + from + " and " + to);
        }
        else {
            System.out.println("There is no trip between " + from + " and " + to);
        }
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        isTrip = false;
        from = "Ternopil";
        to = "Kyiv";
        isTrip = graph.isTrip(from, to);
        if(isTrip) {
            System.out.println("There is a trip between " + from + " and " + to);
        }
        else {
            System.out.println("There is no trip between " + from + " and " + to);
        }
        graph.printGraph();
    }
}
