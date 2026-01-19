/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.supermarche.gestionproduit.beans;

import com.supermarche.gestionproduit.business.ProduitsEntrepriseBean;
import com.supermarche.gestionproduit.entities.Produit;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * JSF backing bean exposing CRUD operations for the table view.
 */
@Named("produitBeans")
@ViewScoped
public class ProduitsBeans implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProduitsEntrepriseBean produitsService;

    private Produit produit = new Produit();
    private Long editId;

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
        if (!validerProduit()) {
            return;
        }
        produitsService.ajouterProduit(produit);
        addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit ajouté avec succès");
        produit = new Produit();
        
        // Fermer le modal via JavaScript
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add(
            "if(bootstrap.Modal.getInstance(document.getElementById('crudModal'))) " +
            "bootstrap.Modal.getInstance(document.getElementById('crudModal')).hide();"
        );
    }
    
    private boolean validerProduit() {
        boolean valid = true;
        
        // Validation du nom
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nom du produit est obligatoire");
            valid = false;
        } else if (produit.getNom().trim().length() < 3) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nom doit contenir au moins 3 caractères");
            valid = false;
        }
        
        // Validation du prix
        if (produit.getPrix() == null || produit.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le prix doit être supérieur à 0");
            valid = false;
        }
        
        // Validation de la catégorie
        if (produit.getCategorie() == null || produit.getCategorie().trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "La catégorie est obligatoire");
            valid = false;
        }
        
        // Validation de la quantité
        if (produit.getQuantite() < 0) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "La quantité ne peut pas être négative");
            valid = false;
        }
        
        // Validation du seuil d'alerte
        if (produit.getSeuilAlerte() < 0) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le seuil d'alerte ne peut pas être négatif");
            valid = false;
        }
        
        return valid;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public void prepareEdit(Produit produit) {
        this.editId = produit.getIdProduit();
        this.produit = new Produit();
        this.produit.setIdProduit(produit.getIdProduit());
        this.produit.setNom(produit.getNom());
        this.produit.setPrix(produit.getPrix());
        this.produit.setCategorie(produit.getCategorie());
        this.produit.setQuantite(produit.getQuantite());
        this.produit.setSeuilAlerte(produit.getSeuilAlerte());
        this.produit.setStatut(produit.getStatut());
    }
    
    public void modifierProduit() {
        if (!validerProduit()) {
            return;
        }
        if (produit.getIdProduit() != null) {
            produitsService.modifierProduit(produit);
            addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit modifié avec succès");
        }
        produit = new Produit();
        editId = null;
        
        // Fermer le modal via JavaScript
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add(
            "if(bootstrap.Modal.getInstance(document.getElementById('editModal'))) " +
            "bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();"
        );
    }
    
    public void annulerEdition() {
        produit = new Produit();
        editId = null;
    }

    public void supprimerProduit(Produit produit) {
        produitsService.supprimerProduit(produit.getIdProduit());
        addMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit supprimé avec succès");
    }
}
