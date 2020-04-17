package com.company;


class Fighter {
    int energyTsy;
    private int count_of_victories = 0;

    Fighter(int i, int energyTsy) {
        this.energyTsy = energyTsy;
    }
    void goForward() {
        count_of_victories++;
    }
}