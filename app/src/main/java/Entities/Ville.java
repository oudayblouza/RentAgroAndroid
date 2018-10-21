package Entities;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Ville {
    private int id;
    private String nom;

    public Ville(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public Ville(){

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

    @Override
    public String toString() {
        return "Ville{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
