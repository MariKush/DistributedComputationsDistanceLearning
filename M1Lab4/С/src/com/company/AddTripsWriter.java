package com.company;

public class AddTripsWriter implements Runnable {
    private Graph graph;

    public AddTripsWriter(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void run() {
        graph.addCities("Kyiv");
        graph.addCities("Lutsk");
        graph.addCities("Odesa");
        graph.addCities("Lviv");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        graph.addCities("Ternopil");
        graph.addCities("Kharkiv");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
