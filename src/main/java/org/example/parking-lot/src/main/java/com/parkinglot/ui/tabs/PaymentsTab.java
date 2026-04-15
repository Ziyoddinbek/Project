package com.parkinglot.ui.tabs;

import com.parkinglot.model.Payment;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class PaymentsTab {

    private TableView<Payment> table;
    private ObservableList<Payment> data;

    public Tab build() {
        Tab tab = new Tab("Payments");
        tab.setClosable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle(Styles.mainBackground());

        Label title = new Label("PAYMENT HISTORY");
        title.setStyle(Styles.sectionTitle());

        table = new TableView<>();
        table.setStyle(Styles.tableView());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Payment, String> colTicket  = col("Ticket #",     p -> p.getTicketNumber());
        TableColumn<Payment, String> colAmount  = col("Amount",       p -> String.format("$%.2f", p.getAmount()));
        TableColumn<Payment, String> colType    = col("Payment Type", p -> p.getPaymentType().name().replace("_", " "));
        TableColumn<Payment, String> colDate    = col("Paid At",      p -> p.getCreationDateFormatted());
        TableColumn<Payment, String> colStatus  = col("Status",       p -> p.getStatus().name());

        // Color status
        colStatus.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "COMPLETED" -> Styles.SUCCESS;
                    case "DECLINED"  -> Styles.ERROR;
                    case "PENDING"   -> Styles.WARNING;
                    default          -> Styles.TEXT;
                };
                setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold;");
            }
        });

        table.getColumns().addAll(colTicket, colAmount, colType, colDate, colStatus);

        data = FXCollections.observableArrayList();
        table.setItems(data);
        loadData();

        // Summary
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-text-fill:" + Styles.SUCCESS + "; -fx-font-size:14px; -fx-font-weight:bold;");
        updateTotal(totalLabel);

        root.getChildren().addAll(title, table, totalLabel);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        tab.setContent(scroll);
        return tab;
    }

    private void loadData() {
        List<Payment> payments = DataStore.getInstance().getPaymentHistory();
        data.setAll(payments);
    }

    private void updateTotal(Label label) {
        double total = DataStore.getInstance().getPaymentHistory().stream()
                .mapToDouble(Payment::getAmount).sum();
        label.setText(String.format("Total Revenue: $%.2f  (%d transactions)",
                total, DataStore.getInstance().getPaymentHistory().size()));
    }

    public void refresh() {
        loadData();
    }

    private TableColumn<Payment, String> col(String title,
            java.util.function.Function<Payment, String> extractor) {
        TableColumn<Payment, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(extractor.apply(d.getValue())));
        return c;
    }
}
