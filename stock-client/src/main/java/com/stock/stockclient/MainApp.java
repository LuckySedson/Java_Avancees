package com.stock.stockclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import com.stock.stockclient.view.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        TabPane tabPane = new TabPane();

        ProduitView    produitView    = new ProduitView();
        BonEntreeView  bonEntreeView  = new BonEntreeView();
        BonSortieView  bonSortieView  = new BonSortieView();
        EtatStockView  etatStockView  = new EtatStockView();
        MouvementsView mouvementsView = new MouvementsView();

        Tab tabProduits   = new Tab("📦 Produits",      produitView.getView());
        Tab tabEntrees    = new Tab("📥 Bon Entrée",    bonEntreeView.getView());
        Tab tabSorties    = new Tab("📤 Bon Sortie",    bonSortieView.getView());
        Tab tabEtatStock  = new Tab("📊 État de Stock", etatStockView.getView());
        Tab tabMouvements = new Tab("🔄 Mouvements",    mouvementsView.getView());

        tabProduits.setClosable(false);
        tabEntrees.setClosable(false);
        tabSorties.setClosable(false);
        tabEtatStock.setClosable(false);
        tabMouvements.setClosable(false);

        tabPane.getTabs().addAll(
                tabProduits, tabEntrees, tabSorties,
                tabEtatStock, tabMouvements
        );

        // ── Refresh ComboBox
        produitView.setOnProduitChange(() -> {
            bonEntreeView.chargerProduits();
            bonSortieView.chargerProduits();
        });

        // ── Refresh auto onglet
        tabPane.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> {
                    switch (newIndex.intValue()) {
                        case 0 -> produitView.refresh();
                        case 3 -> etatStockView.refresh();
                        case 4 -> mouvementsView.refresh();
                    }
                }
        );

        Scene scene = new Scene(tabPane, 950, 600);
        primaryStage.setTitle("Gestion de Stock — Client Java Swing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}