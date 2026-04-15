package com.parkinglot.ui;

import com.parkinglot.model.Account;
import com.parkinglot.service.DataStore;
import com.parkinglot.service.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen {

    public Scene build() {
        // Root
        StackPane root = new StackPane();
        root.setStyle(Styles.mainBackground());

        VBox card = new VBox(20);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(Styles.card() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 4);");
        card.setAlignment(Pos.CENTER);

        // Logo / Title
        Label icon = new Label("🅿");
        icon.setStyle("-fx-text-fill:" + Styles.ACCENT + "; -fx-font-size:48px;");

        Label title = new Label("Central City Parking");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        Label subtitle = new Label("Management System");
        subtitle.setStyle(Styles.labelMuted());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + Styles.BORDER + ";");

        // Form
        Label userLabel = new Label("Username");
        userLabel.setStyle(Styles.labelMuted());

        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setStyle(Styles.input());
        userField.setMaxWidth(Double.MAX_VALUE);

        Label passLabel = new Label("Password");
        passLabel.setStyle(Styles.labelMuted());

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setStyle(Styles.input());
        passField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:" + Styles.ERROR + "; -fx-font-size:12px;");
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle(Styles.accentButton());
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(Styles.accentButton()));

        Runnable doLogin = () -> {
            String user = userField.getText().trim();
            String pass = passField.getText();
            Account account = DataStore.getInstance().authenticate(user, pass);
            if (account != null) {
                DataStore.getInstance().setLoggedInUser(account);
                SceneManager.getInstance().navigateTo("dashboard");
            } else {
                errorLabel.setText("Invalid username or password.");
                errorLabel.setVisible(true);
                passField.clear();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        Label hint = new Label("Hint: admin/admin123  or  attendant/att123");
        hint.setStyle("-fx-text-fill:#555577; -fx-font-size:11px;");

        card.getChildren().addAll(icon, title, subtitle, sep,
                userLabel, userField, passLabel, passField,
                errorLabel, loginBtn, hint);

        root.getChildren().add(card);

        Scene scene = new Scene(root, 1280, 800);
        return scene;
    }
}
