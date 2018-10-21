package Entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Annonces implements Serializable,Parcelable{

    private int id ;
    private String nature ;
    private String titre ;
    private String description ;
    private int prix ;
    private int validite ;
    private Date dateDebut ;
    private int duree ;
    private int numTel ;
    private int etat;
    private String renttype;
    private Double latitude;
    private Double longitude;
    private Users user ;
    private Type type ;
    private Delegation delegation ;
    private String path;
    private int nbrImg;

    public Annonces(int id, String nature, String titre, String description, int prix, int validite, Date dateDebut, int duree, int numTel,int etat,String renttype,Double latitude,Double longitude, Users user, Type type, Delegation delegation,String path,int nbrImg) {
        this.id = id;
        this.nature = nature;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.validite = validite;
        this.dateDebut = dateDebut;
        this.duree = duree;
        this.numTel = numTel;
        this.user = user;
        this.type = type;
        this.delegation = delegation;
        this.etat = etat;
         this.renttype=renttype;
         this.latitude=latitude;
         this.longitude=longitude;
         this.path=path;
        this.nbrImg=nbrImg;
    }
    public Annonces(){

    }

    public int getId() {
        return id;
    }

    public String getNature() {
        return nature;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public int getPrix() {
        return prix;
    }

    public int getValidite() {
        return validite;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public int getDuree() {
        return duree;
    }

    public int getNumTel() {
        return numTel;
    }

    public Users getUser() {
        return user;
    }

    public Type getType() {
        return type;
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public void setValidite(int validite) {
        this.validite = validite;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getRenttype() {
        return renttype;
    }

    public void setRenttype(String renttype) {
        this.renttype = renttype;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNbrImg() {
        return nbrImg;
    }

    public void setNbrImg(int nbrImg) {
        this.nbrImg = nbrImg;
    }

    @Override
    public String toString() {
        return "Annonces{" +
                "id=" + id +
                ", nature='" + nature + '\'' +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", validite=" + validite +
                ", dateDebut=" + dateDebut +
                ", duree=" + duree +
                ", numTel=" + numTel +
                ", user=" + user +
                ", type=" + type +
                ", delegation=" + delegation +
                '}';
    }


    public Annonces(JSONObject j) {

        this.id = j.optInt("id");
        this.nature = j.optString("nature");
        this.titre = j.optString("titre");
        this.description = j.optString("description");
        this.prix = j.optInt("prix");
        this.validite = j.optInt("validite");


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsed = null;
        try {
            parsed = format.parse(j.optString("dateDebut"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Date sql = new java.sql.Date(parsed.getTime());
        this.dateDebut = sql ;


        this.duree = j.optInt("duree");
        this.numTel = j.optInt("numTel");
        this.renttype = j.optString("renttype");

        this.user = new Users();
        this.delegation = new Delegation();
        this.type = new Type();


        this.etat=j.optInt("etat");
        this.renttype=j.optString("renttype");
        this.longitude=j.optDouble("longitude");
        this.latitude=j.optDouble("latitude");
        this.path=j.optString("path");
        this.nbrImg=j.optInt("nbrImg");

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
