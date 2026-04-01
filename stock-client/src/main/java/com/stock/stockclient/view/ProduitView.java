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
    private final TextField tfSearchCode = new TextField();

    public VBox getView() {
        // ── Colonnes ──
        TableColumn<Produit, String>  colNum    = new TableColumn<>("N° Produit");
        TableColumn<Produit, String>  colDesign = new TableColumn<>("Désignation");
        TableColumn<Produit, Integer> colStock  = new TableColumn<>("Stock");

        colNum.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colDesign.setCellValueFactory(new PropertyValueFactory<>("design"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colNum.setPrefWidth(150);
        colDesign.setPrefWidth(280);
        colStock.setPrefWidth(120);

        table.getColumns().addAll(colNum, colDesign, colStock);
        table.setItems(data);

        // ── Recherche unifiée ──
        TextField tfSearch = new TextField();
        tfSearch.setPromptText("🔍 Rechercher par code ou désignation...");
        tfSearch.setPrefWidth(400);

        HBox searchBar = new HBox(10, new Label("Recherche :"), tfSearch);
        searchBar.setPadding(new Insets(10));

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.trim();

            Task<List<Produit>> task = new Task<>() {
                @Override protected List<Produit> call() throws Exception {
                    if (keyword.isEmpty()) {
                        return api.getAllProduits();
                    }
                    try {
                        Produit p = api.getProduitById(keyword);
                        if (p != null) return List.of(p);
                    } catch (Exception ignored) {}
                    return api.searchProduits(keyword);
                }
            };
            task.setOnSucceeded(e -> data.setAll(task.getValue()));
            task.setOnFailed(e -> data.setAll(
                    FXCollections.observableArrayList()
            ));
            new Thread(task).start();
        });

        // ── Formulaire ──
        tfNumProduit.setPromptText("N° Produit");
        tfDesign.setPromptText("Désignation");
        tfStock.setPromptText("Stock");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("N° Produit :"),  0, 0); form.add(tfNumProduit, 1, 0);
        form.add(new Label("Désignation :"), 0, 1); form.add(tfDesign,     1, 1);
        form.add(new Label("Stock :"),       0, 2); form.add(tfStock,      1, 2);

        // ── Boutons ──
        Button btnAjouter    = new Button("➕ Ajouter");
        Button btnModifier   = new Button("✏️ Modifier");
        Button btnSupprimer  = new Button("🗑 Supprimer");
        Button btnRafraichir = new Button("🔄 Rafraîchir");

        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnRafraichir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        HBox boutons = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
        boutons.setPadding(new Insets(10));

        // ── Remplir form au clic sur une ligne ──
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfNumProduit.setText(sel.getNumProduit());
                tfNumProduit.setDisable(true);
                tfDesign.setText(sel.getDesign());
                tfStock.setText(String.valueOf(sel.getStock()));
            }
        });

        // ── Actions ──
        btnAjouter.setOnAction(e -> {
            tfNumProduit.setDisable(false);
            if (!validerFormulaire()) return;
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

        btnModifier.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Sélectionne un produit à modifier.");
                return;
            }
            if (!validerFormulaire()) return;

            try {
                Produit p = new Produit();
                p.setNumProduit(selected.getNumProduit());
                p.setDesign(tfDesign.getText().trim());
                p.setStock(Integer.parseInt(tfStock.getText().trim()));

                Task<Void> task = new Task<>() {
                    @Override protected Void call() throws Exception {
                        api.updateProduit(p);
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> {
                    showAlert("Succès", "Produit modifié avec succès !");
                    clearForm();
                    chargerDonnees();
                });
                task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
                new Thread(task).start();

            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Le stock doit être un nombre entier.");
            }
        });

        btnSupprimer.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Sélectionne un produit.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer le produit ?");
            confirm.setContentText("Voulez-vous vraiment supprimer : "
                    + selected.getDesign() + " (" + selected.getNumProduit() + ") ?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> task = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.deleteProduit(selected.getNumProduit());
                            return null;
                        }
                    };
                    task.setOnSucceeded(ev -> { clearForm(); chargerDonnees(); });
                    task.setOnFailed(ev -> {
                        // Clé étrangère ou autre erreur
                        String erreur = task.getException().getMessage();
                        if (erreur != null && (
                                erreur.contains("foreign key") ||
                                        erreur.contains("constraint") ||
                                        erreur.contains("violates") ||
                                        erreur.contains("500"))) {
                            showAlert("Suppression impossible ❌",
                                    "Ce produit est utilisé dans un Bon d'Entrée ou un Bon de Sortie.\n\n"
                                            + "Supprimez d'abord les bons associés avant de supprimer ce produit.");
                        } else {
                            showAlert("Erreur", erreur);
                        }
                    });
                    new Thread(task).start();
                }
            });
        });

        btnRafraichir.setOnAction(e -> { clearForm(); chargerDonnees(); });

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

    private boolean validerFormulaire() {
        String num    = tfNumProduit.getText().trim();
        String design = tfDesign.getText().trim();
        String stock  = tfStock.getText().trim();

        if (num.isEmpty() || design.isEmpty() || stock.isEmpty()) {
            showAlert("Validation", "❌ Tous les champs sont obligatoires.");
            return false;
        }

        if (!num.matches("[A-Za-z0-9]+")) {
            showAlert("Validation", "❌ Le N° Produit ne doit contenir que des lettres et chiffres.\nExemple : P01");
            return false;
        }

        try {
            int s = Integer.parseInt(stock);
            if (s < 0) {
                showAlert("Validation", "❌ Le stock ne peut pas être négatif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation", "❌ Le stock doit être un nombre entier.");
            return false;
        }

        if (design.length() < 2) {
            showAlert("Validation", "❌ La désignation doit contenir au moins 2 caractères.");
            return false;
        }

        return true;
    }

    public void refresh() {
        chargerDonnees();
    }

    private void clearForm() {
        tfNumProduit.clear();
        tfNumProduit.setDisable(false);
        tfDesign.clear();
        tfStock.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}