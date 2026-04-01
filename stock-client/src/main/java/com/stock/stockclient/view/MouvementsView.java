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

        // ── Barre search ──
        tfNumProduit.setPromptText("Filtrer par N° Produit...");
        tfNumProduit.setPrefWidth(250);
        Button btnTous = new Button("🔄 Tous");
        btnTous.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        HBox searchBar = new HBox(10,
                new Label("Filtrer :"), tfNumProduit, btnTous);
        searchBar.setPadding(new Insets(10));

        // ── Colonnes Entrées ──
        TableColumn<BonEntree, String>  colNumE  = new TableColumn<>("N° Bon");
        TableColumn<BonEntree, String>  colProdE = new TableColumn<>("N° Produit");
        TableColumn<BonEntree, Integer> colQteE  = new TableColumn<>("Qté Entrée");
        TableColumn<BonEntree, String>  colDateE = new TableColumn<>("Date");

        colNumE.setCellValueFactory(new PropertyValueFactory<>("numBonEntree"));
        colProdE.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colQteE.setCellValueFactory(new PropertyValueFactory<>("qteEntree"));
        colDateE.setCellValueFactory(new PropertyValueFactory<>("dateEntree"));

        colNumE.setPrefWidth(150);
        colProdE.setPrefWidth(130);
        colQteE.setPrefWidth(120);
        colDateE.setPrefWidth(130);

        tableEntree.getColumns().addAll(colNumE, colProdE, colQteE, colDateE);
        tableEntree.setItems(dataEntree);
        tableEntree.setPrefHeight(200);

        // ── Colonnes Sorties ──
        TableColumn<BonSortie, String>  colNumS  = new TableColumn<>("N° Bon");
        TableColumn<BonSortie, String>  colProdS = new TableColumn<>("N° Produit");
        TableColumn<BonSortie, Integer> colQteS  = new TableColumn<>("Qté Sortie");
        TableColumn<BonSortie, String>  colDateS = new TableColumn<>("Date");

        colNumS.setCellValueFactory(new PropertyValueFactory<>("numBonSortie"));
        colProdS.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colQteS.setCellValueFactory(new PropertyValueFactory<>("qteSortie"));
        colDateS.setCellValueFactory(new PropertyValueFactory<>("dateSortie"));

        colNumS.setPrefWidth(150);
        colProdS.setPrefWidth(130);
        colQteS.setPrefWidth(120);
        colDateS.setPrefWidth(130);

        tableSortie.getColumns().addAll(colNumS, colProdS, colQteS, colDateS);
        tableSortie.setItems(dataSortie);
        tableSortie.setPrefHeight(200);

        tfNumProduit.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.trim();
            if (keyword.isEmpty()) {
                chargerTous();
            } else {
                filtrerParProduit(keyword);
            }
        });

        btnTous.setOnAction(e -> {
            tfNumProduit.clear();
            chargerTous();
        });

        VBox layout = new VBox(10,
                searchBar,
                new Label("📥 Bons d'Entrée :"),
                tableEntree,
                new Label("📤 Bons de Sortie :"),
                tableSortie
        );
        layout.setPadding(new Insets(15));

        chargerTous();
        return layout;
    }

    // Charge all move
    private void chargerTous() {
        Task<List<BonEntree>> taskE = new Task<>() {
            @Override protected List<BonEntree> call() throws Exception {
                return api.getAllBonEntrees();
            }
        };
        taskE.setOnSucceeded(e -> dataEntree.setAll(taskE.getValue()));
        taskE.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les entrées."));
        new Thread(taskE).start();

        Task<List<BonSortie>> taskS = new Task<>() {
            @Override protected List<BonSortie> call() throws Exception {
                return api.getAllBonSorties();
            }
        };
        taskS.setOnSucceeded(e -> dataSortie.setAll(taskS.getValue()));
        taskS.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les sorties."));
        new Thread(taskS).start();
    }

    private void filtrerParProduit(String numProduit) {
        Task<List<BonEntree>> taskE = new Task<>() {
            @Override protected List<BonEntree> call() throws Exception {
                return api.getEntreesByProduit(numProduit);
            }
        };
        taskE.setOnSucceeded(e -> dataEntree.setAll(taskE.getValue()));
        taskE.setOnFailed(e -> dataEntree.clear());
        new Thread(taskE).start();

        Task<List<BonSortie>> taskS = new Task<>() {
            @Override protected List<BonSortie> call() throws Exception {
                return api.getSortiesByProduit(numProduit);
            }
        };
        taskS.setOnSucceeded(e -> dataSortie.setAll(taskS.getValue()));
        taskS.setOnFailed(e -> dataSortie.clear());
        new Thread(taskS).start();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}