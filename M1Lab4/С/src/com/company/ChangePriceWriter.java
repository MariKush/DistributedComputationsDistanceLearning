package com.company;


public class ChangePriceWriter implements Runnable {
    private Graph graph;

    public ChangePriceWriter(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void run() {
        graph.changePrice("Kyiv", "Lutsk", 8);
        graph.changePrice("Odesa", "Lviv", 16);
        graph.changePrice("Ternopil", "Kyiv", 21);
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        graph.changePrice("Kyiv", "Lviv", 10);

    }
}
