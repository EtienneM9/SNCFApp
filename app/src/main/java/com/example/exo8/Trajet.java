package com.example.exo8;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Trajet {
    private String villeDep;
    private String villeArr;
    private String heureDepart;
    private String heureArrivee;
    private String prix;
    private String duree;
    private String type;

    public Trajet(String villeDep, String villeArr, String heureDepart, String heureArrivee, String prix, String type) {
        this.villeDep = villeDep;
        this.villeArr = villeArr;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.prix = prix;
        this.duree = calculerDuree(heureDepart, heureArrivee);
        this.type = type;

    }

    public String getVilleArr() {
        return villeArr;
    }

    public String getVilleDep() {
        return villeDep;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public String getHeureArrivee() {
        return heureArrivee;
    }

    public String getPrix() {
        return prix;
    }

    public String getDuree(){return duree;}

    public String getType(){return type;}

    private String calculerDuree(String heureDepart, String heureArrivee) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date depart = format.parse(heureDepart);
            Date arrivee = format.parse(heureArrivee);

            if (depart == null || arrivee == null) {
                return "??h ??m"; // Valeur par défaut si erreur
            }

            long differenceMillis = arrivee.getTime() - depart.getTime();
            long heures = TimeUnit.MILLISECONDS.toHours(differenceMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceMillis) % 60;

            return String.format(Locale.getDefault(), "%dh %02dm", heures, minutes);
        } catch (Exception e) {
            e.printStackTrace();
            return "??h ??m"; // Valeur par défaut en cas d'erreur de parsing
        }
    }

}
