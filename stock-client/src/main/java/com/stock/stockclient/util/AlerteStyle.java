package com.stock.stockclient.util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlerteStyle {

    public static void succes(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style vert pour la fenêtre de succès
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #f1f8e9;" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 2;"
        );
        dialogPane.lookup(".content.label").setStyle(
                "-fx-text-fill: #2e7d32;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        alert.showAndWait();
    }

    public static void erreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #ffebee;" +
                        "-fx-border-color: #f44336;" +
                        "-fx-border-width: 2;"
        );
        dialogPane.lookup(".content.label").setStyle(
                "-fx-text-fill: #c62828;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        alert.showAndWait();
    }

    public static void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}