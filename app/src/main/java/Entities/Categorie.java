package Entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Categorie implements Serializable{

    private int id;
    private String nom;
    private String imageUrl;
    private Date dateAjout;


    public Categorie(int id, String nom,String imageUrl, Date dateAjout) {
        this.id = id;
        this.nom = nom;
        this.imageUrl=imageUrl;
        this.dateAjout = dateAjout;
    }

    public Categorie() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }
}
