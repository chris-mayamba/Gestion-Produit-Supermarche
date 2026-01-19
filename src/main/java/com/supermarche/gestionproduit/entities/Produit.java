/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.supermarche.gestionproduit.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 *
 * @author Eden
 */


@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;

    @Column(name = "nom")
    private String nom;
    
    @Column(name = "prix")
    private BigDecimal prix;
    
    @Column(name = "categorie")
    private String categorie;
    
    @Column(name = "quantite")
    private int quantite;
    
    @Column(name = "seuil_alerte")
    private int seuilAlerte;
    
    @Column(name = "statut")
    private String statut = "ACTIF";
    
    public Long getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Long idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public String getStatut() {
        return statut;
    }

    // getters et setters
    public void setStatut(String statut) {    
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "com.supermarche.gestionproduit.entities.Produit[ id=" + idProduit + " ]";
    }
}