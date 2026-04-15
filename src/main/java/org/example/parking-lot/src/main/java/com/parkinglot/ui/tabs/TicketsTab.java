package com.parkinglot.ui.tabs;

import com.parkinglot.model.*;
import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.model.enums.PaymentType;
import com.parkinglot.model.enums.VehicleType;
import com.parkinglot.model.payments.CashTransaction;
import com.parkinglot.model.payments.CreditCardTransaction;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketsTab {

    private TableView<ParkingTicket> table;
    private ObservableList<ParkingTicket> ticketData;
    private Runnable refreshCallback;

    public Tab build(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
        Tab tab = new Tab("Tickets");
        tab.setClosable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle(Styles.mainBackground());

        Label title = new Label("PARKING TICKETS");
        title.setStyle(Styles.sectionTitle());

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button issueBtn = new Button("+ Issue New Ticket");
        issueBtn.setStyle(Styles.accentButton());
        issueBtn.setOnMouseEntered(e -> issueBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        issueBtn.setOnMouseExited(e -> issueBtn.setStyle(Styles.accentButton()));
        issueBtn.setOnAction(e -> showIssueTicketDialog());

        Button payBtn = new Button("💳 Pay Ticket");
        payBtn.setStyle(Styles.successButton());
        payBtn.setOnMouseEntered(e -> payBtn.setStyle(Styles.button("#16a34a", "white")));
        payBtn.setOnMouseExited(e -> payBtn.setStyle(Styles.successButton()));
        payBtn.setOnAction(e -> {
            ParkingTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select a ticket first."); return; }
            if (!selected.isActive()) { showAlert("This ticket is already " + selected.getStatus() + "."); return; }
            showPaymentDialog(selected);
        });

        Button lostBtn = new Button("Mark Lost");
        lostBtn.setStyle(Styles.button(Styles.WARNING, "white"));
        lostBtn.setOnMouseEntered(e -> lostBtn.setStyle(Styles.button("#d97706", "white")));
        lostBtn.setOnMouseExited(e -> lostBtn.setStyle(Styles.button(Styles.WARNING, "white")));
        lostBtn.setOnAction(e -> {
            ParkingTicket selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select a ticket first."); return; }
            if (!selected.isActive()) { showAlert("Ticket is not active."); return; }
            selected.markLost();
            // Free the spot
            freeSpot(selected);
            refresh();
            refreshCallback.run();
        });

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT));
        refreshBtn.setOnAction(e -> refresh());

        toolbar.getChildren().addAll(issueBtn, payBtn, lostBtn, refreshBtn);

        // Table
        table = new TableView<>();
        table.setStyle(Styles.tableView());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ParkingTicket, String> colNum    = col("Ticket #",      t -> t.getTicketNumber());
        TableColumn<ParkingTicket, String> colPlate  = col("License Plate", t -> t.getVehicle().getLicenseNumber());
        TableColumn<ParkingTicket, String> colType   = col("Vehicle Type",  t -> t.getVehicle().getType().name());
        TableColumn<ParkingTicket, String> colFloor  = col("Floor",         t -> t.getFloorName());
        TableColumn<ParkingTicket, String> colSpot   = col("Spot",          t -> t.getParkingSpot().getNumber());
        TableColumn<ParkingTicket, String> colIssued = col("Issued At",     t -> t.getIssuedAtFormatted());
        TableColumn<ParkingTicket, String> colDur    = col("Duration",      t -> formatDuration(t.getIssuedAt()));
        TableColumn<ParkingTicket, String> colAmt    = col("Amount Due",    t -> t.isActive()
                ? String.format("$%.2f", DataStore.getInstance().getParkingLot().getParkingRate().calculateFee(t.getIssuedAt()))
                : String.format("$%.2f", t.getPaidAmount()));
        TableColumn<ParkingTicket, String> colStatus = col("Status",        t -> t.getStatus().name());

        // Color status column
        colStatus.setCellFactory(col2 -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "ACTIVE" -> Styles.SUCCESS;
                    case "PAID"   -> Styles.TEXT_MUTED;
                    case "LOST"   -> Styles.ERROR;
                    default       -> Styles.TEXT;
                };
                setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold;");
            }
        });

        table.getColumns().addAll(colNum, colPlate, colType, colFloor, colSpot, colIssued, colDur, colAmt, colStatus);

        ticketData = FXCollections.observableArrayList();
        table.setItems(ticketData);
        loadTickets();

        root.getChildren().addAll(title, toolbar, table);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        tab.setContent(scroll);
        return tab;
    }

    private void loadTickets() {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        List<ParkingTicket> all = new ArrayList<>(lot.getActiveTickets().values());
        // Also include paid/lost from payment history context
        ticketData.setAll(all);
    }

    public void refresh() {
        loadTickets();
    }

    private void showIssueTicketDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Issue New Parking Ticket");
        dialog.setHeaderText(null);

        DialogPane dp = dialog.getDialogPane();
        dp.setStyle(Styles.mainBackground());
        dp.setPrefWidth(460);

        TextField plateField = new TextField();
        plateField.setPromptText("e.g. ABC-1234");
        plateField.setStyle(Styles.input());

        ChoiceBox<VehicleType> vehicleTypeBox = new ChoiceBox<>(
                FXCollections.observableArrayList(VehicleType.values()));
        vehicleTypeBox.setValue(VehicleType.CAR);
        vehicleTypeBox.setMaxWidth(Double.MAX_VALUE);
        vehicleTypeBox.setStyle(Styles.input());

        ChoiceBox<ParkingSpotType> spotTypeBox = new ChoiceBox<>(
                FXCollections.observableArrayList(ParkingSpotType.values()));
        spotTypeBox.setValue(ParkingSpotType.COMPACT);
        spotTypeBox.setMaxWidth(Double.MAX_VALUE);
        spotTypeBox.setStyle(Styles.input());

        // Auto-select spot type based on vehicle type
        vehicleTypeBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == VehicleType.ELECTRIC)   spotTypeBox.setValue(ParkingSpotType.ELECTRIC);
            else if (newVal == VehicleType.MOTORCYCLE) spotTypeBox.setValue(ParkingSpotType.MOTORCYCLE);
            else if (newVal == VehicleType.TRUCK || newVal == VehicleType.VAN)
                spotTypeBox.setValue(ParkingSpotType.LARGE);
            else spotTypeBox.setValue(ParkingSpotType.COMPACT);
        });

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(140);
        resultArea.setStyle("-fx-control-inner-background:" + Styles.BG_MAIN + "; -fx-text-fill:" + Styles.SUCCESS +
                "; -fx-font-family:monospace; -fx-font-size:12px;");
        resultArea.setVisible(false);
        resultArea.setManaged(false);

        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill:" + Styles.WARNING + "; -fx-font-size:12px;");
        warningLabel.setVisible(false);
        warningLabel.setManaged(false);

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(12);
        form.setPadding(new Insets(16));
        form.add(styledLabel("License Plate:"), 0, 0);
        form.add(plateField, 1, 0);
        form.add(styledLabel("Vehicle Type:"), 0, 1);
        form.add(vehicleTypeBox, 1, 1);
        form.add(styledLabel("Spot Preference:"), 0, 2);
        form.add(spotTypeBox, 1, 2);
        form.add(warningLabel, 0, 3, 2, 1);
        form.add(resultArea, 0, 4, 2, 1);
        ColumnConstraints c0 = new ColumnConstraints(130);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c0, c1);

        dp.setContent(form);

        ButtonType issueBtn = new ButtonType("Issue Ticket", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(issueBtn, ButtonType.CANCEL);
        dp.lookupButton(ButtonType.CANCEL).setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT));

        // Override the default close behavior so we can show result in dialog
        Button okButton = (Button) dp.lookupButton(issueBtn);
        okButton.setStyle(Styles.accentButton());
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            event.consume(); // prevent dialog from closing
            String plate = plateField.getText().trim().toUpperCase();
            if (plate.isEmpty()) { showAlert("Enter a license plate."); return; }

            ParkingLot lot = DataStore.getInstance().getParkingLot();
            if (lot.isFull()) { showAlert("Parking lot is full!"); return; }

            // Check if plate already parked
            boolean alreadyParked = lot.getActiveTickets().values().stream()
                    .anyMatch(t -> t.getVehicle().getLicenseNumber().equalsIgnoreCase(plate) && t.isActive());
            if (alreadyParked) { showAlert("This vehicle is already parked."); return; }

            VehicleType vType = vehicleTypeBox.getValue();
            ParkingSpotType sType = spotTypeBox.getValue();

            // Warn if electric vehicle not requesting electric spot
            if (vType == VehicleType.ELECTRIC && sType != ParkingSpotType.ELECTRIC) {
                sType = ParkingSpotType.ELECTRIC;
                warningLabel.setText("⚡ Electric vehicles are assigned to electric spots only.");
                warningLabel.setVisible(true);
                warningLabel.setManaged(true);
            }

            Vehicle vehicle = DataStore.getInstance().createVehicle(plate, vType);
            ParkingTicket ticket = lot.getNewParkingTicket(vehicle, sType);

            if (ticket == null) {
                // Try handicapped as last resort
                ticket = lot.getNewParkingTicket(vehicle, ParkingSpotType.HANDICAPPED);
                if (ticket != null) {
                    warningLabel.setText("⚠ No preferred spots available. Assigned to HANDICAPPED spot.");
                    warningLabel.setVisible(true);
                    warningLabel.setManaged(true);
                }
            }

            if (ticket == null) {
                showAlert("No available spots for this vehicle type.");
                return;
            }

            resultArea.setText(
                    "✅ TICKET ISSUED SUCCESSFULLY\n" +
                    "─────────────────────────────\n" +
                    "Ticket #:     " + ticket.getTicketNumber() + "\n" +
                    "License Plate:" + ticket.getVehicle().getLicenseNumber() + "\n" +
                    "Vehicle Type: " + ticket.getVehicle().getType().name() + "\n" +
                    "Floor:        " + ticket.getFloorName() + "\n" +
                    "Spot:         " + ticket.getParkingSpot().getNumber() + "\n" +
                    "Spot Type:    " + ticket.getParkingSpot().getType().name() + "\n" +
                    "Issued At:    " + ticket.getIssuedAtFormatted() + "\n" +
                    "─────────────────────────────\n" +
                    "Rate: $4.00/hr (1st), $3.50/hr (2-3), $2.50/hr (4+)"
            );
            resultArea.setVisible(true);
            resultArea.setManaged(true);
            okButton.setText("Close");
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, ev2 -> {
                dialog.close();
                refresh();
                refreshCallback.run();
            });
        });

        dialog.showAndWait();
    }

    private void showPaymentDialog(ParkingTicket ticket) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Process Payment");
        dialog.setHeaderText(null);

        DialogPane dp = dialog.getDialogPane();
        dp.setStyle(Styles.mainBackground());
        dp.setPrefWidth(480);

        ParkingRate rate = DataStore.getInstance().getParkingLot().getParkingRate();
        double amountDue = rate.calculateFee(ticket.getIssuedAt());

        // Ticket info
        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(12));
        infoBox.setStyle(Styles.card());
        infoBox.getChildren().addAll(
                infoRow("Ticket #:",      ticket.getTicketNumber()),
                infoRow("License Plate:", ticket.getVehicle().getLicenseNumber()),
                infoRow("Vehicle Type:",  ticket.getVehicle().getType().name()),
                infoRow("Floor / Spot:",  ticket.getFloorName() + " / " + ticket.getParkingSpot().getNumber()),
                infoRow("Issued At:",     ticket.getIssuedAtFormatted()),
                infoRow("Duration:",      formatDuration(ticket.getIssuedAt()))
        );

        Label amountLabel = new Label(String.format("Amount Due: $%.2f", amountDue));
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        amountLabel.setStyle("-fx-text-fill:" + Styles.WARNING + ";");

        // Payment method
        Label methodLabel = new Label("Payment Method:");
        methodLabel.setStyle(Styles.labelMuted());

        ToggleGroup tg = new ToggleGroup();
        RadioButton cashRb = new RadioButton("Cash");
        cashRb.setToggleGroup(tg);
        cashRb.setSelected(true);
        cashRb.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        RadioButton cardRb = new RadioButton("Credit Card");
        cardRb.setToggleGroup(tg);
        cardRb.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        HBox methodBox = new HBox(20, cashRb, cardRb);
        methodBox.setAlignment(Pos.CENTER_LEFT);

        // Cash fields
        VBox cashBox = new VBox(8);
        Label cashLabel = new Label("Cash Tendered ($):");
        cashLabel.setStyle(Styles.labelMuted());
        TextField cashField = new TextField(String.format("%.2f", amountDue));
        cashField.setStyle(Styles.input());
        Label changeLabel = new Label("Change: $0.00");
        changeLabel.setStyle("-fx-text-fill:" + Styles.SUCCESS + "; -fx-font-weight:bold;");
        cashField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double tendered = Double.parseDouble(newVal);
                double change = tendered - amountDue;
                changeLabel.setText(change >= 0
                        ? String.format("Change: $%.2f", change)
                        : "⚠ Insufficient amount");
                changeLabel.setStyle(change >= 0
                        ? "-fx-text-fill:" + Styles.SUCCESS + "; -fx-font-weight:bold;"
                        : "-fx-text-fill:" + Styles.ERROR + "; -fx-font-weight:bold;");
            } catch (NumberFormatException ex) {
                changeLabel.setText("Invalid amount");
            }
        });
        cashBox.getChildren().addAll(cashLabel, cashField, changeLabel);

        // Card fields
        VBox cardBox = new VBox(8);
        Label cardLabel = new Label("Name on Card:");
        cardLabel.setStyle(Styles.labelMuted());
        TextField cardNameField = new TextField();
        cardNameField.setPromptText("Full name as on card");
        cardNameField.setStyle(Styles.input());
        cardBox.getChildren().addAll(cardLabel, cardNameField);
        cardBox.setVisible(false);
        cardBox.setManaged(false);

        tg.selectedToggleProperty().addListener((obs, old, newVal) -> {
            boolean isCash = newVal == cashRb;
            cashBox.setVisible(isCash);
            cashBox.setManaged(isCash);
            cardBox.setVisible(!isCash);
            cardBox.setManaged(!isCash);
        });

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill:" + Styles.SUCCESS + "; -fx-font-weight:bold;");

        VBox content = new VBox(14, infoBox, amountLabel, methodLabel, methodBox, cashBox, cardBox, resultLabel);
        content.setPadding(new Insets(16));
        dp.setContent(content);

        ButtonType confirmBtn = new ButtonType("Confirm Payment", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);
        dp.lookupButton(ButtonType.CANCEL).setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT));

        Button okBtn = (Button) dp.lookupButton(confirmBtn);
        okBtn.setStyle(Styles.successButton());
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            event.consume();
            boolean isCash = cashRb.isSelected();
            Payment payment;

            if (isCash) {
                double tendered;
                try { tendered = Double.parseDouble(cashField.getText()); }
                catch (NumberFormatException ex) { showAlert("Enter a valid cash amount."); return; }
                if (tendered < amountDue) { showAlert("Insufficient cash tendered."); return; }
                CashTransaction ct = new CashTransaction(amountDue, ticket.getTicketNumber(), tendered);
                ct.initiateTransaction();
                payment = ct;
            } else {
                String name = cardNameField.getText().trim();
                if (name.isEmpty()) { showAlert("Enter name on card."); return; }
                CreditCardTransaction cct = new CreditCardTransaction(amountDue, ticket.getTicketNumber(), name);
                cct.initiateTransaction();
                payment = cct;
            }

            // Mark ticket paid and free spot
            ticket.markPaid(amountDue);
            freeSpot(ticket);
            DataStore.getInstance().addPayment(payment);
            DataStore.getInstance().getParkingLot().removeFromActiveTickets(ticket.getTicketNumber());

            resultLabel.setText("✅ Payment of $" + String.format("%.2f", amountDue) + " processed successfully!");
            okBtn.setText("Close");
            okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev2 -> {
                dialog.close();
                refresh();
                refreshCallback.run();
            });
        });

        dialog.showAndWait();
    }

    private void freeSpot(ParkingTicket ticket) {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        for (ParkingFloor floor : lot.getFloors()) {
            if (floor.getName().equals(ticket.getFloorName())) {
                floor.freeSlot(ticket.getParkingSpot().getNumber());
                break;
            }
        }
    }

    private HBox infoRow(String key, String value) {
        Label k = new Label(key);
        k.setStyle(Styles.labelMuted());
        k.setMinWidth(120);
        Label v = new Label(value);
        v.setStyle(Styles.label());
        HBox row = new HBox(8, k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private String formatDuration(LocalDateTime issuedAt) {
        Duration d = Duration.between(issuedAt, LocalDateTime.now());
        long h = d.toHours();
        long m = d.toMinutesPart();
        return h + "h " + m + "m";
    }

    private TableColumn<ParkingTicket, String> col(String title,
            java.util.function.Function<ParkingTicket, String> extractor) {
        TableColumn<ParkingTicket, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(extractor.apply(d.getValue())));
        return c;
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(Styles.labelMuted());
        return l;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.getDialogPane().setStyle(Styles.mainBackground());
        a.showAndWait();
    }
}
