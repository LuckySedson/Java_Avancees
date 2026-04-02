package com.stock.stockclient.view;

import com.stock.stockclient.model.Produit;
import com.stock.stockclient.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class EtatStockView {

    private final ApiService api = new ApiService();
    private final TableView<Produit> table = new TableView<>();
    private final ObservableList<Produit> data = FXCollections.observableArrayList();

    public VBox getView() {

        // ── Titre ──
        Label titre = new Label("ÉTAT DE STOCK");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titre.setAlignment(Pos.CENTER);

        // ── Colonnes ──
        TableColumn<Produit, String>  colDesign = new TableColumn<>("DÉSIGNATION");
        TableColumn<Produit, Integer> colStock  = new TableColumn<>("STOCK");

        colDesign.setCellValueFactory(new PropertyValueFactory<>("design"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colDesign.setPrefWidth(400);
        colStock.setPrefWidth(200);

        // stock faible rouge < 20
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setStyle(item < 20
                            ? "-fx-text-fill: red; -fx-font-weight: bold;"
                            : "-fx-text-fill: green;");
                }
            }
        });

        table.getColumns().addAll(colDesign, colStock);
        table.setItems(data);

        // ── Animation à la sélection ──
        table.setRowFactory(tv -> {
            TableRow<Produit> row = new TableRow<>();
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle(
                            "-fx-background-color: linear-gradient(to right, #42a5f5, #1976D2);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;"
                    );
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        Button btnRafraichir = new Button("🔄 Rafraîchir l'état");
        btnRafraichir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnRafraichir.setOnAction(e -> chargerDonnees());

        HBox hbox = new HBox(btnRafraichir);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(10));

        VBox layout = new VBox(15, titre, hbox, table);
        layout.setPadding(new Insets(15));
        chargerDonnees();
        return layout;
    }

    public void refresh() { chargerDonnees(); }

    private void chargerDonnees() {
        Task<List<Produit>> task = new Task<>() {
            @Override protected List<Produit> call() throws Exception {
                return api.getAllProduits();
            }
        };
        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible de charger l'état de stock.\n"
                    + task.getException().getMessage());
            alert.showAndWait();
        });
        new Thread(task).start();
    }
}