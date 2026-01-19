# 📚 API REST - Service Gestion des Produits (Service 2)

**Base URL:** `http://localhost:8080/gestionproduits-1.0/api/produits`

---

## 🔹 Endpoints pour Service Vente (Étudiant 1)

### 1. Lister tous les produits actifs
```http
GET /api/produits
```
**Réponse:**
```json
[
  {
    "idProduit": 1,
    "nom": "Pain de mie",
    "prix": 2.50,
    "categorie": "Alimentaire",
    "quantite": 45,
    "seuilAlerte": 10,
    "statut": "ACTIF"
  }
]
```

### 2. Obtenir un produit spécifique
```http
GET /api/produits/{id}
```
**Exemple:** `GET /api/produits/1`

**Réponse:**
```json
{
  "idProduit": 1,
  "nom": "Pain de mie",
  "prix": 2.50,
  "categorie": "Alimentaire",
  "quantite": 45,
  "seuilAlerte": 10,
  "statut": "ACTIF"
}
```

### 3. Vérifier la disponibilité d'un produit
```http
GET /api/produits/{id}/disponibilite?quantite={qte}
```
**Exemple:** `GET /api/produits/1/disponibilite?quantite=5`

**Réponse (disponible):**
```json
{
  "idProduit": 1,
  "quantiteDemandee": 5,
  "disponible": true,
  "quantiteEnStock": 45,
  "nom": "Pain de mie",
  "prix": 2.50
}
```

**Réponse (stock insuffisant):**
```json
{
  "idProduit": 1,
  "quantiteDemandee": 50,
  "disponible": false,
  "quantiteEnStock": 45,
  "nom": "Pain de mie",
  "prix": 2.50,
  "message": "Stock insuffisant"
}
```

### 4. Mettre à jour le stock après vente ⭐
```http
PUT /api/produits/{id}/stock
Content-Type: application/json
```

**Body:**
```json
{
  "quantiteVendue": 5
}
```

**Réponse (succès):**
```json
{
  "idProduit": 1,
  "quantiteVendue": 5,
  "succes": true,
  "nouvelleQuantite": 40,
  "message": "Stock mis à jour avec succès"
}
```

**Réponse (échec):**
```json
{
  "idProduit": 1,
  "quantiteVendue": 100,
  "succes": false,
  "message": "Impossible de mettre à jour le stock"
}
```

---

## 🔹 Endpoints pour Service Emplacement/Rapport (Étudiant 3)

### 5. Lister les produits en rupture de stock
```http
GET /api/produits/rupture
```

**Réponse:**
```json
{
  "total": 3,
  "produits": [
    {
      "idProduit": 5,
      "nom": "Chocolat noir",
      "prix": 2.95,
      "categorie": "Alimentaire",
      "quantite": 8,
      "seuilAlerte": 12,
      "statut": "ACTIF"
    },
    {
      "idProduit": 12,
      "nom": "Savon liquide",
      "prix": 3.50,
      "categorie": "Hygiène",
      "quantite": 7,
      "seuilAlerte": 10,
      "statut": "ACTIF"
    }
  ]
}
```

---

## 🔹 Endpoints CRUD (Usage interne ou API)

### 6. Ajouter un produit
```http
POST /api/produits
Content-Type: application/json
```

**Body:**
```json
{
  "nom": "Nouveau produit",
  "prix": 5.99,
  "categorie": "Alimentaire",
  "quantite": 100,
  "seuilAlerte": 20
}
```

**Réponse:**
```json
{
  "succes": true,
  "message": "Produit ajouté avec succès"
}
```

### 7. Modifier un produit
```http
PUT /api/produits/{id}
Content-Type: application/json
```

**Body:**
```json
{
  "nom": "Produit modifié",
  "prix": 6.50,
  "categorie": "Boissons",
  "quantite": 80,
  "seuilAlerte": 15,
  "statut": "ACTIF"
}
```

### 8. Supprimer un produit (soft delete)
```http
DELETE /api/produits/{id}
```

**Réponse:**
```json
{
  "succes": true,
  "message": "Produit supprimé avec succès"
}
```

---

## 📋 Codes de statut HTTP

| Code | Signification |
|------|--------------|
| 200 | OK - Succès |
| 201 | Created - Ressource créée |
| 400 | Bad Request - Requête invalide |
| 404 | Not Found - Ressource non trouvée |
| 500 | Internal Server Error - Erreur serveur |

---

## 🔄 Scénarios d'intégration

### Scénario 1: Service Vente enregistre une vente

1. **Service Vente** vérifie la disponibilité:
   ```
   GET /api/produits/1/disponibilite?quantite=3
   ```

2. Si disponible, **Service Vente** enregistre la vente dans sa propre BD

3. **Service Vente** met à jour le stock:
   ```
   PUT /api/produits/1/stock
   Body: {"quantiteVendue": 3}
   ```

### Scénario 2: Service Rapport génère un rapport

1. **Service Rapport** récupère les produits en rupture:
   ```
   GET /api/produits/rupture
   ```

2. **Service Rapport** utilise ces données pour générer son rapport

---

## 🧪 Tester l'API

### Avec cURL:
```bash
# Lister les produits
curl -X GET http://localhost:8080/gestionproduits-1.0/api/produits

# Vérifier disponibilité
curl -X GET "http://localhost:8080/gestionproduits-1.0/api/produits/1/disponibilite?quantite=5"

# Mettre à jour le stock
curl -X PUT http://localhost:8080/gestionproduits-1.0/api/produits/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantiteVendue": 5}'

# Produits en rupture
curl -X GET http://localhost:8080/gestionproduits-1.0/api/produits/rupture
```

### Avec Postman:
1. Créer une nouvelle collection "Service Gestion Produits"
2. Importer les endpoints ci-dessus
3. Tester chaque endpoint

---

## 📝 Notes importantes

- ✅ Tous les endpoints retournent du JSON
- ✅ L'API gère les erreurs avec des messages appropriés
- ✅ La mise à jour du stock est **transactionnelle** (rollback en cas d'erreur)
- ✅ La suppression est **logique** (statut INACTIF)
- ✅ Les produits inactifs ne sont pas retournés par défaut
