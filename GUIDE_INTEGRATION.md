# 🤝 Guide d'Intégration - Service Gestion des Produits

## Pour l'Étudiant 1 (Service Vente)

### Configuration
Ajoutez la dépendance pour les appels HTTP dans votre `pom.xml` :

```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>
```

### Exemple de code Java pour appeler l'API

```java
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class ProduitServiceClient {
    
    private static final String BASE_URL = "http://localhost:8080/gestionproduits/resources/produits";
    private final Client client;
    
    public ProduitServiceClient() {
        this.client = ClientBuilder.newClient();
    }
    
    /**
     * Vérifier la disponibilité d'un produit avant la vente
     */
    public boolean verifierDisponibilite(Long idProduit, int quantite) {
        try {
            Response response = client
                .target(BASE_URL)
                .path(idProduit.toString())
                .path("disponibilite")
                .queryParam("quantite", quantite)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                Map<String, Object> result = response.readEntity(Map.class);
                return (Boolean) result.get("disponible");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mettre à jour le stock après une vente
     */
    public boolean mettreAJourStock(Long idProduit, int quantiteVendue) {
        try {
            Map<String, Integer> body = new HashMap<>();
            body.put("quantiteVendue", quantiteVendue);
            
            Response response = client
                .target(BASE_URL)
                .path(idProduit.toString())
                .path("stock")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(body));
            
            if (response.getStatus() == 200) {
                Map<String, Object> result = response.readEntity(Map.class);
                return (Boolean) result.get("succes");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtenir les informations d'un produit
     */
    public Map<String, Object> getProduit(Long idProduit) {
        try {
            Response response = client
                .target(BASE_URL)
                .path(idProduit.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(Map.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### Utilisation dans votre Service Vente

```java
@Stateless
public class VenteService {
    
    private ProduitServiceClient produitClient = new ProduitServiceClient();
    
    @Transactional
    public boolean enregistrerVente(Long idProduit, int quantite) {
        // 1. Vérifier la disponibilité
        if (!produitClient.verifierDisponibilite(idProduit, quantite)) {
            throw new RuntimeException("Produit non disponible en quantité suffisante");
        }
        
        // 2. Obtenir les infos du produit (prix, nom)
        Map<String, Object> produit = produitClient.getProduit(idProduit);
        double prix = ((Number) produit.get("prix")).doubleValue();
        
        // 3. Enregistrer la vente dans votre BD
        Vente vente = new Vente();
        vente.setIdProduit(idProduit);
        vente.setQuantite(quantite);
        vente.setPrixUnitaire(prix);
        vente.setTotal(prix * quantite);
        vente.setDateVente(LocalDateTime.now());
        em.persist(vente);
        
        // 4. Mettre à jour le stock
        boolean stockMisAJour = produitClient.mettreAJourStock(idProduit, quantite);
        
        if (!stockMisAJour) {
            throw new RuntimeException("Impossible de mettre à jour le stock");
        }
        
        return true;
    }
}
```

---

## Pour l'Étudiant 3 (Service Emplacement/Rapport)

### Exemple pour générer un rapport de rupture de stock

```java
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class RapportService {
    
    private static final String BASE_URL = "http://localhost:8080/gestionproduits/resources/produits";
    private final Client client;
    
    public RapportService() {
        this.client = ClientBuilder.newClient();
    }
    
    /**
     * Récupérer les produits en rupture de stock
     */
    public List<Map<String, Object>> getProduitsEnRupture() {
        try {
            Response response = client
                .target(BASE_URL)
                .path("rupture")
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                Map<String, Object> result = response.readEntity(Map.class);
                return (List<Map<String, Object>>) result.get("produits");
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Générer un rapport de rupture
     */
    public void genererRapportRupture() {
        List<Map<String, Object>> produitsEnRupture = getProduitsEnRupture();
        
        System.out.println("=== RAPPORT DE RUPTURE DE STOCK ===");
        System.out.println("Total: " + produitsEnRupture.size() + " produits");
        System.out.println();
        
        for (Map<String, Object> produit : produitsEnRupture) {
            System.out.println("ID: " + produit.get("idProduit"));
            System.out.println("Nom: " + produit.get("nom"));
            System.out.println("Stock actuel: " + produit.get("quantite"));
            System.out.println("Seuil d'alerte: " + produit.get("seuilAlerte"));
            System.out.println("----");
        }
    }
    
    /**
     * Lister tous les produits pour l'emplacement
     */
    public List<Map<String, Object>> getTousProduits() {
        try {
            Response response = client
                .target(BASE_URL)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(List.class);
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
```

---

## 🧪 Tester votre intégration

### Test unitaire avec JUnit

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    
    @Test
    public void testVerifierDisponibilite() {
        ProduitServiceClient client = new ProduitServiceClient();
        
        // Test avec un produit existant
        boolean disponible = client.verifierDisponibilite(1L, 5);
        assertTrue(disponible);
        
        // Test avec quantité trop élevée
        boolean nonDisponible = client.verifierDisponibilite(1L, 10000);
        assertFalse(nonDisponible);
    }
    
    @Test
    public void testMettreAJourStock() {
        ProduitServiceClient client = new ProduitServiceClient();
        
        // Test mise à jour normale
        boolean succes = client.mettreAJourStock(1L, 2);
        assertTrue(succes);
    }
}
```

---

## 🔧 Configuration réseau

### Si les services sont sur des machines différentes

Changez `localhost` par l'adresse IP du serveur hébergeant le Service 2 :

```java
private static final String BASE_URL = "http://192.168.1.100:8080/gestionproduits/resources/produits";
```

### Configuration CORS (si nécessaire)

Si vous utilisez JavaScript/Angular/React pour appeler l'API :

Ajoutez dans `ProduitsResource.java` :

```java
@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, 
                       ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
}
```

---

## 📞 Support

En cas de problème d'intégration, vérifiez :
1. ✅ Le service est déployé et accessible
2. ✅ L'URL de base est correcte
3. ✅ Le format JSON des requêtes est valide
4. ✅ Les IDs des produits existent dans la base de données

Testez d'abord avec cURL ou Postman avant d'intégrer dans votre code.
