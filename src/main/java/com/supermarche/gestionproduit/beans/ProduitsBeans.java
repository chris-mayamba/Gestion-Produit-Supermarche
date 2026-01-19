/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.supermarche.gestionproduit.beans;

import com.supermarche.gestionproduit.business.ProduitsEntrepriseBean;
import com.supermarche.gestionproduit.entities.Produit;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * JSF backing bean exposing CRUD operations for the table view.
 */
@Named("produitBeans")
@RequestScoped
public class ProduitsBeans implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProduitsEntrepriseBean produitsService;

    private Produit produit = new Produit();

    public List<Produit> getProduits() {
        return produitsService.listerProduits();
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void ajouterProduit() {
        produitsService.ajouterProduit(produit);
        produit = new Produit();
    }

    public void editerProduit(Produit produit) {
        this.produit = produit;
    }

    public void supprimerProduit(Produit produit) {
        produitsService.supprimerProduit(produit.getIdProduit());
    }
}
