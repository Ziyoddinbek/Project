package com.parkinglot;

import com.parkinglot.service.SceneManager;
import com.parkinglot.ui.DashboardScreen;
import com.parkinglot.ui.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sm = SceneManager.getInstance();
        sm.setPrimaryStage(primaryStage);

        sm.register("login",     () -> new LoginScreen().build());
        sm.register("dashboard", () -> new DashboardScreen().build());

        primaryStage.setTitle("Community 2.0 — Parking Management System");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        sm.navigateTo("login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
