package com.company;

public class Writer implements Runnable {
    private Database db;

    public Writer(Database db) {
        this.db = db;
    }

    @Override
    public void run() {
        for(int i = 0; i < 3; ++i) {
            try {
                Thread.sleep(3000);
                db.write(new Data("Masha", 2507017));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
