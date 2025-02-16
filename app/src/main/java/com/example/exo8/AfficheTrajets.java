package com.example.exo8;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exo8.R;
import com.example.exo8.Trajet;
import com.example.exo8.TrajetAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exo8.SncfApiClient;
import com.example.exo8.SncfApiService;
import com.example.exo8.JourneyResponse;
import com.example.exo8.Trajet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AfficheTrajets extends AppCompatActivity {

    private String TAG = "AFFICHETRAJETS";
    private ListView listeTrajets;
    private List<Trajet> trajets;
    private TrajetAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private SncfApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichetrajets);

        // Récupérer les données passées via l'Intent, y compris les IDs de gares
        String dateDepart = getIntent().getStringExtra("DATE_DEPART");
        String dateArrivee = getIntent().getStringExtra("DATE_ARRIVEE");
        String idGareDep = getIntent().getStringExtra("ID_GARE_DEP");
        String idGareArr = getIntent().getStringExtra("ID_GARE_ARR");
        // On peut aussi récupérer les noms pour l'affichage si besoin :
        String villeDep = getIntent().getStringExtra("VILLE_DEP");
        String villeArr = getIntent().getStringExtra("VILLE_ARR");

        listeTrajets = findViewById(R.id.listeTrajets);
        trajets = new ArrayList<>();
        adapter = new TrajetAdapter(this, trajets);
        listeTrajets.setAdapter(adapter);
        bottomNavigationView = findViewById(R.id.footer);
        TextView villedep = findViewById(R.id.tv_departure_city);
        TextView villarr = findViewById(R.id.tv_arrival_city);
        TextView datedep = findViewById(R.id.tv_departure_date);
        TextView datearr = findViewById(R.id.tv_arrival_date);

        villedep.setText(villeDep);
        villarr.setText(villeArr);
        datedep.setText(dateDepart);
        datearr.setText(dateArrivee);


        // Initialisation de Retrofit (assurez-vous que SncfApiClient est configuré pour l'API)
        Retrofit retrofit = SncfApiClient.getClient(getString(R.string.sncf_api_key));
        service = retrofit.create(SncfApiService.class);

        // Gérer la navigation dans le footer
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_billets) {
                Intent intent = new Intent(AfficheTrajets.this, ShowTikets.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_voyager) {
                Intent intent = new Intent(AfficheTrajets.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.nav_compte) {
                Intent intent = new Intent(AfficheTrajets.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Lancer la requête API en utilisant les identifiants de gare
        fetchJourneys(villeDep, villeArr, idGareDep, idGareArr, dateDepart);
    }

    private void fetchJourneys(String villedep, String villarr, String from, String to, String datet) {
        String datetime = convertToApiDateFormat(datet);
        Log.d(TAG, "Recherche des trajets pour : " + datetime + " | " + from + " -> " + to);
        Call<JourneyResponse> call = service.getJourneys(from, to, datetime);

        if (call == null) {
            Log.d(TAG, "La requête Retrofit est NULL !");
            return;
        }

        Log.d(TAG, "Requête Retrofit créée, en attente de réponse...");
        call.enqueue(new Callback<JourneyResponse>() {
            @Override
            public void onResponse(Call<JourneyResponse> call, Response<JourneyResponse> response) {
                Log.d(TAG, "dans onResponse");
                if (response.isSuccessful() && response.body() != null) {
                    try{
                        String rawJson = response.raw().body().toString();
                        Log.d(TAG, "DEBUG_PRICE: " + rawJson);
                        trajets.clear();
                        for (JourneyResponse.Journey journey : response.body().getJourneys()) {
                            String departureTime = extractHour(journey.getDepartureTime());
                            String arrivalTime = extractHour(journey.getArrivalTime());
                            String price = journey.getFare();
                            Log.d("DEBUG_PRICE", price);
                            String typeTrain = journey.getTypeTrain(); // Récupération du type de train

                            Log.d("DEBUG_TRAJET", "Prix reçu pour le trajet : " + price);

                            trajets.add(new Trajet(villedep, villarr, departureTime, arrivalTime, price, typeTrain));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting raw JSON", e);
                    }
                } else {
                    Log.d(TAG   , "Aucun trajet trouvé");
                    Toast.makeText(AfficheTrajets.this, "Aucun trajet trouvé", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JourneyResponse> call, Throwable t) {
                Log.e(TAG, "Erreur de requête : " + t.getMessage());
                Toast.makeText(AfficheTrajets.this, "Erreur lors de la récupération des trajets", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String convertToApiDateFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractHour(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "??:??"; // Valeur par défaut en cas d'erreur
        }
    }



}



/* ------------------------------------- CSV VERSION -------------------------------------
public class AfficheTrajets extends AppCompatActivity {

    private static final String TAG = "AFFICHERTRAJETS"; // Tag Logcat
    private ListView listeTrajets;
    private List<Trajet> trajets;
    private TrajetAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private TextView villeD;
    private TextView villeA;
    private TextView dateD;
    private TextView dateA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichetrajets);

        // Récupération des données de l'intent
        String dateDepart = getIntent().getStringExtra("DATE_DEPART");
        String dep = getIntent().getStringExtra("VILLE_DEP");
        String arr = getIntent().getStringExtra("VILLE_ARR");
        String dateArr = getIntent().getStringExtra("DATE_ARRIVEE");

        Log.d(TAG, "Intent values -> Date: " + dateDepart + ", Départ: " + dep + ", Arrivée: " + arr);


        bottomNavigationView = findViewById(R.id.footer);
        villeD = findViewById(R.id.tv_departure_city);
        villeA = findViewById(R.id.tv_arrival_city);
        dateD = findViewById(R.id.tv_departure_date);
        dateA = findViewById(R.id.tv_arrival_date);

        listeTrajets = findViewById(R.id.listeTrajets);
        trajets = new ArrayList<>();

        villeD.setText(dep);
        villeA.setText(arr);
        dateD.setText(dateDepart);
        dateA.setText(dateArr);

        // Chargement et filtrage des trajets depuis le CSV
        trajets = chargerTrajetsDepuisCSV(dateDepart, dep, arr);

        if (trajets.isEmpty()) {
            Log.w(TAG, "Aucun trajet trouvé pour ces critères !");
        } else {
            Log.d(TAG, "Nombre de trajets trouvés: " + trajets.size());
        }

        // Affichage des trajets dans la liste
        adapter = new TrajetAdapter(this, trajets);
        listeTrajets.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_billets) {
                Intent intent = new Intent(AfficheTrajets.this, ShowTikets.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_voyager) {
                Intent intent = new Intent(AfficheTrajets.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }

    private List<Trajet> chargerTrajetsDepuisCSV(String date, String depart, String arrivee) {
        List<Trajet> trajetsFiltres = new ArrayList<>();

        Log.d(TAG, "Recherche des trajets pour : " + date + " | " + depart + " -> " + arrivee);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.trajets);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String ligne;
            reader.readLine(); // Ignorer l'en-tête

            while ((ligne = reader.readLine()) != null) {
                Log.d(TAG, "Ligne brute CSV: " + ligne);

                // Nettoyage des espaces autour des valeurs
                String[] valeurs = ligne.split("\\s*,\\s*");  // <-- SUPPRIME les espaces autour des virgules

                if (valeurs.length >= 6) {
                    String csvDate = valeurs[0].trim();
                    String csvDepart = valeurs[1].trim();
                    String csvArrivee = valeurs[2].trim();
                    String heureDep = valeurs[3].trim();
                    String heureArr = valeurs[4].trim();
                    String prix = valeurs[5].trim();

                    Log.d(TAG, "Valeurs nettoyées : [" + csvDate + "] [" + csvDepart + "] [" + csvArrivee + "]");

                    // Comparaison après normalisation
                    if (csvDate.trim().equalsIgnoreCase(date.trim()) &&
                            csvDepart.trim().equalsIgnoreCase(depart.trim()) &&
                            csvArrivee.trim().equalsIgnoreCase(arrivee.trim())) {

                        trajetsFiltres.add(new Trajet(csvDepart, csvArrivee, heureDep, heureArr, prix));
                        Log.d(TAG, "Trajet ajouté -> " + heureDep + " -> " + heureArr + " | " + prix);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la lecture du CSV", e);
        }

        Log.d(TAG, "Nombre de trajets trouvés : " + trajetsFiltres.size());
        return trajetsFiltres;
    }

}

*/
