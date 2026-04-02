package com.stock.stockclient.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stock.stockclient.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8082/api";
    private static final Gson gson = new Gson();

    // ============ MÉTHODES HTTP GÉNÉRIQUES ============

    private String GET(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return lireReponse(conn);
    }

    private String POST(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
        }
        return lireReponse(conn);
    }

    private void DELETE(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        int code = conn.getResponseCode();
        if (code != 200 && code != 204) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream())
            );

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            throw new IOException("Erreur HTTP " + code + " : " + sb.toString());
        }
    }

    private String lireReponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // ============ PRODUITS ============

    public List<Produit> getAllProduits() throws IOException {
        String json = GET("/produits");
        Type type = new TypeToken<List<Produit>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public List<Produit> searchProduits(String design) throws IOException {
        String json = GET("/produits/search?design=" + design);
        Type type = new TypeToken<List<Produit>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addProduit(Produit p) throws IOException {
        POST("/produits", gson.toJson(p));
    }

    public void deleteProduit(String id) throws IOException {
        DELETE("/produits/" + id);
    }

    public Produit getProduitById(String id) throws IOException {
        String json = GET("/produits/" + id);
        return gson.fromJson(json, Produit.class);
    }

    public void updateProduit(Produit p) throws IOException {
        // PUT /api/produits/{id}
        URL url = new URL(BASE_URL + "/produits/" + p.getNumProduit());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(p).getBytes());
        }
        conn.getResponseCode();
    }

    // ============ BON ENTREE ============

    public List<BonEntree> getAllBonEntrees() throws IOException {
        String json = GET("/bonentrees");
        Type type = new TypeToken<List<BonEntree>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addBonEntree(BonEntree be) throws IOException {
        POST("/bonentrees", gson.toJson(be));
    }

    public void deleteBonEntree(String id) throws IOException {
        DELETE("/bonentrees/" + id);
    }

    public void updateBonEntree(BonEntree be) throws IOException {
        URL url = new URL(BASE_URL + "/bonentrees/" + be.getNumBonEntree());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(be).getBytes());
        }
        conn.getResponseCode();
    }

    // ============ BON SORTIE ============

    public List<BonSortie> getAllBonSorties() throws IOException {
        String json = GET("/bonsorties");
        Type type = new TypeToken<List<BonSortie>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addBonSortie(BonSortie bs) throws IOException {
        POST("/bonsorties", gson.toJson(bs));
    }

    public void deleteBonSortie(String id) throws IOException {
        DELETE("/bonsorties/" + id);
    }

    public void updateBonSortie(BonSortie bs) throws IOException {
        URL url = new URL(BASE_URL + "/bonsorties/" + bs.getNumBonSortie());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(bs).getBytes());
        }
        conn.getResponseCode();
    }

    // ============ MOUVEMENTS ============

    public List<BonEntree> getEntreesByProduit(String numProduit) throws IOException {
        String json = GET("/produits/" + numProduit + "/entrees");
        Type type = new TypeToken<List<BonEntree>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public List<BonSortie> getSortiesByProduit(String numProduit) throws IOException {
        String json = GET("/produits/" + numProduit + "/sorties");
        Type type = new TypeToken<List<BonSortie>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // Génère un ID unique basé sur le timestamp
    public String genererIdBonEntree() {
        return "BE" + System.currentTimeMillis() % 100000;
    }

    public String genererIdBonSortie() {
        return "BS" + System.currentTimeMillis() % 100000;
    }

}
