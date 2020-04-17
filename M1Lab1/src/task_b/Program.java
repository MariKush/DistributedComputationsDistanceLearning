package task_b;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;

public class Program extends Application implements EventHandler<Event> {
    public static void main(String[] args) {
        launch(args);
    }

    static volatile Slider main_slider;
    static volatile AtomicInteger semaphore = new AtomicInteger();
    static volatile Label semaphore_state;
    private MyThread thread1;
    private MyThread thread2;

    private Button thread1_start;
    private Button thread1_stop;
    private Button thread2_start;
    private Button thread2_stop;

    @Override
    public void start(Stage stage) {

        Label title = new Label("Slider");
        main_slider = new Slider();
        main_slider = new Slider(10, 90, 50);
        main_slider.setShowTickMarks(true);
        main_slider.setShowTickLabels(true);
        main_slider.setSnapToTicks(true);
        VBox top_box = new VBox();
        top_box.getChildren().addAll(title, main_slider);
        top_box.setAlignment(Pos.TOP_CENTER);


        thread1_start = new Button("Start 1");
        thread1_start.setOnAction(this::handle);
        thread1_stop = new Button("Stop 1");
        thread1_stop.setOnAction(this::handle);
        HBox thread_box1 = new HBox();
        thread_box1.getChildren().addAll( thread1_start, thread1_stop);
        thread_box1.setAlignment(Pos.TOP_CENTER);
        thread_box1.setSpacing(10);

        thread2_start = new Button("Start 2");
        thread2_start.setOnAction(this::handle);
        thread2_stop = new Button("Stop 2");
        thread2_stop.setOnAction(this::handle);
        HBox thread_box2 = new HBox();
        thread_box2.getChildren().addAll(thread2_start, thread2_stop);
        thread_box2.setAlignment(Pos.TOP_CENTER);
        thread_box2.setSpacing(10);

        semaphore_state = new Label("Semaphore state");
        thread1_stop.setDisable(true);
        thread2_stop.setDisable(true);

        VBox center_pane = new VBox();
        center_pane.getChildren().addAll(semaphore_state, thread_box1, thread_box2);
        center_pane.setAlignment(Pos.TOP_CENTER);
        center_pane.setSpacing(10);

        BorderPane main_pane = new BorderPane();
        main_pane.setTop(top_box);
        main_pane.setCenter(center_pane);

        Scene scene = new Scene(main_pane, 400, 150);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handle(Event event) {
        if (event.getSource() == thread1_start) {
            thread1 = new MyThread(true);
            thread1.setDaemon(true);
            thread1.setPriority(Thread.MIN_PRIORITY);
            thread1.start();
            thread1_start.setDisable(true);
            thread1_stop.setDisable(false);
        } else if (event.getSource() == thread1_stop) {
            thread1.MyInterrupt();
            thread1_start.setDisable(false);
            thread1_stop.setDisable(true);
        } else if (event.getSource() == thread2_start) {
            thread2 = new MyThread(false);
            thread2.setDaemon(true);
            thread2.setPriority(Thread.MAX_PRIORITY);
            thread2.start();
            thread2_stop.setDisable(false);
            thread2_start.setDisable(true);
        } else if (event.getSource() == thread2_stop) {
            thread2.MyInterrupt();
            thread2_start.setDisable(false);
            thread2_stop.setDisable(true);
        }
    }
}
