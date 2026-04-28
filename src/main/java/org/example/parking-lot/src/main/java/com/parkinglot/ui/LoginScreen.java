package com.parkinglot.ui;

import com.parkinglot.model.Account;
import com.parkinglot.model.Location;
import com.parkinglot.model.Person;
import com.parkinglot.model.accounts.ParkingAttendant;
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
        // ── Full-page root: left branding panel + right form panel ────────────
        HBox root = new HBox();
        root.setStyle(Styles.mainBackground());

        // ── Left branding panel ───────────────────────────────────────────────
        VBox leftPanel = new VBox(24);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(60));
        leftPanel.setStyle("-fx-background-color:" + Styles.ACCENT + ";");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxWidth(Double.MAX_VALUE);

        // C2 logo — text badge
        Label bigIcon = new Label("C2");
        bigIcon.setStyle(
                "-fx-text-fill:white;" +
                " -fx-font-size:72px;" +
                " -fx-font-weight:bold;" +
                " -fx-background-color:rgba(0,0,0,0.25);" +
                " -fx-background-radius:18;" +
                " -fx-padding:8 20 8 20;"
        );

        Label brandTitle = new Label("Community 2.0");
        brandTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        brandTitle.setStyle("-fx-text-fill:white; -fx-text-alignment:center;");
        brandTitle.setAlignment(Pos.CENTER);

        Label brandSub = new Label("Parking Management System");
        brandSub.setStyle("-fx-text-fill:rgba(255,255,255,0.75); -fx-font-size:16px;");

        Separator brandSep = new Separator();
        brandSep.setStyle("-fx-background-color:rgba(255,255,255,0.25);");
        brandSep.setMaxWidth(200);

        Label tagline = new Label("Fast · Reliable · Smart");
        tagline.setStyle("-fx-text-fill:rgba(255,255,255,0.55); -fx-font-size:13px;");

        leftPanel.getChildren().addAll(bigIcon, brandTitle, brandSub, brandSep, tagline);

        // ── Right form panel ──────────────────────────────────────────────────
        VBox rightPanel = new VBox(0);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setStyle(Styles.mainBackground());
        rightPanel.setPrefWidth(480);
        rightPanel.setMinWidth(420);
        rightPanel.setMaxWidth(520);

        VBox formBox = buildLoginForm();
        formBox.setMaxWidth(380);
        rightPanel.getChildren().add(formBox);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 1280, 800);
        return scene;
    }

    // ── Login Form ────────────────────────────────────────────────────────────

    private VBox buildLoginForm() {
        VBox form = new VBox(16);
        form.setPadding(new Insets(48, 40, 48, 40));
        form.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Welcome back");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle(Styles.labelMuted());

        Region gap = new Region();
        gap.setPrefHeight(8);

        Label userLabel = new Label("Username");
        userLabel.setStyle(Styles.labelMuted());

        TextField userField = new TextField();
        userField.setPromptText("Enter your username");
        userField.setStyle(Styles.input());
        userField.setMaxWidth(Double.MAX_VALUE);

        Label passLabel = new Label("Password");
        passLabel.setStyle(Styles.labelMuted());

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setStyle(Styles.input());
        passField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:" + Styles.ERROR + "; -fx-font-size:12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle(Styles.accentButton() + " -fx-font-size:14px; -fx-padding:12 0;");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                Styles.button(Styles.ACCENT_HOVER, "white") + " -fx-font-size:14px; -fx-padding:12 0;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                Styles.accentButton() + " -fx-font-size:14px; -fx-padding:12 0;"));

        Separator orSep = new Separator();
        orSep.setStyle("-fx-background-color:" + Styles.BORDER + ";");

        Label orLabel = new Label("Don't have an account?");
        orLabel.setStyle(Styles.labelMuted() + " -fx-font-size:12px;");
        orLabel.setAlignment(Pos.CENTER);
        orLabel.setMaxWidth(Double.MAX_VALUE);

        Button registerBtn = new Button("Create New Account");
        registerBtn.setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT) +
                " -fx-font-size:13px; -fx-padding:10 0; -fx-border-color:" + Styles.ACCENT +
                "; -fx-border-radius:6; -fx-border-width:1;");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(
                Styles.button(Styles.BG_INPUT, Styles.TEXT) +
                " -fx-font-size:13px; -fx-padding:10 0;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(
                Styles.button(Styles.BG_CARD, Styles.TEXT) +
                " -fx-font-size:13px; -fx-padding:10 0; -fx-border-color:" + Styles.ACCENT +
                "; -fx-border-radius:6; -fx-border-width:1;"));
        registerBtn.setOnAction(e -> showRegistrationDialog());

        Runnable doLogin = () -> {
            String user = userField.getText().trim();
            String pass = passField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter username and password.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            Account account = DataStore.getInstance().authenticate(user, pass);
            if (account != null) {
                DataStore.getInstance().setLoggedInUser(account);
                SceneManager.getInstance().navigateTo("dashboard");
            } else {
                errorLabel.setText("Invalid username or password.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                passField.clear();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        form.getChildren().addAll(
                title, subtitle, gap,
                userLabel, userField,
                passLabel, passField,
                errorLabel,
                loginBtn,
                orSep,
                orLabel,
                registerBtn
        );
        return form;
    }

    // ── Registration Dialog ───────────────────────────────────────────────────

    private void showRegistrationDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText(null);

        DialogPane dp = dialog.getDialogPane();
        dp.setStyle(Styles.mainBackground());
        dp.setPrefWidth(500);

        // ── Header ────────────────────────────────────────────────────────────
        Label header = new Label("Create New Account");
        header.setFont(Font.font("System", FontWeight.BOLD, 20));
        header.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        Label subHeader = new Label("Fill in your details to register as a Parking Attendant");
        subHeader.setStyle(Styles.labelMuted());

        // ── Fields ────────────────────────────────────────────────────────────
        TextField fullNameField = styledField("e.g. John Smith");
        TextField usernameField = styledField("Choose a username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");
        passwordField.setStyle(Styles.input());
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Repeat your password");
        confirmPassField.setStyle(Styles.input());
        TextField phoneField = styledField("e.g. +1 555 0100");
        TextField emailField = styledField("e.g. john@example.com");
        TextField dobField   = styledField("DD/MM/YYYY");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill:" + Styles.ERROR + "; -fx-font-size:12px;");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        // ── Form grid ─────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 0, 8, 0));

        grid.add(fieldLabel("Full Name *"),      0, 0); grid.add(fullNameField,  1, 0);
        grid.add(fieldLabel("Username *"),       0, 1); grid.add(usernameField,  1, 1);
        grid.add(fieldLabel("Password *"),       0, 2); grid.add(passwordField,  1, 2);
        grid.add(fieldLabel("Confirm Password *"), 0, 3); grid.add(confirmPassField, 1, 3);
        grid.add(fieldLabel("Phone Number *"),   0, 4); grid.add(phoneField,     1, 4);
        grid.add(fieldLabel("Email"),            0, 5); grid.add(emailField,     1, 5);
        grid.add(fieldLabel("Date of Birth *"),  0, 6); grid.add(dobField,       1, 6);
        grid.add(statusLabel,                    0, 7, 2, 1);

        ColumnConstraints col0 = new ColumnConstraints(150);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        VBox content = new VBox(10, header, subHeader, new Separator(), grid);
        content.setPadding(new Insets(20));
        dp.setContent(content);

        // ── Buttons ───────────────────────────────────────────────────────────
        ButtonType registerBtnType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(registerBtnType, ButtonType.CANCEL);
        dp.lookupButton(ButtonType.CANCEL).setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT));

        Button okBtn = (Button) dp.lookupButton(registerBtnType);
        okBtn.setStyle(Styles.accentButton());
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            event.consume();

            String fullName   = fullNameField.getText().trim();
            String username   = usernameField.getText().trim();
            String password   = passwordField.getText();
            String confirmPass = confirmPassField.getText();
            String phone      = phoneField.getText().trim();
            String email      = emailField.getText().trim();
            String dob        = dobField.getText().trim();

            // Validation
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()
                    || phone.isEmpty() || dob.isEmpty()) {
                showStatus(statusLabel, "Please fill in all required fields (*).", Styles.ERROR);
                return;
            }
            if (!password.equals(confirmPass)) {
                showStatus(statusLabel, "Passwords do not match.", Styles.ERROR);
                return;
            }
            if (password.length() < 6) {
                showStatus(statusLabel, "Password must be at least 6 characters.", Styles.ERROR);
                return;
            }
            if (!dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
                showStatus(statusLabel, "Date of birth must be in DD/MM/YYYY format.", Styles.ERROR);
                return;
            }
            boolean exists = DataStore.getInstance().getAccounts().stream()
                    .anyMatch(a -> a.getUserName().equalsIgnoreCase(username));
            if (exists) {
                showStatus(statusLabel, "Username already taken. Choose another.", Styles.ERROR);
                return;
            }

            // Create account
            Location loc = new Location("N/A", "N/A", "N/A", "N/A", "N/A");
            Person person = new Person(fullName + " (DOB: " + dob + ")", loc, email, phone);
            ParkingAttendant attendant = new ParkingAttendant(username, password, person);
            DataStore.getInstance().addAccount(attendant);

            showStatus(statusLabel,
                    "✅ Account created! You can now log in as \"" + username + "\".",
                    Styles.SUCCESS);
            okBtn.setText("Close");
            // Clear fields
            fullNameField.clear(); usernameField.clear();
            passwordField.clear(); confirmPassField.clear();
            phoneField.clear(); emailField.clear(); dobField.clear();

            okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev2 -> dialog.close());
        });

        dialog.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(Styles.input());
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle(Styles.labelMuted());
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private void showStatus(Label label, String msg, String color) {
        label.setText(msg);
        label.setStyle("-fx-text-fill:" + color + "; -fx-font-size:12px;");
        label.setVisible(true);
        label.setManaged(true);
    }
}
