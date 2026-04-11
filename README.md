    Architecture anle projet : Client - Serveur

stock-app/ : Backend SpringBoot + API REST + PostgreSQL (Vscode)
stock-client/ :  Frontend JavaFX (Intellij IDEA)

Outil : Java JDK 17 ou 21 (mety foana) et Maven

    1) Creation bd dans pgAdmin
-   nom base de donnees : gestion_stock; (CREATE DATABASE gestion_stock;)
-   Creation des tables : efa misy schema.sql ao de les requetes ao io copiena ao am pgAdmin

    2) Changer la connexion PostgreSQL
-   sokady ity fichier ity ao am backend stock-app/ (application.properties) : " src/main/resources/application.properties "
-   Mila ovaina le mot de passe ao atao ny mdp-nao ao amn'i pgAdmin : ex : spring.datasource.password=root

    3) Lancement du projet
    a) Lancer backend aloha (IMPORTANT)
-   Lancer d'abord le serveur springboot : cd stock-app/ aveo mvn spring-boot:run na ./mvnw spring-boot:run
-   Testeo amn navigateur web Chrome na Edge : http://localhost:8082/api/produits

    b) Lancer le frontend
-   cd stock-client/
-   charger dependances Maven : Sur Intellij io pratiquable kokoa fa cliquena fotsiny le bouton load maven etsy ambony
-   Clic droit sur MainApp.java de atao Run 'MainApp' na ( miditra ao amn MainApp.java de atao run aveo )

Vita ny chocolat. HALA MADRID!