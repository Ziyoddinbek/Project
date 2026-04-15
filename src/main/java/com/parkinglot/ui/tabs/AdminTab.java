package com.parkinglot.ui.tabs;

import com.parkinglot.model.*;
import com.parkinglot.model.accounts.ParkingAttendant;
import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.model.spots.*;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AdminTab {

    private Runnable refreshCallback;

    public Tab build(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
        Tab tab = new Tab("Admin");
        tab.setClosable(false);

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        tab.setContent(scroll);
        return tab;
    }

    private VBox buildContent() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(24));
        root.setStyle(Styles.mainBackground());

        Label pageTitle = new Label("ADMIN PANEL");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        pageTitle.setStyle("-fx-text-fill:" + Styles.ACCENT + ";");

        root.getChildren().addAll(
                pageTitle,
                buildFloorManagement(),
                buildSpotManagement(),
                buildRateManagement(),
                buildAttendantManagement()
        );
        return root;
    }

    // ── Floor Management ──────────────────────────────────────────────────────

    private VBox buildFloorManagement() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());

        Label title = sectionTitle("🏢 Floor Management");

        TextField nameField = new TextField();
        nameField.setPromptText("Floor name (e.g. Floor-4)");
        nameField.setStyle(Styles.input());

        Button addBtn = new Button("Add Floor");
        addBtn.setStyle(Styles.accentButton());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(Styles.accentButton()));

        Button removeBtn = new Button("Remove Floor");
        removeBtn.setStyle(Styles.dangerButton());
        removeBtn.setOnMouseEntered(e -> removeBtn.setStyle(Styles.button("#b91c1c", "white")));
        removeBtn.setOnMouseExited(e -> removeBtn.setStyle(Styles.dangerButton()));

        Label statusLabel = new Label();
        statusLabel.setStyle(Styles.label());

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { statusLabel.setText("⚠ Enter a floor name."); return; }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            boolean exists = lot.getFloors().stream().anyMatch(f -> f.getName().equals(name));
            if (exists) { statusLabel.setText("⚠ Floor already exists."); return; }
            lot.addParkingFloor(new ParkingFloor(name));
            statusLabel.setText("✅ Floor '" + name + "' added.");
            nameField.clear();
            refreshCallback.run();
        });

        removeBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { statusLabel.setText("⚠ Enter a floor name."); return; }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            boolean removed = lot.removeParkingFloor(name);
            if (removed) {
                statusLabel.setText("✅ Floor '" + name + "' removed.");
                nameField.clear();
                refreshCallback.run();
            } else {
                statusLabel.setText("⚠ Cannot remove: floor has occupied spots or doesn't exist.");
            }
        });

        HBox btnRow = new HBox(10, addBtn, removeBtn);
        card.getChildren().addAll(title, formRow("Floor Name:", nameField), btnRow, statusLabel);
        return card;
    }

    // ── Spot Management ───────────────────────────────────────────────────────

    private VBox buildSpotManagement() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());

        Label title = sectionTitle("🅿 Spot Management");

        // Floor selector
        ChoiceBox<String> floorBox = new ChoiceBox<>();
        floorBox.setStyle(Styles.input());
        floorBox.setMaxWidth(Double.MAX_VALUE);
        refreshFloorBox(floorBox);

        ChoiceBox<ParkingSpotType> typeBox = new ChoiceBox<>(
                FXCollections.observableArrayList(ParkingSpotType.values()));
        typeBox.setValue(ParkingSpotType.COMPACT);
        typeBox.setStyle(Styles.input());
        typeBox.setMaxWidth(Double.MAX_VALUE);

        TextField spotNumField = new TextField();
        spotNumField.setPromptText("Spot number (e.g. F4-C1)");
        spotNumField.setStyle(Styles.input());

        Label statusLabel = new Label();
        statusLabel.setStyle(Styles.label());

        Button addBtn = new Button("Add Spot");
        addBtn.setStyle(Styles.accentButton());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(Styles.accentButton()));
        addBtn.setOnAction(e -> {
            String floorName = floorBox.getValue();
            String spotNum = spotNumField.getText().trim();
            if (floorName == null || spotNum.isEmpty()) {
                statusLabel.setText("⚠ Select a floor and enter a spot number.");
                return;
            }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            ParkingFloor floor = lot.getFloors().stream()
                    .filter(f -> f.getName().equals(floorName)).findFirst().orElse(null);
            if (floor == null) { statusLabel.setText("⚠ Floor not found."); return; }

            // Check duplicate
            if (floor.getSpotByNumber(spotNum) != null) {
                statusLabel.setText("⚠ Spot number already exists on this floor.");
                return;
            }

            ParkingSpotType sType = typeBox.getValue();
            ParkingSpot spot = switch (sType) {
                case COMPACT     -> new CompactSpot(spotNum);
                case LARGE       -> new LargeSpot(spotNum);
                case HANDICAPPED -> new HandicappedSpot(spotNum);
                case MOTORCYCLE  -> new MotorbikeSpot(spotNum);
                case ELECTRIC    -> new ElectricSpot(spotNum);
            };
            floor.addParkingSpot(spot);
            statusLabel.setText("✅ Spot '" + spotNum + "' (" + sType.name() + ") added to " + floorName + ".");
            spotNumField.clear();
            refreshCallback.run();
        });

        Button removeBtn = new Button("Remove Spot");
        removeBtn.setStyle(Styles.dangerButton());
        removeBtn.setOnMouseEntered(e -> removeBtn.setStyle(Styles.button("#b91c1c", "white")));
        removeBtn.setOnMouseExited(e -> removeBtn.setStyle(Styles.dangerButton()));
        removeBtn.setOnAction(e -> {
            String floorName = floorBox.getValue();
            String spotNum = spotNumField.getText().trim();
            if (floorName == null || spotNum.isEmpty()) {
                statusLabel.setText("⚠ Select a floor and enter a spot number.");
                return;
            }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            ParkingFloor floor = lot.getFloors().stream()
                    .filter(f -> f.getName().equals(floorName)).findFirst().orElse(null);
            if (floor == null) { statusLabel.setText("⚠ Floor not found."); return; }
            boolean removed = floor.removeParkingSpot(spotNum);
            if (removed) {
                statusLabel.setText("✅ Spot '" + spotNum + "' removed from " + floorName + ".");
                spotNumField.clear();
                refreshCallback.run();
            } else {
                statusLabel.setText("⚠ Cannot remove: spot is occupied or not found.");
            }
        });

        HBox btnRow = new HBox(10, addBtn, removeBtn);
        card.getChildren().addAll(title,
                formRow("Floor:", floorBox),
                formRow("Spot Type:", typeBox),
                formRow("Spot Number:", spotNumField),
                btnRow, statusLabel);
        return card;
    }

    // ── Rate Management ───────────────────────────────────────────────────────

    private VBox buildRateManagement() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());

        Label title = sectionTitle("💰 Parking Rate Management");

        ParkingRate rate = DataStore.getInstance().getParkingLot().getParkingRate();

        // Rate table
        record RateRow(String hour, double rate) {}
        ObservableList<RateRow> rateData = FXCollections.observableArrayList(
                new RateRow("Hour 1",          rate.getFirstHourRate()),
                new RateRow("Hours 2-3",       rate.getSecondThirdHourRate()),
                new RateRow("Hour 4+",         rate.getRemainingHourRate())
        );

        TableView<RateRow> rateTable = new TableView<>(rateData);
        rateTable.setStyle(Styles.tableView());
        rateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rateTable.setPrefHeight(130);

        TableColumn<RateRow, String> colHour = new TableColumn<>("Hour");
        colHour.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hour()));

        TableColumn<RateRow, String> colRate = new TableColumn<>("Rate ($/hr)");
        colRate.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().rate())));

        rateTable.getColumns().addAll(colHour, colRate);

        // Edit fields
        Label editTitle = new Label("Edit Rates:");
        editTitle.setStyle(Styles.labelMuted());

        TextField hour1Field = new TextField(String.valueOf(rate.getFirstHourRate()));
        hour1Field.setStyle(Styles.input());
        TextField hour23Field = new TextField(String.valueOf(rate.getSecondThirdHourRate()));
        hour23Field.setStyle(Styles.input());
        TextField hour4Field = new TextField(String.valueOf(rate.getRemainingHourRate()));
        hour4Field.setStyle(Styles.input());

        Label statusLabel = new Label();
        statusLabel.setStyle(Styles.label());

        Button saveBtn = new Button("Save Rates");
        saveBtn.setStyle(Styles.accentButton());
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle(Styles.accentButton()));
        saveBtn.setOnAction(e -> {
            try {
                double r1 = Double.parseDouble(hour1Field.getText());
                double r23 = Double.parseDouble(hour23Field.getText());
                double r4 = Double.parseDouble(hour4Field.getText());
                if (r1 <= 0 || r23 <= 0 || r4 <= 0) throw new NumberFormatException();
                rate.setFirstHourRate(r1);
                rate.setSecondThirdHourRate(r23);
                rate.setRemainingHourRate(r4);
                rateData.setAll(
                        new RateRow("Hour 1",    r1),
                        new RateRow("Hours 2-3", r23),
                        new RateRow("Hour 4+",   r4)
                );
                statusLabel.setText("✅ Rates updated successfully.");
            } catch (NumberFormatException ex) {
                statusLabel.setText("⚠ Enter valid positive numbers.");
            }
        });

        GridPane editForm = new GridPane();
        editForm.setHgap(12); editForm.setVgap(8);
        editForm.add(styledLabel("Hour 1 rate ($):"),    0, 0); editForm.add(hour1Field,  1, 0);
        editForm.add(styledLabel("Hours 2-3 rate ($):"), 0, 1); editForm.add(hour23Field, 1, 1);
        editForm.add(styledLabel("Hour 4+ rate ($):"),   0, 2); editForm.add(hour4Field,  1, 2);
        ColumnConstraints c0 = new ColumnConstraints(150);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS);
        editForm.getColumnConstraints().addAll(c0, c1);

        card.getChildren().addAll(title, rateTable, editTitle, editForm, saveBtn, statusLabel);
        return card;
    }

    // ── Attendant Management ──────────────────────────────────────────────────

    private VBox buildAttendantManagement() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());

        Label title = sectionTitle("👤 Attendant Management");

        // Current attendants list
        ListView<String> attendantList = new ListView<>();
        attendantList.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-text-fill:" + Styles.TEXT +
                "; -fx-border-color:" + Styles.BORDER + ";");
        attendantList.setPrefHeight(120);
        refreshAttendantList(attendantList);

        // Add form
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setStyle(Styles.input());

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle(Styles.input());

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setStyle(Styles.input());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle(Styles.input());

        Label statusLabel = new Label();
        statusLabel.setStyle(Styles.label());

        Button addBtn = new Button("Add Attendant");
        addBtn.setStyle(Styles.accentButton());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(Styles.accentButton()));
        addBtn.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                statusLabel.setText("⚠ Username, password, and name are required.");
                return;
            }
            boolean exists = DataStore.getInstance().getAccounts().stream()
                    .anyMatch(a -> a.getUserName().equals(user));
            if (exists) { statusLabel.setText("⚠ Username already exists."); return; }

            Location loc = new Location("N/A", "N/A", "N/A", "N/A", "N/A");
            Person person = new Person(name, loc, email, "");
            DataStore.getInstance().addAccount(new ParkingAttendant(user, pass, person));
            statusLabel.setText("✅ Attendant '" + user + "' added.");
            userField.clear(); passField.clear(); nameField.clear(); emailField.clear();
            refreshAttendantList(attendantList);
        });

        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle(Styles.dangerButton());
        removeBtn.setOnMouseEntered(e -> removeBtn.setStyle(Styles.button("#b91c1c", "white")));
        removeBtn.setOnMouseExited(e -> removeBtn.setStyle(Styles.dangerButton()));
        removeBtn.setOnAction(e -> {
            String selected = attendantList.getSelectionModel().getSelectedItem();
            if (selected == null) { statusLabel.setText("⚠ Select an attendant."); return; }
            String username = selected.split(" ")[0];
            boolean removed = DataStore.getInstance().removeAccount(username);
            if (removed) {
                statusLabel.setText("✅ Attendant '" + username + "' removed.");
                refreshAttendantList(attendantList);
            } else {
                statusLabel.setText("⚠ Cannot remove admin accounts.");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(8);
        form.add(styledLabel("Username:"), 0, 0); form.add(userField,  1, 0);
        form.add(styledLabel("Password:"), 0, 1); form.add(passField,  1, 1);
        form.add(styledLabel("Full Name:"), 0, 2); form.add(nameField, 1, 2);
        form.add(styledLabel("Email:"),    0, 3); form.add(emailField, 1, 3);
        ColumnConstraints c0 = new ColumnConstraints(120);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c0, c1);

        HBox btnRow = new HBox(10, addBtn, removeBtn);

        card.getChildren().addAll(title,
                styledLabel("Current Attendants:"), attendantList,
                styledLabel("Add New Attendant:"), form,
                btnRow, statusLabel);
        return card;
    }

    private void refreshAttendantList(ListView<String> list) {
        List<String> items = DataStore.getInstance().getAccounts().stream()
                .map(a -> a.getUserName() + "  [" + a.getRole() + "]  — " + a.getPerson().getName())
                .toList();
        list.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshFloorBox(ChoiceBox<String> box) {
        List<String> floors = DataStore.getInstance().getParkingLot().getFloors().stream()
                .map(f -> f.getName()).toList();
        box.setItems(FXCollections.observableArrayList(floors));
        if (!floors.isEmpty()) box.setValue(floors.get(0));
    }

    private HBox formRow(String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setStyle(Styles.labelMuted());
        lbl.setMinWidth(130);
        lbl.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(12, lbl, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        return row;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 15));
        l.setStyle("-fx-text-fill:" + Styles.TEXT + ";");
        return l;
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(Styles.labelMuted());
        return l;
    }
}
