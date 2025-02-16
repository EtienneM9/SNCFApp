package com.example.exo8;

public class Ticket {
    private String destination;
    private String heureDepart;
    private String gareDepart;
    private String heureArrivee;
    private String gareArrivee;

    public Ticket(String destination, String heureDepart, String gareDepart, String heureArrivee, String gareArrivee) {
        this.destination = destination;
        this.heureDepart = heureDepart;
        this.gareDepart = gareDepart;
        this.heureArrivee = heureArrivee;
        this.gareArrivee = gareArrivee;
    }

    public String getDestination() { return destination; }
    public String getHeureDepart() { return heureDepart; }
    public String getGareDepart() { return gareDepart; }
    public String getHeureArrivee() { return heureArrivee; }
    public String getGareArrivee() { return gareArrivee; }
}
