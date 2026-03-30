package com.stock.stockclient.view;

import com.stock.stockclient.model.BonEntree;
import com.stock.stockclient.model.BonSortie;
import com.stock.stockclient.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class MouvementsView {
    private final ApiService api = new ApiService();

    private final TableView<BonEntree> tableEntree = new TableView<>();
    private final TableView<BonSortie> tableSortie = new TableView<>();
    private final ObservableList<BonEntree> dataEntree = FXCollections.observableArrayList();
    private final ObservableList<BonSortie> dataSortie = FXCollections.observableArrayList();

    private final TextField tfNumProduit = new TextField();

    public VBox getView() {
        // ── Barre de recherche ──
        tfNumProduit.setPromptText("Entrez le N° Produit...");
        tfNumProduit.setPrefWidth(250);
        Button btnRechercher = new Button("🔍 Voir les mouvements");
        btnRechercher.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        HBox searchBar = new HBox(10, new Label("N° Produit :"), tfNumProduit, btnRechercher);
        searchBar.setPadding(new Insets(10));

        // ── Table Entrées ──
        TableColumn<BonEntree, String>  colNumE  = new TableColumn<>("N° Bon");
        TableColumn<BonEntree, Integer> colQteE  = new TableColumn<>("Qté Entrée");
        TableColumn<BonEntree, String>  colDateE = new TableColumn<>("Date");

        colNumE.setCellValueFactory(new PropertyValueFactory<>("numBonEntree"));
        colQteE.setCellValueFactory(new PropertyValueFactory<>("qteEntree"));
        colDateE.setCellValueFactory(new PropertyValueFactory<>("dateEntree"));

        colNumE.setPrefWidth(150);
        colQteE.setPrefWidth(120);
        colDateE.setPrefWidth(130);

        tableEntree.getColumns().addAll(colNumE, colQteE, colDateE);
        tableEntree.setItems(dataEntree);
        tableEntree.setPrefHeight(200);

        // ── Table Sorties ──
        TableColumn<BonSortie, String>  colNumS  = new TableColumn<>("N° Bon");
        TableColumn<BonSortie, Integer> colQteS  = new TableColumn<>("Qté Sortie");
        TableColumn<BonSortie, String>  colDateS = new TableColumn<>("Date");

        colNumS.setCellValueFactory(new PropertyValueFactory<>("numBonSortie"));
        colQteS.setCellValueFactory(new PropertyValueFactory<>("qteSortie"));
        colDateS.setCellValueFactory(new PropertyValueFactory<>("dateSortie"));

        colNumS.setPrefWidth(150);
        colQteS.setPrefWidth(120);
        colDateS.setPrefWidth(130);

        tableSortie.getColumns().addAll(colNumS, colQteS, colDateS);
        tableSortie.setItems(dataSortie);
        tableSortie.setPrefHeight(200);

        // ── Action ──
        btnRechercher.setOnAction(e -> {
            String numProd = tfNumProduit.getText().trim();
            if (numProd.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Attention");
                a.setContentText("Saisis un N° de produit.");
                a.showAndWait();
                return;
            }

            // Charger les entrées
            Task<List<BonEntree>> taskEntree = new Task<>() {
                @Override protected List<BonEntree> call() throws Exception {
                    return api.getEntreesByProduit(numProd);
                }
            };
            taskEntree.setOnSucceeded(ev -> dataEntree.setAll(taskEntree.getValue()));
            taskEntree.setOnFailed(ev -> {
                taskEntree.getException().printStackTrace();
                showAlert("Erreur", "Impossible de charger les entrées.");
            });
            new Thread(taskEntree).start();

            // Charger les sorties
            Task<List<BonSortie>> taskSortie = new Task<>() {
                @Override protected List<BonSortie> call() throws Exception {
                    return api.getSortiesByProduit(numProd);
                }
            };
            taskSortie.setOnSucceeded(ev -> dataSortie.setAll(taskSortie.getValue()));
            taskSortie.setOnFailed(ev -> {
                taskSortie.getException().printStackTrace();
                showAlert("Erreur", "Impossible de charger les sorties.");
            });
            new Thread(taskSortie).start();
        });

        // ── Layout ──
        VBox layout = new VBox(10,
                searchBar,
                new Label("📥 Bons d'Entrée :"),
                tableEntree,
                new Label("📤 Bons de Sortie :"),
                tableSortie
        );
        layout.setPadding(new Insets(15));
        return layout;
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
