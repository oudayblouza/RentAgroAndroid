package Entities;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class Users {

    private int id;
    private String username;
    private String username_canonical;
    private String email;
    private String email_canonical;
    private String password;
    private String roles;
    private Delegation delegation;
    private String prenom;
    private Double latitude;
    private Double longitude;
    private int numtel;
    private float rate = 0;
    private int voters = 0;
    private int enabled = 1;

    public Users(int id, String username, String username_canonical, String email, String email_canonical, String password, String roles, String prenom, Double latitude, Double longitude, int numtel, int enabled) {
        this.id = id;
        this.username = username;
        this.username_canonical = username_canonical;
        this.email = email;
        this.email_canonical = email_canonical;
        this.password = password;
        this.roles = roles;
        this.prenom = prenom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numtel = numtel;
        this.enabled = enabled;

    }

    public Users() {

    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUsername_canonical() {
        return username_canonical;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail_canonical() {
        return email_canonical;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsername_canonical(String username_canonical) {
        this.username_canonical = username_canonical;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail_canonical(String email_canonical) {
        this.email_canonical = email_canonical;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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

    public int getNumtel() {
        return numtel;
    }

    public void setNumtel(int numtel) {
        this.numtel = numtel;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getVoters() {
        return voters;
    }

    public void setVoters(int voters) {
        this.voters = voters;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", username_canonical='" + username_canonical + '\'' +
                ", email='" + email + '\'' +
                ", email_canonical='" + email_canonical + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", delegation=" + delegation +
                ", prenom='" + prenom + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", numtel=" + numtel +
                ", rate=" + rate +
                ", voters=" + voters +
                ", enabled=" + enabled +
                '}';
    }
}
