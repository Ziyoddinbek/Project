package com.parkinglot.ui;

import com.parkinglot.model.Account;
import com.parkinglot.model.accounts.Admin;
import com.parkinglot.service.DataStore;
import com.parkinglot.service.SceneManager;
import com.parkinglot.ui.tabs.AdminTab;
import com.parkinglot.ui.tabs.BuyTicketTab;
import com.parkinglot.ui.tabs.FloorsTab;
import com.parkinglot.ui.tabs.OverviewTab;
import com.parkinglot.ui.tabs.PaymentsTab;
import com.parkinglot.ui.tabs.TicketsTab;
import com.parkinglot.ui.tabs.VehiclesTab;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardScreen {

    private OverviewTab overviewTab;
    private FloorsTab floorsTab;
    private TicketsTab ticketsTab;
    private BuyTicketTab buyTicketTab;
    private VehiclesTab vehiclesTab;
    private PaymentsTab paymentsTab;

    public Scene build() {
        Account user = DataStore.getInstance().getLoggedInUser();

        BorderPane root = new BorderPane();
        root.setStyle(Styles.mainBackground());
        root.setTop(buildTopBar(user));
        root.setCenter(buildTabPane(user));

        return new Scene(root, 1280, 800);
    }



    private HBox buildTopBar(Account user) {
        HBox bar = new HBox(16);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:" + Styles.BG_CARD + ";" +
                " -fx-border-color:" + Styles.BORDER + "; -fx-border-width:0 0 1 0;");

        // C2 logo — small text badge for top bar
        Label icon = new Label("C2");
        icon.setStyle(
                "-fx-text-fill:" + Styles.SUCCESS + ";" +
                " -fx-font-size:15px;" +
                " -fx-font-weight:bold;" +
                " -fx-background-color:" + Styles.BG_INPUT + ";" +
                " -fx-background-radius:6;" +
                " -fx-padding:3 8 3 8;"
        );

        Label lotName = new Label(DataStore.getInstance().getParkingLot().getName());
        lotName.setFont(Font.font("System", FontWeight.BOLD, 16));
        lotName.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + user.getPerson().getName() + "  [" + user.getRole() + "]");
        userLabel.setStyle("-fx-text-fill:" + Styles.TEXT_MUTED + "; -fx-font-size:13px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(Styles.button(Styles.ERROR, "white"));
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(Styles.button("#b91c1c", "white")));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(Styles.button(Styles.ERROR, "white")));
        logoutBtn.setOnAction(e -> {
            DataStore.getInstance().setLoggedInUser(null);
            SceneManager.getInstance().navigateTo("login");
        });

        bar.getChildren().addAll(icon, lotName, spacer, userLabel, logoutBtn);
        return bar;
    }



    private TabPane buildTabPane(Account user) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(Styles.tabPane());

        // Shared refresh callback — refreshes all tabs
        Runnable refreshAll = () -> {
            overviewTab.refresh();
            floorsTab.refresh();
            if (ticketsTab != null) ticketsTab.refresh();
            if (buyTicketTab == null) {} // no-op, stateless
            vehiclesTab.refresh();
            paymentsTab.refresh();
        };

        overviewTab  = new OverviewTab();
        floorsTab    = new FloorsTab();
        vehiclesTab  = new VehiclesTab();
        paymentsTab  = new PaymentsTab();

        tabPane.getTabs().addAll(
                overviewTab.build(),
                floorsTab.build(refreshAll)
        );

        if (user instanceof Admin) {
            // Admin sees full Tickets tab (table + issue + pay + mark lost)
            ticketsTab = new TicketsTab();
            tabPane.getTabs().add(ticketsTab.build(refreshAll));
            tabPane.getTabs().addAll(vehiclesTab.build(), paymentsTab.build());
            AdminTab adminTab = new AdminTab();
            tabPane.getTabs().add(adminTab.build(refreshAll));
        } else {
            // Attendant sees only Buy Ticket form — no Vehicles tab
            buyTicketTab = new BuyTicketTab();
            tabPane.getTabs().addAll(buyTicketTab.build(refreshAll), paymentsTab.build());
        }

        // Refresh on tab switch
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, old, newTab) -> {
            if (newTab == null) return;
            switch (newTab.getText()) {
                case "Overview"    -> overviewTab.refresh();
                case "Floors"      -> floorsTab.refresh();
                case "Tickets"     -> { if (ticketsTab != null) ticketsTab.refresh(); }
                case "Vehicles"    -> vehiclesTab.refresh();
                case "Payments"    -> paymentsTab.refresh();
            }
        });

        return tabPane;
    }
}
