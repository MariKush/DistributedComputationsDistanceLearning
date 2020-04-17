package task_b;

import javafx.application.Platform;

public class MyThread extends Thread {
    private boolean up;
    private boolean close = false;

    MyThread(boolean up) {
        this.up = up;
    }

    @Override
    public void run() {
        if (Program.semaphore.get() != 0) SetStateText("USED");
        while (Program.semaphore.get() != 0) {
        }
        if(Program.semaphore.compareAndSet(0,1)){
            SetStateText("USED");
            while (!close) {
                synchronized (Program.main_slider) {
                    if (up) {
                        if (Program.main_slider.getValue() < Program.main_slider.getMax())
                            Program.main_slider.setValue(Program.main_slider.getValue() + 1);
                    } else {
                        if (Program.main_slider.getValue() > Program.main_slider.getMin())
                            Program.main_slider.setValue(Program.main_slider.getValue() - 1);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            Program.semaphore.set(0);
            SetStateText("FREE");
        }

    }

    private void SetStateText(String text) {
        Platform.runLater(() -> Program.semaphore_state.setText(text));
    }

    public void MyInterrupt() {
        close = true;
    }
}