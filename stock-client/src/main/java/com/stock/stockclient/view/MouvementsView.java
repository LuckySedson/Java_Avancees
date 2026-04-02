package com.stock.stockclient.view;

import com.stock.stockclient.model.BonEntree;
import com.stock.stockclient.model.BonSortie;
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

import java.util.ArrayList;
import java.util.List;

public class MouvementsView {

    private final ApiService api = new ApiService();

    private final TableView<LigneMouvement> table = new TableView<>();
    private final ObservableList<LigneMouvement> data = FXCollections.observableArrayList();

    private final ComboBox<String> cbProduit = new ComboBox<>();
    private final Label lblDesignation = new Label("Désignation : ");

    public static class LigneMouvement {
        private final String numBon;
        private final String entree;
        private final String sortie;
        private final String date;

        public LigneMouvement(String numBon, String entree, String sortie, String date) {
            this.numBon  = numBon;
            this.entree  = entree;
            this.sortie  = sortie;
            this.date    = date;
        }

        public String getNumBon()  { return numBon; }
        public String getEntree()  { return entree; }
        public String getSortie()  { return sortie; }
        public String getDate()    { return date; }
    }

    public VBox getView() {

        // ── Label désignation ──
        lblDesignation.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblDesignation.setPadding(new Insets(5, 0, 5, 0));

        // ── ComboBox produits ──
        cbProduit.setPromptText("Tous les produits");
        cbProduit.setPrefWidth(250);
        cbProduit.setEditable(false);

        Button btnTous = new Button("🔄 Tous");
        btnTous.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        HBox searchBar = new HBox(10,
                new Label("Produit :"), cbProduit, btnTous);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER_LEFT);

        // ── Titre tableau ──
        Label titreTableau = new Label("ÉTAT DES MOUVEMENTS DE STOCK");
        titreTableau.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titreTableau.setUnderline(true);

        // ── Colonnes tableau unifié ──
        TableColumn<LigneMouvement, String> colBon    = new TableColumn<>("N° BON");
        TableColumn<LigneMouvement, String> colEntree = new TableColumn<>("ENTRÉE");
        TableColumn<LigneMouvement, String> colSortie = new TableColumn<>("SORTIE");
        TableColumn<LigneMouvement, String> colDate   = new TableColumn<>("DATE");

        colBon.setCellValueFactory(new PropertyValueFactory<>("numBon"));
        colEntree.setCellValueFactory(new PropertyValueFactory<>("entree"));
        colSortie.setCellValueFactory(new PropertyValueFactory<>("sortie"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colBon.setPrefWidth(150);
        colEntree.setPrefWidth(150);
        colSortie.setPrefWidth(150);
        colDate.setPrefWidth(150);

        table.getColumns().addAll(colBon, colEntree, colSortie, colDate);
        table.setItems(data);

        // ── Actions ──
        cbProduit.setOnAction(e -> {
            String selected = cbProduit.getValue();
            if (selected == null) {
                lblDesignation.setText("Désignation : ");
                chargerTous();
            } else {
                String[] parts = selected.split(" — ");
                String numProd = parts[0].trim();
                String design  = parts.length > 1 ? parts[1].trim() : "";
                lblDesignation.setText("Désignation : " + design);
                filtrerParProduit(numProd);
            }
        });

        btnTous.setOnAction(e -> {
            cbProduit.setValue(null);
            lblDesignation.setText("Désignation : ");
            chargerTous();
        });

        // ── Layout ──
        VBox layout = new VBox(10,
                searchBar,
                lblDesignation,
                titreTableau,
                table
        );
        layout.setPadding(new Insets(15));

        // Charger les produits dans ComboBox + tous les mouvements
        chargerProduits();
        chargerTous();
        return layout;
    }

    public void refresh() {
        chargerProduits();
        if (cbProduit.getValue() == null) {
            chargerTous();
        } else {
            String[] parts = cbProduit.getValue().split(" — ");
            filtrerParProduit(parts[0].trim());
        }
    }

    private void chargerProduits() {
        Task<List<String>> task = new Task<>() {
            @Override protected List<String> call() throws Exception {
                return api.getAllProduits()
                        .stream()
                        .map(p -> p.getNumProduit() + " — " + p.getDesign())
                        .toList();
            }
        };
        task.setOnSucceeded(e -> {
            String current = cbProduit.getValue();
            cbProduit.setItems(FXCollections.observableArrayList(task.getValue()));
            cbProduit.setValue(current); // garder la sélection actuelle
        });
        task.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les produits."));
        new Thread(task).start();
    }

    private void chargerTous() {
        Task<List<LigneMouvement>> task = new Task<>() {
            @Override protected List<LigneMouvement> call() throws Exception {
                List<LigneMouvement> lignes = new ArrayList<>();

                for (BonEntree be : api.getAllBonEntrees()) {
                    lignes.add(new LigneMouvement(
                            be.getNumBonEntree(),
                            String.valueOf(be.getQteEntree()),
                            "",
                            be.getDateEntree()
                    ));
                }
                // Ajouter toutes les sorties
                for (BonSortie bs : api.getAllBonSorties()) {
                    lignes.add(new LigneMouvement(
                            bs.getNumBonSortie(),
                            "",
                            String.valueOf(bs.getQteSortie()),
                            bs.getDateSortie()
                    ));
                }
                return lignes;
            }
        };
        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les mouvements."));
        new Thread(task).start();
    }

    private void filtrerParProduit(String numProduit) {
        Task<List<LigneMouvement>> task = new Task<>() {
            @Override protected List<LigneMouvement> call() throws Exception {
                List<LigneMouvement> lignes = new ArrayList<>();

                for (BonEntree be : api.getEntreesByProduit(numProduit)) {
                    lignes.add(new LigneMouvement(
                            be.getNumBonEntree(),
                            String.valueOf(be.getQteEntree()),
                            "",
                            be.getDateEntree()
                    ));
                }
                for (BonSortie bs : api.getSortiesByProduit(numProduit)) {
                    lignes.add(new LigneMouvement(
                            bs.getNumBonSortie(),
                            "",
                            String.valueOf(bs.getQteSortie()),
                            bs.getDateSortie()
                    ));
                }
                return lignes;
            }
        };
        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les mouvements."));
        new Thread(task).start();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}