package com.stock.stockclient;

import com.stock.stockclient.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        TabPane tabPane = new TabPane();

        Tab tabProduits    = new Tab("📦 Produits",      new ProduitView().getView());
        Tab tabEntrees     = new Tab("📥 Bon Entrée",    new BonEntreeView().getView());
        Tab tabSorties     = new Tab("📤 Bon Sortie",    new BonSortieView().getView());
        Tab tabEtatStock   = new Tab("📊 État de Stock", new EtatStockView().getView());
        Tab tabMouvements  = new Tab("🔄 Mouvements",    new MouvementsView().getView());

        // Empêcher la fermeture des onglets
        tabProduits.setClosable(false);
        tabEntrees.setClosable(false);
        tabSorties.setClosable(false);
        tabEtatStock.setClosable(false);
        tabMouvements.setClosable(false);

        tabPane.getTabs().addAll(
                tabProduits, tabEntrees, tabSorties,
                tabEtatStock, tabMouvements
        );

        Scene scene = new Scene(tabPane, 950, 600);
        primaryStage.setTitle("Gestion de Stock — Client JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
