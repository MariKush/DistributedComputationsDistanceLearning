package main_package;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

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
