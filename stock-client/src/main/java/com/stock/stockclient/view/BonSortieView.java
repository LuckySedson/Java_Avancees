package com.stock.stockclient.view;

import com.stock.stockclient.model.BonSortie;
import com.stock.stockclient.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BonSortieView {

    private final ApiService api = new ApiService();
    private final TableView<BonSortie> table = new TableView<>();
    private final ObservableList<BonSortie> data = FXCollections.observableArrayList();

    private final TextField tfNumBon      = new TextField();
    private final ComboBox<String> cbNumProd = new ComboBox<>();
    private final TextField tfQte         = new TextField();
    private final DatePicker dpDate       = new DatePicker();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yy");

    public VBox getView() {

        // ── Colonnes ──
        TableColumn<BonSortie, String>  colNum  = new TableColumn<>("N° Bon");
        TableColumn<BonSortie, String>  colProd = new TableColumn<>("N° Produit");
        TableColumn<BonSortie, Integer> colQte  = new TableColumn<>("Qté Sortie");
        TableColumn<BonSortie, String>  colDate = new TableColumn<>("Date");

        colNum.setCellValueFactory(new PropertyValueFactory<>("numBonSortie"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("numProduit"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteSortie"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSortie"));

        colNum.setPrefWidth(150);
        colProd.setPrefWidth(150);
        colQte.setPrefWidth(120);
        colDate.setPrefWidth(150);

        table.getColumns().addAll(colNum, colProd, colQte, colDate);
        table.setItems(data);

        // ── Formulaire ──
        tfNumBon.setPromptText("N° Bon Sortie  ex: BS01");
        cbNumProd.setPromptText("Choisir un produit...");
        cbNumProd.setPrefWidth(200);
        cbNumProd.setEditable(false);
        tfQte.setPromptText("Quantité");
        dpDate.setPromptText("Choisir une date");
        dpDate.setPrefWidth(200);

        // Bloquer les lettres dans tfQte
        tfQte.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tfQte.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("N° Bon :"),     0, 0); form.add(tfNumBon,   1, 0);
        form.add(new Label("N° Produit :"), 0, 1); form.add(cbNumProd,  1, 1);
        form.add(new Label("Quantité :"),   0, 2); form.add(tfQte,      1, 2);
        form.add(new Label("Date :"),       0, 3); form.add(dpDate,     1, 3);

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

        // ── Action Ajouter ──
        btnAjouter.setOnAction(e -> {
            if (!validerFormulaire()) return;

            BonSortie bs = new BonSortie();
            bs.setNumBonSortie(tfNumBon.getText().trim());
            bs.setNumProduit(getNumProduitSelectionne());
            bs.setQteSortie(Integer.parseInt(tfQte.getText().trim()));
            bs.setDateSortie(dpDate.getValue().format(FORMATTER));

            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    api.addBonSortie(bs);
                    return null;
                }
            };
            task.setOnSucceeded(ev -> { clearForm(); chargerDonnees(); });
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        // ── Action Modifier ──
        btnModifier.setOnAction(e -> {
            BonSortie selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Sélectionne un bon à modifier.");
                return;
            }
            if (!validerFormulaire()) return;

            BonSortie bs = new BonSortie();
            bs.setNumBonSortie(selected.getNumBonSortie());
            bs.setNumProduit(getNumProduitSelectionne());
            bs.setQteSortie(Integer.parseInt(tfQte.getText().trim()));
            bs.setDateSortie(dpDate.getValue().format(FORMATTER));

            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    api.updateBonSortie(bs);
                    return null;
                }
            };
            task.setOnSucceeded(ev -> {
                showAlert("Succès", "✅ Bon de sortie modifié !");
                clearForm();
                chargerDonnees();
            });
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        // ── Action Supprimer ──
        btnSupprimer.setOnAction(e -> {
            BonSortie selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Attention", "Sélectionne un bon."); return; }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer ce bon de sortie ?");
            confirm.setContentText(
                    "N° Bon : "     + selected.getNumBonSortie() +
                            "\nProduit : "  + selected.getNumProduit()   +
                            "\nQuantité : " + selected.getQteSortie()
            );

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> task = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.deleteBonSortie(selected.getNumBonSortie());
                            return null;
                        }
                    };
                    task.setOnSucceeded(ev -> { clearForm(); chargerDonnees(); });
                    task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
                    new Thread(task).start();
                }
            });
        });

        // ── Action Rafraîchir ──
        btnRafraichir.setOnAction(e -> { clearForm(); chargerDonnees(); });

        // ── Remplir formulaire au clic sur une ligne ──
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfNumBon.setText(sel.getNumBonSortie());
                tfNumBon.setDisable(true);
                String numProd = sel.getNumProduit();
                cbNumProd.getItems().stream()
                        .filter(item -> item.startsWith(numProd))
                        .findFirst()
                        .ifPresent(cbNumProd::setValue);
                tfQte.setText(String.valueOf(sel.getQteSortie()));
                try {
                    dpDate.setValue(LocalDate.parse(sel.getDateSortie(), FORMATTER));
                } catch (Exception ex) {
                    dpDate.setValue(null);
                }
            }
        });

        VBox layout = new VBox(10, table, form, boutons);
        layout.setPadding(new Insets(15));
        chargerProduits();
        chargerDonnees();
        return layout;
    }

    private void chargerDonnees() {
        Task<List<BonSortie>> task = new Task<>() {
            @Override protected List<BonSortie> call() throws Exception {
                return api.getAllBonSorties();
            }
        };
        task.setOnSucceeded(e -> data.setAll(task.getValue()));
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showAlert("Erreur de connexion",
                    "Vérifie que le serveur SpringBoot est lancé.\n"
                            + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    // ── public pour être appelé depuis MainApp ──
    public void chargerProduits() {
        Task<List<String>> task = new Task<>() {
            @Override protected List<String> call() throws Exception {
                return api.getAllProduits()
                        .stream()
                        .map(p -> p.getNumProduit() + " — " + p.getDesign())
                        .toList();
            }
        };
        task.setOnSucceeded(e -> cbNumProd.setItems(
                FXCollections.observableArrayList(task.getValue())
        ));
        task.setOnFailed(e -> showAlert("Erreur", "Impossible de charger les produits."));
        new Thread(task).start();
    }

    private String getNumProduitSelectionne() {
        String selected = cbNumProd.getValue();
        if (selected == null) return "";
        return selected.split(" — ")[0].trim();
    }

    private boolean validerFormulaire() {
        String numBon = tfNumBon.getText().trim();
        String qte    = tfQte.getText().trim();

        if (numBon.isEmpty() || qte.isEmpty()) {
            showAlert("Validation", "❌ Tous les champs sont obligatoires.");
            return false;
        }

        if (!numBon.matches("[A-Za-z0-9]+")) {
            showAlert("Validation",
                    "❌ Le N° Bon ne doit contenir que des lettres et chiffres.\nExemple : BS01");
            return false;
        }

        if (cbNumProd.getValue() == null) {
            showAlert("Validation", "❌ Veuillez choisir un produit.");
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

        if (dpDate.getValue() == null) {
            showAlert("Validation", "❌ Veuillez choisir une date.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        tfNumBon.clear();
        tfNumBon.setDisable(false);
        cbNumProd.setValue(null);
        tfQte.clear();
        dpDate.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}