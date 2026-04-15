package com.parkinglot.ui;

import com.parkinglot.model.Account;
import com.parkinglot.model.accounts.Admin;
import com.parkinglot.service.DataStore;
import com.parkinglot.service.SceneManager;
import com.parkinglot.ui.tabs.*;
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

    // ── Top Bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar(Account user) {
        HBox bar = new HBox(16);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:" + Styles.BG_CARD + ";" +
                " -fx-border-color:" + Styles.BORDER + "; -fx-border-width:0 0 1 0;");

        Label icon = new Label("🅿");
        icon.setStyle("-fx-text-fill:" + Styles.ACCENT + "; -fx-font-size:22px;");

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

    // ── Tab Pane ──────────────────────────────────────────────────────────────

    private TabPane buildTabPane(Account user) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(Styles.tabPane());

        // Shared refresh callback — refreshes all tabs
        Runnable refreshAll = () -> {
            overviewTab.refresh();
            floorsTab.refresh();
            ticketsTab.refresh();
            vehiclesTab.refresh();
            paymentsTab.refresh();
        };

        overviewTab  = new OverviewTab();
        floorsTab    = new FloorsTab();
        ticketsTab   = new TicketsTab();
        vehiclesTab  = new VehiclesTab();
        paymentsTab  = new PaymentsTab();

        tabPane.getTabs().addAll(
                overviewTab.build(),
                floorsTab.build(refreshAll),
                ticketsTab.build(refreshAll),
                vehiclesTab.build(),
                paymentsTab.build()
        );

        // Admin tab only for admins
        if (user instanceof Admin) {
            AdminTab adminTab = new AdminTab();
            tabPane.getTabs().add(adminTab.build(refreshAll));
        }

        // Refresh overview when switching to it
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, old, newTab) -> {
            if (newTab != null) {
                String tabText = newTab.getText();
                switch (tabText) {
                    case "Overview"  -> overviewTab.refresh();
                    case "Floors"    -> floorsTab.refresh();
                    case "Tickets"   -> ticketsTab.refresh();
                    case "Vehicles"  -> vehiclesTab.refresh();
                    case "Payments"  -> paymentsTab.refresh();
                }
            }
        });

        return tabPane;
    }
}
