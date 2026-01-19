package com.supermarche.gestionproduit.business;

import com.supermarche.gestionproduit.entities.Produit;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
@LocalBean
public class ProduitsEntrepriseBean {

    @PersistenceContext
    private EntityManager em;

    // CREATE
    public void ajouterProduit(Produit produit) {
        produit.setStatut("ACTIF");
        em.persist(produit);
    }

    // READ - tous les produits actifs
    public List<Produit> listerProduits() {
        return em.createQuery(
            "SELECT p FROM Produit p WHERE p.statut = 'ACTIF'",
            Produit.class
        ).getResultList();
    }

    // READ - par ID
    public Produit trouverProduit(Long id) {
        return em.find(Produit.class, id);
    }

    // UPDATE
    public Produit modifierProduit(Produit produit) {
        return em.merge(produit);
    }

    // DELETE LOGIQUE
    public void supprimerProduit(Long id) {
        Produit p = em.find(Produit.class, id);
        if (p != null) {
            p.setStatut("INACTIF");
            em.merge(p);
        }
    }

    // DIMINUER LE STOCK (appelé par Service Vente)
    public boolean diminuerStock(Long idProduit, int quantiteVendue) {
        Produit p = em.find(Produit.class, idProduit);

        if (p == null || !"ACTIF".equals(p.getStatut())) {
            return false;
        }

        if (p.getQuantite() < quantiteVendue) {
            return false;
        }

        p.setQuantite(p.getQuantite() - quantiteVendue);
        em.merge(p);
        return true;
    }

    // PRODUITS EN RUPTURE
    public List<Produit> produitsEnRupture() {
        return em.createQuery(
            "SELECT p FROM Produit p WHERE p.quantite <= p.seuilAlerte AND p.statut = 'ACTIF'",
            Produit.class
        ).getResultList();
    }
}

