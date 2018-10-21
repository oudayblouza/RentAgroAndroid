package Entities;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Delegation {
    private int id;
    private Ville ville;
    private String nom;

    public Delegation(int id, Ville ville, String nom) {
        this.id = id;
        this.ville = ville;
        this.nom = nom;
    }
    public Delegation(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String  toString() {
        return "Delegation{" +
                "id=" + id +
                ", ville=" + ville +
                ", nom='" + nom + '\'' +
                '}';
    }
}
