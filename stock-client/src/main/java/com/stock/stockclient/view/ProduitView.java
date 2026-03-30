package com.stock.stockclient.view;

import com.stock.stockclient.model.Produit;
import com.stock.stockclient.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class ProduitView {
    private final ApiService api = new ApiService();
    private final TableView<Produit> table = new TableView<>();
    private final ObservableList<Produit> data = FXCollections.observableArrayList();

    private final TextField tfNumProduit = new TextField();
    private final TextField tfDesign     = new TextField();
    private final TextField tfStock      = new TextField();
    private final TextField tfSearch     = new TextField();

    public VBox getView() {
        TableColumn<Produit, String>  colNum    = new TableColumn<>("N° Produit");
        TableColumn<Produit, String>  colDesign = new TableColumn<>("Désignation");
        TableColumn<Produit, Integer> colStock  = new TableColumn<>("Stock");

        colNum.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colDesign.setCellValueFactory(new PropertyValueFactory<>("design"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colNum.setPrefWidth(150);
        colDesign.setPrefWidth(300);
        colStock.setPrefWidth(150);

        table.getColumns().addAll(colNum, colDesign, colStock);
        table.setItems(data);

        tfNumProduit.setPromptText("N° Produit");
        tfDesign.setPromptText("Désignation");
        tfStock.setPromptText("Stock initial");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("N° Produit :"),  0, 0); form.add(tfNumProduit, 1, 0);
        form.add(new Label("Désignation :"), 0, 1); form.add(tfDesign,     1, 1);
        form.add(new Label("Stock :"),       0, 2); form.add(tfStock,      1, 2);

        Button btnAjouter    = new Button("➕ Ajouter");
        Button btnSupprimer  = new Button("🗑 Supprimer");
        Button btnRafraichir = new Button("🔄 Rafraîchir");
        Button btnSearch     = new Button("Rechercher");

        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnRafraichir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        tfSearch.setPromptText("🔍 Rechercher par désignation...");
        tfSearch.setPrefWidth(300);

        HBox searchBar = new HBox(10, tfSearch, btnSearch);
        searchBar.setPadding(new Insets(10));

        HBox boutons = new HBox(10, btnAjouter, btnSupprimer, btnRafraichir);
        boutons.setPadding(new Insets(10));

        // ── Actions avec Task ──
        btnAjouter.setOnAction(e -> {
            try {
                Produit p = new Produit();
                p.setNumProduit(tfNumProduit.getText().trim());
                p.setDesign(tfDesign.getText().trim());
                p.setStock(Integer.parseInt(tfStock.getText().trim()));

                Task<Void> task = new Task<>() {
                    @Override protected Void call() throws Exception {
                        api.addProduit(p);
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> { clearForm(); chargerDonnees(); });
                task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
                new Thread(task).start();

            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Le stock doit être un nombre entier.");
            }
        });

        btnSupprimer.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Attention", "Sélectionne un produit."); return; }

            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    api.deleteProduit(selected.getNumProduit());
                    return null;
                }
            };
            task.setOnSucceeded(ev -> chargerDonnees());
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        btnRafraichir.setOnAction(e -> chargerDonnees());

        btnSearch.setOnAction(e -> {
            String keyword = tfSearch.getText().trim();
            Task<List<Produit>> task = new Task<>() {
                @Override protected List<Produit> call() throws Exception {
                    return keyword.isEmpty()
                            ? api.getAllProduits()
                            : api.searchProduits(keyword);
                }
            };
            task.setOnSucceeded(ev -> data.setAll(task.getValue()));
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfNumProduit.setText(sel.getNumProduit());
                tfDesign.setText(sel.getDesign());
                tfStock.setText(String.valueOf(sel.getStock()));
            }
        });

        VBox layout = new VBox(10, searchBar, table, form, boutons);
        layout.setPadding(new Insets(15));
        chargerDonnees();
        return layout;
    }

    private void chargerDonnees() {
        Task<List<Produit>> task = new Task<>() {
            @Override protected List<Produit> call() throws Exception {
                return api.getAllProduits();
            }
        };
        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showAlert("Erreur de connexion", "Vérifie que le serveur SpringBoot est lancé.\n"
                    + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private void clearForm() {
        tfNumProduit.clear(); tfDesign.clear(); tfStock.clear();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
