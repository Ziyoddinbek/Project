package com.parkinglot.ui.tabs;

import com.parkinglot.model.ParkingFloor;
import com.parkinglot.model.ParkingLot;
import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class OverviewTab {

    private Label fullLabel;
    private VBox floorBreakdown;
    private Label totalCapLabel, availableLabel, activeTicketsLabel, revenueLabel;

    public Tab build() {
        Tab tab = new Tab("Overview");
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

        ParkingLot lot = DataStore.getInstance().getParkingLot();

        // ── Summary Cards ─────────────────────────────────────────────────────
        Label sectionTitle = new Label("PARKING LOT OVERVIEW");
        sectionTitle.setStyle(Styles.sectionTitle());

        HBox cards = new HBox(16);
        cards.setFillHeight(true);

        totalCapLabel    = statCard("Total Capacity",  String.valueOf(lot.getTotalCapacity()), Styles.TEXT, "🏢");
        availableLabel   = statCard("Available Spots", String.valueOf(lot.getAvailableSpots()), Styles.SUCCESS, "✅");
        activeTicketsLabel = statCard("Active Tickets", String.valueOf(lot.getActiveTickets().size()), Styles.WARNING, "🎫");
        revenueLabel     = statCard("Today's Revenue", "$0.00", Styles.ACCENT, "💰");

        cards.getChildren().addAll(
                wrapCard(totalCapLabel,     "Total Capacity",  "🏢"),
                wrapCard(availableLabel,    "Available Spots", "✅"),
                wrapCard(activeTicketsLabel,"Active Tickets",  "🎫"),
                wrapCard(revenueLabel,      "Today's Revenue", "💰")
        );
        for (javafx.scene.Node n : cards.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── FULL label ────────────────────────────────────────────────────────
        fullLabel = new Label("⚠  PARKING LOT FULL");
        fullLabel.setStyle("-fx-text-fill:" + Styles.ERROR + "; -fx-font-size:18px; -fx-font-weight:bold;" +
                " -fx-background-color:#3b0000; -fx-background-radius:8; -fx-padding:10 20;");
        fullLabel.setVisible(lot.isFull());
        fullLabel.setManaged(lot.isFull());

        // ── Floor Breakdown ───────────────────────────────────────────────────
        Label floorTitle = new Label("FLOOR AVAILABILITY BREAKDOWN");
        floorTitle.setStyle(Styles.sectionTitle());

        floorBreakdown = new VBox(12);
        buildFloorBreakdown(lot);

        root.getChildren().addAll(sectionTitle, cards, fullLabel, floorTitle, floorBreakdown);
        return root;
    }

    private VBox wrapCard(Label valueLabel, String labelText, String icon) {
        Label lbl = new Label(labelText);
        lbl.setStyle(Styles.labelMuted());

        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(Styles.card());
        card.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(lbl, valueLabel);
        return card;
    }

    private Label statCard(String labelText, String value, String color, String icon) {
        Label l = new Label(value);
        l.setFont(Font.font("System", FontWeight.BOLD, 28));
        l.setStyle("-fx-text-fill:" + color + ";");
        return l;
    }

    private void buildFloorBreakdown(ParkingLot lot) {
        floorBreakdown.getChildren().clear();
        for (ParkingFloor floor : lot.getFloors()) {
            VBox floorCard = new VBox(12);
            floorCard.setPadding(new Insets(16));
            floorCard.setStyle(Styles.card());

            Label floorName = new Label(floor.getName());
            floorName.setFont(Font.font("System", FontWeight.BOLD, 15));
            floorName.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

            HBox spotTypes = new HBox(12);
            for (ParkingSpotType type : ParkingSpotType.values()) {
                int total = floor.getTotalCountByType(type);
                if (total == 0) continue;
                int free = floor.getFreeCountByType(type);
                VBox typeBox = buildTypeBox(type, free, total);
                spotTypes.getChildren().add(typeBox);
            }

            // Progress bar for overall floor usage
            int total = floor.getTotalCapacity();
            int used = total - floor.getAvailableCount();
            ProgressBar pb = new ProgressBar(total > 0 ? (double) used / total : 0);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setPrefHeight(8);
            String pbColor = floor.isFull() ? Styles.ERROR : (used > total * 0.8 ? Styles.WARNING : Styles.SUCCESS);
            pb.setStyle("-fx-accent:" + pbColor + ";");

            Label usageLabel = new Label(used + "/" + total + " spots used");
            usageLabel.setStyle(Styles.labelMuted());

            floorCard.getChildren().addAll(floorName, spotTypes, pb, usageLabel);
            floorBreakdown.getChildren().add(floorCard);
        }
    }

    private VBox buildTypeBox(ParkingSpotType type, int free, int total) {
        String color = switch (type) {
            case ELECTRIC    -> Styles.SPOT_ELECTRIC_TEXT;
            case HANDICAPPED -> Styles.SPOT_HANDICAPPED_TEXT;
            case COMPACT     -> Styles.SUCCESS;
            case LARGE       -> "#60a5fa";
            case MOTORCYCLE  -> "#c084fc";
        };
        String emoji = switch (type) {
            case ELECTRIC    -> "⚡";
            case HANDICAPPED -> "♿";
            case COMPACT     -> "🚗";
            case LARGE       -> "🚛";
            case MOTORCYCLE  -> "🏍";
        };

        Label typeLabel = new Label(emoji + " " + type.name());
        typeLabel.setStyle("-fx-text-fill:" + color + "; -fx-font-size:11px; -fx-font-weight:bold;");

        Label countLabel = new Label(free + "/" + total);
        countLabel.setStyle("-fx-text-fill:" + Styles.TEXT + "; -fx-font-size:14px; -fx-font-weight:bold;");

        Label freeLabel = new Label("free");
        freeLabel.setStyle(Styles.labelMuted());

        VBox box = new VBox(2, typeLabel, countLabel, freeLabel);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background-radius:8;");
        box.setMinWidth(90);
        return box;
    }

    public void refresh() {
        ParkingLot lot = DataStore.getInstance().getParkingLot();

        totalCapLabel.setText(String.valueOf(lot.getTotalCapacity()));
        availableLabel.setText(String.valueOf(lot.getAvailableSpots()));
        activeTicketsLabel.setText(String.valueOf(lot.getActiveTickets().size()));

        double revenue = lot.getActiveTickets().values().stream()
                .filter(t -> !t.isActive())
                .mapToDouble(com.parkinglot.model.ParkingTicket::getPaidAmount)
                .sum();
        // Also count from payment history
        double histRevenue = DataStore.getInstance().getPaymentHistory().stream()
                .mapToDouble(com.parkinglot.model.Payment::getAmount)
                .sum();
        revenueLabel.setText(String.format("$%.2f", histRevenue));

        fullLabel.setVisible(lot.isFull());
        fullLabel.setManaged(lot.isFull());

        buildFloorBreakdown(lot);
    }
}
