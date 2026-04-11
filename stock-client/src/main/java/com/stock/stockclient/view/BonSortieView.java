package com.stock.stockclient.view;

import com.stock.stockclient.model.BonSortie;
import com.stock.stockclient.service.ApiService;
import com.stock.stockclient.util.AlerteStyle;
import com.stock.stockclient.util.BoutonStyle;
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

    private final Label lblNumBon            = new Label("(généré automatiquement)");
    private final ComboBox<String> cbNumProd = new ComboBox<>();
    private final TextField tfQte            = new TextField();
    private final DatePicker dpDate          = new DatePicker();

    private final Button btnAjouter    = new Button("➕ Ajouter");
    private final Button btnModifier   = new Button("✏️ Modifier");
    private final Button btnSupprimer  = new Button("🗑 Supprimer");
    private final Button btnRafraichir = new Button("🔄 Rafraîchir");

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
        lblNumBon.setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
        cbNumProd.setPromptText("Choisir un produit...");
        cbNumProd.setPrefWidth(200);
        cbNumProd.setEditable(false);
        tfQte.setPromptText("Quantité");
        dpDate.setPromptText("Choisir une date");
        dpDate.setPrefWidth(200);

        tfQte.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tfQte.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("N° Bon :"),     0, 0); form.add(lblNumBon,  1, 0);
        form.add(new Label("N° Produit :"), 0, 1); form.add(cbNumProd,  1, 1);
        form.add(new Label("Quantité :"),   0, 2); form.add(tfQte,      1, 2);
        form.add(new Label("Date :"),       0, 3); form.add(dpDate,     1, 3);

        // ── Style boutons ──
        BoutonStyle.vert(btnAjouter);
        BoutonStyle.orange(btnModifier);
        BoutonStyle.rouge(btnSupprimer);
        BoutonStyle.bleu(btnRafraichir);

        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        HBox boutons = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
        boutons.setPadding(new Insets(10));

        // ── Animation sélection ──
        table.setRowFactory(tv -> {
            TableRow<BonSortie> row = new TableRow<>();
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

        // ── Listener ──
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean selectionne = (sel != null);
            btnAjouter.setDisable(selectionne);
            btnModifier.setDisable(!selectionne);
            btnSupprimer.setDisable(!selectionne);

            if (sel != null) {
                lblNumBon.setText(sel.getNumBonSortie());
                lblNumBon.setStyle("-fx-text-fill: black; -fx-font-style: normal;");
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

        // ── Action ──
        btnAjouter.setOnAction(e -> {
            if (!validerFormulaire()) return;

            String numProd  = getNumProduitSelectionne();
            int qteSortie   = Integer.parseInt(tfQte.getText().trim());

            Task<Integer> taskStock = new Task<>() {
                @Override protected Integer call() throws Exception {
                    return api.getProduitById(numProd).getStock();
                }
            };

            taskStock.setOnSucceeded(ev -> {
                int stockActuel = taskStock.getValue();
                if (qteSortie > stockActuel) {
                    AlerteStyle.erreur("Stock insuffisant ❌",
                            "Stock disponible : "   + stockActuel  +
                                    "\nQuantité demandée : " + qteSortie   +
                                    "\n\nImpossible d'effectuer cette sortie.");
                    return;
                }

                Task<Void> taskAdd = new Task<>() {
                    @Override protected Void call() throws Exception {
                        String idGenere = api.genererIdBonSortie();
                        BonSortie bs = new BonSortie();
                        bs.setNumBonSortie(idGenere);
                        bs.setNumProduit(numProd);
                        bs.setQteSortie(qteSortie);
                        bs.setDateSortie(dpDate.getValue().format(FORMATTER));
                        api.addBonSortie(bs);
                        return null;
                    }
                };
                taskAdd.setOnSucceeded(evv -> {
                    AlerteStyle.succes("Succès", "✅ Bon de sortie ajouté avec succès !");
                    clearForm();
                    chargerDonnees();
                });
                taskAdd.setOnFailed(evv ->
                        AlerteStyle.erreur("Erreur", taskAdd.getException().getMessage()));
                new Thread(taskAdd).start();
            });

            taskStock.setOnFailed(ev ->
                    AlerteStyle.erreur("Erreur", "Impossible de vérifier le stock."));
            new Thread(taskStock).start();
        });

        btnModifier.setOnAction(e -> {
            BonSortie selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlerteStyle.info("Attention", "Sélectionne un bon à modifier.");
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
                AlerteStyle.succes("Succès", "✅ Bon de sortie modifié avec succès !");
                clearForm();
                chargerDonnees();
            });
            task.setOnFailed(ev ->
                    AlerteStyle.erreur("Erreur", task.getException().getMessage()));
            new Thread(task).start();
        });

        btnSupprimer.setOnAction(e -> {
            BonSortie selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlerteStyle.info("Attention", "Sélectionne un bon.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer ce bon de sortie ?");
            confirm.setContentText(
                    "N° Bon : "    + selected.getNumBonSortie() +
                            "\nProduit : " + selected.getNumProduit()   +
                            "\nQuantité : "+ selected.getQteSortie()
            );

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> task = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.deleteBonSortie(selected.getNumBonSortie());
                            return null;
                        }
                    };
                    task.setOnSucceeded(ev -> {
                        AlerteStyle.succes("Succès", "✅ Bon de sortie supprimé avec succès !");
                        clearForm();
                        chargerDonnees();
                    });
                    task.setOnFailed(ev ->
                            AlerteStyle.erreur("Erreur", task.getException().getMessage()));
                    new Thread(task).start();
                }
            });
        });

        btnRafraichir.setOnAction(e -> { clearForm(); chargerDonnees(); });

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
            AlerteStyle.erreur("Erreur de connexion",
                    "Vérifie que le serveur SpringBoot est lancé.\n"
                            + task.getException().getMessage());
        });
        new Thread(task).start();
    }

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
        task.setOnFailed(e ->
                AlerteStyle.erreur("Erreur", "Impossible de charger les produits."));
        new Thread(task).start();
    }

    private String getNumProduitSelectionne() {
        String selected = cbNumProd.getValue();
        if (selected == null) return "";
        return selected.split(" — ")[0].trim();
    }

    private boolean validerFormulaire() {
        String qte = tfQte.getText().trim();

        if (getNumProduitSelectionne().isEmpty() || qte.isEmpty()) {
            AlerteStyle.info("Validation", "❌ Tous les champs sont obligatoires.");
            return false;
        }
        if (cbNumProd.getValue() == null) {
            AlerteStyle.info("Validation", "❌ Veuillez choisir un produit.");
            return false;
        }
        try {
            int q = Integer.parseInt(qte);
            if (q <= 0) {
                AlerteStyle.info("Validation", "❌ La quantité doit être supérieure à 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlerteStyle.info("Validation", "❌ La quantité doit être un nombre entier.");
            return false;
        }
        if (dpDate.getValue() == null) {
            AlerteStyle.info("Validation", "❌ Veuillez choisir une date.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        lblNumBon.setText("(généré automatiquement)");
        lblNumBon.setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
        cbNumProd.setValue(null);
        tfQte.clear();
        dpDate.setValue(null);
        table.getSelectionModel().clearSelection();
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
}