/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.entities;

import java.util.Date;

/**
 *
 * @author ASUS
 */
public class Reclamation {
    private int id,id_user;
    private Date dateReclamation;
    private String status,typeReclamation,email,objet,description,image;
    public Reclamation() {
    }

    public Reclamation( String typeReclamation, Date dateReclamation, String image, String status, String email, String objet, String description, int id_client) {
        this.id = id;
        this.id_user = id_client;
        this.dateReclamation = dateReclamation;
        this.status = status;
        this.typeReclamation = typeReclamation;
        this.image = image;
        this.email = email;
        this.objet = objet;
        this.description = description;
    }

    public Reclamation(int id, String typeReclamation, Date dateReclamation, String image, String status, String email, String objet, String description, int id_client) {
        this.id = id;
        this.id_user = id_client;
        this.dateReclamation = dateReclamation;
        this.status = status;
        this.typeReclamation = typeReclamation;
        this.image = image;
        this.email = email;
        this.objet = objet;
        this.description = description;
    }

    

   

   

    

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(Date dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeReclamation() {
        return typeReclamation;
    }

    public void setTypeReclamation(String typeReclamation) {
        this.typeReclamation = typeReclamation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_client) {
        this.id_user = id_client;
    }
    
    
}

