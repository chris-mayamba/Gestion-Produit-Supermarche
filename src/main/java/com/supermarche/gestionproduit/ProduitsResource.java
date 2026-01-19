package com.supermarche.gestionproduit;

import com.supermarche.gestionproduit.business.ProduitsEntrepriseBean;
import com.supermarche.gestionproduit.entities.Produit;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST pour la Gestion des Produits
 * Service 2 - Communication avec Service Vente (1) et Service Emplacement/Rapport (3)
 * 
 * @author Eden
 */
@Path("produits")
public class ProduitsResource {

    @EJB
    private ProduitsEntrepriseBean produitsService;

    /**
     * GET /api/produits
     * Lister tous les produits actifs
     * Usage: Service Vente, Service Rapport
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listerProduits() {
        try {
            List<Produit> produits = produitsService.listerProduits();
            return Response.ok(produits).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la récupération des produits"))
                    .build();
        }
    }

    /**
     * GET /api/produits/{id}
     * Obtenir les détails d'un produit spécifique
     * Usage: Service Vente (vérifier prix), Service Emplacement
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduit(@PathParam("id") Long id) {
        try {
            Produit produit = produitsService.trouverProduit(id);
            if (produit == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Produit non trouvé"))
                        .build();
            }
            return Response.ok(produit).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la récupération du produit"))
                    .build();
        }
    }

    /**
     * GET /api/produits/{id}/disponibilite
     * Vérifier la disponibilité d'un produit
     * Usage: Service Vente (avant enregistrement vente)
     */
    @GET
    @Path("/{id}/disponibilite")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifierDisponibilite(
            @PathParam("id") Long id,
            @QueryParam("quantite") @DefaultValue("1") int quantite) {
        try {
            Produit produit = produitsService.trouverProduit(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("idProduit", id);
            response.put("quantiteDemandee", quantite);
            
            if (produit == null) {
                response.put("disponible", false);
                response.put("message", "Produit non trouvé");
                return Response.status(Response.Status.NOT_FOUND).entity(response).build();
            }
            
            if (!"ACTIF".equals(produit.getStatut())) {
                response.put("disponible", false);
                response.put("message", "Produit inactif");
                return Response.ok(response).build();
            }
            
            boolean disponible = produit.getQuantite() >= quantite;
            response.put("disponible", disponible);
            response.put("quantiteEnStock", produit.getQuantite());
            response.put("nom", produit.getNom());
            response.put("prix", produit.getPrix());
            
            if (!disponible) {
                response.put("message", "Stock insuffisant");
            }
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la vérification"))
                    .build();
        }
    }

    /**
     * PUT /api/produits/{id}/stock
     * Mettre à jour le stock après une vente
     * Usage: Service Vente (après enregistrement vente)
     */
    @PUT
    @Path("/{id}/stock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mettreAJourStock(
            @PathParam("id") Long id,
            Map<String, Object> requestBody) {
        try {
            int quantiteVendue = ((Number) requestBody.get("quantiteVendue")).intValue();
            
            boolean succes = produitsService.diminuerStock(id, quantiteVendue);
            
            Map<String, Object> response = new HashMap<>();
            response.put("idProduit", id);
            response.put("quantiteVendue", quantiteVendue);
            response.put("succes", succes);
            
            if (succes) {
                Produit produit = produitsService.trouverProduit(id);
                response.put("nouvelleQuantite", produit.getQuantite());
                response.put("message", "Stock mis à jour avec succès");
                return Response.ok(response).build();
            } else {
                response.put("message", "Impossible de mettre à jour le stock");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la mise à jour du stock"))
                    .build();
        }
    }

    /**
     * GET /api/produits/rupture
     * Lister les produits en rupture de stock
     * Usage: Service Rapport (génération rapports)
     */
    @GET
    @Path("/rupture")
    @Produces(MediaType.APPLICATION_JSON)
    public Response produitsEnRupture() {
        try {
            List<Produit> produits = produitsService.produitsEnRupture();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", produits.size());
            response.put("produits", produits);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la récupération des produits en rupture"))
                    .build();
        }
    }

    /**
     * POST /api/produits
     * Ajouter un nouveau produit (usage interne ou API)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ajouterProduit(Produit produit) {
        try {
            produitsService.ajouterProduit(produit);
            return Response.status(Response.Status.CREATED)
                    .entity(createSuccessResponse("Produit ajouté avec succès"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de l'ajout du produit"))
                    .build();
        }
    }

    /**
     * PUT /api/produits/{id}
     * Modifier un produit existant
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierProduit(@PathParam("id") Long id, Produit produit) {
        try {
            Produit existant = produitsService.trouverProduit(id);
            if (existant == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Produit non trouvé"))
                        .build();
            }
            
            produit.setIdProduit(id);
            produitsService.modifierProduit(produit);
            
            return Response.ok(createSuccessResponse("Produit modifié avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la modification du produit"))
                    .build();
        }
    }

    /**
     * DELETE /api/produits/{id}
     * Supprimer (désactiver) un produit
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerProduit(@PathParam("id") Long id) {
        try {
            Produit produit = produitsService.trouverProduit(id);
            if (produit == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Produit non trouvé"))
                        .build();
            }
            
            produitsService.supprimerProduit(id);
            return Response.ok(createSuccessResponse("Produit supprimé avec succès")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la suppression du produit"))
                    .build();
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("succes", false);
        response.put("message", message);
        return response;
    }
    
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("succes", true);
        response.put("message", message);
        return response;
    }
}
