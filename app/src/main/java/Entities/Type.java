package Entities;

import java.util.Date;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Type {

    private int id;
    private Categorie categorie;
    private String  nom ;
    private int valide;
    private Date dateAjout;



    public Type(int id, Categorie categorie, String nom, int valide, Date dateAjout) {
        this.id = id;
        this.categorie = categorie;
        this.nom = nom;
        this.valide = valide;
        this.dateAjout = dateAjout;
    }

    public Type() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getValide() {
        return valide;
    }

    public void setValide(int valide) {
        this.valide = valide;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", categorie=" + categorie +
                ", nom='" + nom + '\'' +
                ", valide=" + valide +
                ", dateAjout=" + dateAjout +
                '}';
    }
}
