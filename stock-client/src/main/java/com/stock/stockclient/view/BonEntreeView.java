package com.stock.stockclient.view;

import com.stock.stockclient.model.BonEntree;
import com.stock.stockclient.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class BonEntreeView {
    private final ApiService api = new ApiService();
    private final TableView<BonEntree> table = new TableView<>();
    private final ObservableList<BonEntree> data = FXCollections.observableArrayList();

    private final TextField tfNumBon    = new TextField();
    private final TextField tfNumProd   = new TextField();
    private final TextField tfQte       = new TextField();
    private final TextField tfDate      = new TextField();

    public VBox getView() {
        // ── Colonnes ──
        TableColumn<BonEntree, String>  colNum  = new TableColumn<>("N° Bon");
        TableColumn<BonEntree, String>  colProd = new TableColumn<>("N° Produit");
        TableColumn<BonEntree, Integer> colQte  = new TableColumn<>("Qté Entrée");
        TableColumn<BonEntree, String>  colDate = new TableColumn<>("Date");

        colNum.setCellValueFactory(new PropertyValueFactory<>("numBonEntree"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteEntree"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEntree"));

        colNum.setPrefWidth(150);
        colProd.setPrefWidth(150);
        colQte.setPrefWidth(120);
        colDate.setPrefWidth(150);

        table.getColumns().addAll(colNum, colProd, colQte, colDate);
        table.setItems(data);

        // ── Formulaire ──
        tfNumBon.setPromptText("N° Bon Entrée");
        tfNumProd.setPromptText("N° Produit");
        tfQte.setPromptText("Quantité");
        tfDate.setPromptText("Date (jj/mm/aa)");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));

        form.add(new Label("N° Bon :"),    0, 0); form.add(tfNumBon,  1, 0);
        form.add(new Label("N° Produit :"),0, 1); form.add(tfNumProd, 1, 1);
        form.add(new Label("Quantité :"),  0, 2); form.add(tfQte,     1, 2);
        form.add(new Label("Date :"),      0, 3); form.add(tfDate,    1, 3);

        // ── Boutons ──
        Button btnAjouter    = new Button("➕ Ajouter");
        Button btnSupprimer  = new Button("🗑 Supprimer");
        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        Button btnRafraichir = new Button("🔄 Rafraîchir");

        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnRafraichir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        HBox boutons = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
        boutons.setPadding(new Insets(10));

        // ── Actions ──
        btnAjouter.setOnAction(e -> {
            if (!validerFormulaire()) return;
            try {
                BonEntree be = new BonEntree();
                be.setNumBonEntree(tfNumBon.getText().trim());
                be.setNumProduit(tfNumProd.getText().trim());
                be.setQteEntree(Integer.parseInt(tfQte.getText().trim()));
                be.setDateEntree(tfDate.getText().trim());

                Task<Void> task = new Task<>() {
                    @Override protected Void call() throws Exception {
                        api.addBonEntree(be);
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> { clearForm(); chargerDonnees(); });
                task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
                new Thread(task).start();

            } catch (NumberFormatException ex) {
                showAlert("Erreur", "La quantité doit être un nombre entier.");
            }
        });

        btnSupprimer.setOnAction(e -> {
            BonEntree selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Attention", "Sélectionne un bon à supprimer."); return; }

            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    api.deleteBonEntree(selected.getNumBonEntree());
                    return null;
                }
            };
            task.setOnSucceeded(ev -> chargerDonnees());
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        btnModifier.setOnAction(e -> {
            BonEntree selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Sélectionne un bon à modifier.");
                return;
            }
            if (!validerFormulaire()) return;
            try {
                BonEntree be = new BonEntree();
                be.setNumBonEntree(selected.getNumBonEntree()); // ID inchangé
                be.setNumProduit(tfNumProd.getText().trim());
                be.setQteEntree(Integer.parseInt(tfQte.getText().trim()));
                be.setDateEntree(tfDate.getText().trim());

                Task<Void> task = new Task<>() {
                    @Override protected Void call() throws Exception {
                        api.updateBonEntree(be); // PUT /api/bonentrees/{id}
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> {
                    showAlert("Succès", "Bon d'entrée modifié !");
                    clearForm();
                    chargerDonnees();
                });
                task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
                new Thread(task).start();

            } catch (NumberFormatException ex) {
                showAlert("Erreur", "La quantité doit être un nombre entier.");
            }
        });

        btnRafraichir.setOnAction(e -> chargerDonnees());

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfNumBon.setText(sel.getNumBonEntree());
                tfNumBon.setDisable(true);
                tfNumProd.setText(sel.getNumProduit());
                tfQte.setText(String.valueOf(sel.getQteEntree()));
                tfDate.setText(sel.getDateEntree());
            }
        });

        VBox layout = new VBox(10, table, form, boutons);
        layout.setPadding(new Insets(15));
        chargerDonnees();
        return layout;
    }

    private void chargerDonnees() {
        Task<List<BonEntree>> task = new Task<>() {
            @Override protected List<BonEntree> call() throws Exception {
                return api.getAllBonEntrees();
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
        String numBon  = tfNumBon.getText().trim();
        String numProd = tfNumProd.getText().trim();
        String qte     = tfQte.getText().trim();
        String date    = tfDate.getText().trim();

        if (numBon.isEmpty() || numProd.isEmpty() || qte.isEmpty() || date.isEmpty()) {
            showAlert("Validation", "❌ Tous les champs sont obligatoires.");
            return false;
        }

        if (!numBon.matches("[A-Za-z0-9]+")) {
            showAlert("Validation", "❌ Le N° Bon ne doit contenir que des lettres et chiffres.\nExemple : BE01");
            return false;
        }

        if (!numProd.matches("[A-Za-z0-9]+")) {
            showAlert("Validation", "❌ Le N° Produit ne doit contenir que des lettres et chiffres.\nExemple : P01");
            return false;
        }

        try {
            int q = Integer.parseInt(qte);
            if (q <= 0) {
                showAlert("Validation", "❌ La quantité doit être supérieure à 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation", "❌ La quantité doit être un nombre entier.");
            return false;
        }

        if (!date.matches("\\d{2}/\\d{2}/(\\d{2}|\\d{4})")) {
            showAlert("Validation", "❌ Format de date invalide.\nUtilise : jj/mm/aa  ex: 01/04/26");
            return false;
        }

        return true;
    }

    private void clearForm() {
        tfNumBon.clear();
        tfNumBon.setDisable(false);
        tfNumProd.clear();
        tfQte.clear();
        tfDate.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
