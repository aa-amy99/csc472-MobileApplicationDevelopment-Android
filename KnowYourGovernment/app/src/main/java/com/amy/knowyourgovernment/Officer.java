package com.amy.knowyourgovernment;

import java.io.Serializable;

public class Officer implements Serializable {
    private String role, name, party, address, phone, url, email, photo, google, fb, twitter, youtube;

    public Officer (String role, String name, String party, String address, String phone,
                    String url, String email, String photo, String google, String fb, String twitter, String youtube){
        this.name =  name;
        this.role =  role;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photo = photo;
        this.google = google;
        this.fb= fb;
        this.twitter = twitter;
        this.youtube = youtube;
    }
    //GET Methods
    public String getName() { return name; }
    public String getRole (){ return role; }
    public String getParty() { return party; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUrl() { return url; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }
    public String getFb() { return fb; }
    public String getGoogle() { return google; }
    public String getTwitter() { return twitter; }
    public String getYoutube() { return youtube; }

    //SET Methods
    public void setName(String myName) { this.name = myName; }
    public void setRole(String myRole) { this.role = myRole; }
    public void setParty(String myParty) { this.party =  myParty; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setUrl(String url) { this.url = url; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setGoogle(String google) { this.google = google; }
    public void setFb(String fb) { this.fb = fb; }
    public void setTwitter(String twitter) { this.twitter = twitter; }
    public void setYoutube(String youtube) { this.youtube = youtube; }
}
