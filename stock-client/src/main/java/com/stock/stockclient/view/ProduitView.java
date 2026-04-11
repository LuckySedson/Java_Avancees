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
import com.stock.stockclient.util.BoutonStyle;
import com.stock.stockclient.util.AlerteStyle;

import java.util.List;

public class ProduitView {

    private final ApiService api = new ApiService();
    private final TableView<Produit> table = new TableView<>();
    private final ObservableList<Produit> data = FXCollections.observableArrayList();

    private final TextField tfNumProduit = new TextField();
    private final TextField tfDesign     = new TextField();
    private final TextField tfStock      = new TextField();

    private final Button btnAjouter    = new Button("➕ Ajouter");
    private final Button btnModifier   = new Button("✏️ Modifier");
    private final Button btnSupprimer  = new Button("🗑 Supprimer");
    private final Button btnRafraichir = new Button("🔄 Rafraîchir");

    private Runnable onProduitChange;

    public void setOnProduitChange(Runnable callback) {
        this.onProduitChange = callback;
    }

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
                    if (keyword.isEmpty()) return api.getAllProduits();
                    try {
                        Produit p = api.getProduitById(keyword);
                        if (p != null) return List.of(p);
                    } catch (Exception ignored) {}
                    return api.searchProduits(keyword);
                }
            };
            task.setOnSucceeded(e -> data.setAll(task.getValue()));
            task.setOnFailed(e -> data.setAll(
                    FXCollections.observableArrayList()));
            new Thread(task).start();
        });

        // ── Formulaire ──
        tfNumProduit.setPromptText("N° Produit  ex: P01");
        tfDesign.setPromptText("Désignation");
        tfStock.setPromptText("Stock");

        tfStock.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tfStock.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("N° Produit :"),  0, 0); form.add(tfNumProduit, 1, 0);
        form.add(new Label("Désignation :"), 0, 1); form.add(tfDesign,     1, 1);
        form.add(new Label("Stock :"),       0, 2); form.add(tfStock,      1, 2);

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

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean selectionne = (sel != null);
            btnAjouter.setDisable(selectionne);
            btnModifier.setDisable(!selectionne);
            btnSupprimer.setDisable(!selectionne);

            if (sel != null) {
                tfNumProduit.setText(sel.getNumProduit());
                tfNumProduit.setDisable(true);
                tfDesign.setText(sel.getDesign());
                tfStock.setText(String.valueOf(sel.getStock()));
            }
        });

        // ── Action ──
        btnAjouter.setOnAction(e -> {
            tfNumProduit.setDisable(false);
            if (!validerFormulaire()) return;

            String numProduit = tfNumProduit.getText().trim();
            String design     = tfDesign.getText().trim();
            int stock         = Integer.parseInt(tfStock.getText().trim());

            // Vérifier si produit existe
            Task<Produit> taskVerif = new Task<>() {
                @Override protected Produit call() throws Exception {
                    try { return api.getProduitById(numProduit); }
                    catch (Exception ex) { return null; }
                }
            };

            taskVerif.setOnSucceeded(ev -> {
                Produit existant = taskVerif.getValue();

                if (existant != null) {
                    Alert choix = new Alert(Alert.AlertType.CONFIRMATION);
                    choix.setTitle("Produit existant");
                    choix.setHeaderText("⚠️ Le produit " + numProduit + " existe déjà !");
                    choix.setContentText(
                            "Désignation actuelle : " + existant.getDesign() +
                                    "\nStock actuel : "        + existant.getStock() +
                                    "\n\nQue voulez-vous faire ?"
                    );
                    ButtonType btnModif   = new ButtonType("✏️ Modifier");
                    ButtonType btnAnnuler = new ButtonType("❌ Annuler",
                            ButtonBar.ButtonData.CANCEL_CLOSE);
                    choix.getButtonTypes().setAll(btnModif, btnAnnuler);

                    choix.showAndWait().ifPresent(response -> {
                        if (response == btnModif) {
                            Produit p = new Produit();
                            p.setNumProduit(numProduit);
                            p.setDesign(design);
                            p.setStock(stock);

                            Task<Void> taskUpdate = new Task<>() {
                                @Override protected Void call() throws Exception {
                                    api.updateProduit(p);
                                    return null;
                                }
                            };
                            taskUpdate.setOnSucceeded(evv -> {
                                AlerteStyle.succes("Succès", "✅ Produit modifié avec succès !");
                                clearForm();
                                chargerDonnees();
                                if (onProduitChange != null) onProduitChange.run();
                            });
                            taskUpdate.setOnFailed(evv ->
                                    AlerteStyle.erreur("Erreur", taskUpdate.getException().getMessage()));
                            new Thread(taskUpdate).start();
                        }
                    });
                } else {
                    Produit p = new Produit();
                    p.setNumProduit(numProduit);
                    p.setDesign(design);
                    p.setStock(stock);

                    Task<Void> taskAdd = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.addProduit(p);
                            return null;
                        }
                    };
                    taskAdd.setOnSucceeded(evv -> {
                        AlerteStyle.succes("Succès", "✅ Produit ajouté avec succès !");
                        clearForm();
                        chargerDonnees();
                        if (onProduitChange != null) onProduitChange.run();
                    });
                    taskAdd.setOnFailed(evv ->
                            AlerteStyle.erreur("Erreur", taskAdd.getException().getMessage()));
                    new Thread(taskAdd).start();
                }
            });
            taskVerif.setOnFailed(ev ->
                    AlerteStyle.erreur("Erreur", "Impossible de vérifier le produit."));
            new Thread(taskVerif).start();
        });

        btnModifier.setOnAction(e -> {
            Produit selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Attention", "Sélectionne un produit à modifier.");
                return;
            }
            if (!validerFormulaire()) return;

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
                AlerteStyle.succes("Succès", "✅ Produit modifié avec succès !");
                clearForm();
                chargerDonnees();
                if (onProduitChange != null) onProduitChange.run();
            });
            task.setOnFailed(ev -> showAlert("Erreur", task.getException().getMessage()));
            new Thread(task).start();
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
                    + selected.getDesign()
                    + " (" + selected.getNumProduit() + ") ?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Task<Void> task = new Task<>() {
                        @Override protected Void call() throws Exception {
                            api.deleteProduit(selected.getNumProduit());
                            return null;
                        }
                    };
                    task.setOnSucceeded(ev -> {
                        AlerteStyle.succes("Succès", "✅ Produit supprimé avec succès !");
                        clearForm();
                        chargerDonnees();
                        if (onProduitChange != null) onProduitChange.run();
                    });
                    task.setOnFailed(ev -> {
                        String erreur = task.getException().getMessage();
                        if (erreur != null && (
                                erreur.contains("foreign key") ||
                                        erreur.contains("constraint")  ||
                                        erreur.contains("violates")    ||
                                        erreur.contains("500"))) {
                            showAlert("Suppression impossible ❌",
                                    "Ce produit est utilisé dans un Bon d'Entrée ou Bon de Sortie.\n\n"
                                            + "Supprimez d'abord les bons associés.");
                        } else {
                            AlerteStyle.erreur("Erreur", erreur);
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
            showAlert("Erreur de connexion",
                    "Vérifie que le serveur SpringBoot est lancé.\n"
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
            showAlert("Validation",
                    "❌ Le N° Produit ne doit contenir que des lettres et chiffres.\nExemple : P01");
            return false;
        }
        if (design.length() < 2) {
            showAlert("Validation",
                    "❌ La désignation doit contenir au moins 2 caractères.");
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
        return true;
    }

    private void clearForm() {
        tfNumProduit.clear();
        tfNumProduit.setDisable(false);
        tfDesign.clear();
        tfStock.clear();
        table.getSelectionModel().clearSelection();
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    private void showAlert(String titre, String msg) {
        AlerteStyle.info(titre, msg);
    }
}