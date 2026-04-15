package com.parkinglot.service;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manages scene navigation for the application.
 */
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, Supplier<Scene>> sceneBuilders = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public void register(String name, Supplier<Scene> builder) {
        sceneBuilders.put(name, builder);
    }

    public void navigateTo(String name) {
        Supplier<Scene> builder = sceneBuilders.get(name);
        if (builder == null) throw new IllegalArgumentException("No scene registered: " + name);
        Scene scene = builder.get();
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
