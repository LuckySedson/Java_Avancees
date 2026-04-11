package com.stock.stockclient.util;

import javafx.scene.control.Button;

public class BoutonStyle {

    public static void appliquer(Button btn, String couleurNormale) {
        // Style normal
        String styleNormal =
                "-fx-background-color: " + couleurNormale + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;";

        // Style survol
        String styleSurvol =
                "-fx-background-color: derive(" + couleurNormale + ", -20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 2);";

        // Style cliqué
        String styleClique =
                "-fx-background-color: derive(" + couleurNormale + ", -35%);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;";

        btn.setStyle(styleNormal);

        btn.setOnMouseEntered(e -> btn.setStyle(styleSurvol));
        btn.setOnMouseExited(e  -> btn.setStyle(styleNormal));
        btn.setOnMousePressed(e -> btn.setStyle(styleClique));
        btn.setOnMouseReleased(e -> btn.setStyle(styleSurvol));
    }

    // Méthodes pour chaque couleur
    public static void vert(Button btn)  { appliquer(btn, "#4CAF50"); }
    public static void rouge(Button btn) { appliquer(btn, "#f44336"); }
    public static void orange(Button btn){ appliquer(btn, "#FF9800"); }
    public static void bleu(Button btn)  { appliquer(btn, "#2196F3"); }
}