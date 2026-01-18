/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.supermarche.gestionproduit.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 *
 * @author Eden
 */
@Named(value = "produitsBeans")
@RequestScoped
public class ProduitsBeans implements Serializable{

    private int id;
    private String nom;
    private float prix;
    private String categorie;
    private int quantite;
    private int seuilAlerte;
    private String status;
    
    
}
