package com.example.exo8;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private Button btnDepart, btnArrivee, affiche;
    private TextView txtDepart, txtArrivee;
    private String dateDepart = "", dateArrivee = "";
    private AutoCompleteTextView dep, arr;
    private SncfApiService service;
    private BottomNavigationView bottomNavigationView;
    private Map<String, String> stationIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDepart = findViewById(R.id.btnDepart);
        btnArrivee = findViewById(R.id.btnArrivee);
        txtDepart = findViewById(R.id.txtDepart);
        txtArrivee = findViewById(R.id.txtArrivee);
        affiche = findViewById(R.id.Affiche);
        dep = findViewById(R.id.editVilleDepart);
        arr = findViewById(R.id.editVilleArrivee);
        bottomNavigationView = findViewById(R.id.footer);

        // Initialisation de Retrofit
        Retrofit retrofit = SncfApiClient.getClient(getString(R.string.sncf_api_key));
        service = retrofit.create(SncfApiService.class);

        if (service == null) {
            Log.e("DEBUG_INIT", "❌ ERREUR: Impossible d'initialiser Retrofit !");
        } else {
            Log.d("DEBUG_INIT", "✅ Retrofit initialisé avec succès !");
        }

        // Ajouter l'autocomplétion
        setupAutoComplete(dep);
        setupAutoComplete(arr);

        btnDepart.setOnClickListener(v -> showDatePicker(txtDepart, true));
        btnArrivee.setOnClickListener(v -> showDatePicker(txtArrivee, false));

        // Ajout des TextView pour les erreurs
        TextView errorDepart = findViewById(R.id.errorDepart);
        TextView errorArrivee = findViewById(R.id.errorArrivee);
        TextView errorDate = findViewById(R.id.errorDate);

        affiche.setOnClickListener(v -> {
            String villeDepart = dep.getText().toString().trim();
            String villeArrivee = arr.getText().toString().trim();
            String dateDepartText = dateDepart.trim();

            boolean isValid = true;

            // Vérification des champs
            if (villeDepart.isEmpty()) {
                errorDepart.setText("Le champ 'Départ' est obligatoire.");
                errorDepart.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorDepart.setVisibility(View.GONE);
            }

            if (villeArrivee.isEmpty()) {
                errorArrivee.setText("Le champ 'Arrivée' est obligatoire.");
                errorArrivee.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorArrivee.setVisibility(View.GONE);
            }

            if (dateDepartText.isEmpty()) {
                errorDate.setText("Veuillez sélectionner une date de départ.");
                errorDate.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorDate.setVisibility(View.GONE);
            }

            // Si tout est valide, on lance l'activité AfficheTrajets
            if (isValid) {
                Intent intent = new Intent(MainActivity.this, AfficheTrajets.class);
                intent.putExtra("DATE_DEPART", dateDepart);
                intent.putExtra("DATE_ARRIVEE", dateArrivee);
                intent.putExtra("VILLE_DEP", villeDepart);
                intent.putExtra("VILLE_ARR", villeArrivee);

                // Récupération des IDs de gares stockés dans le tag lors de la sélection
                String idGareDep = (String) dep.getTag();
                String idGareArr = (String) arr.getTag();
                intent.putExtra("ID_GARE_DEP", idGareDep);
                intent.putExtra("ID_GARE_ARR", idGareArr);

                startActivity(intent);
            }
        });

        // Gérer la navigation dans le footer
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_billets) {
                Intent intent = new Intent(MainActivity.this, ShowTikets.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.nav_compte) {
                Log.d("DEBUG_PROF", "lancement du profil");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void showDatePicker(TextView textView, boolean isDepart) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                textView.setText(selectedDate);

                if (isDepart) {
                    dateDepart = selectedDate;
                } else {
                    dateArrivee =selectedDate;
                }
            }
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    /* -------------------- Auto Complétion -------------------------------------------*/

    private void setupAutoComplete(AutoCompleteTextView editText) {
        Log.d("DEBUG_UI", "Dans setautocomplete");

        editText.setThreshold(2); // Démarre la suggestion après 2 lettres

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                Log.d("DEBUG_UI", "Saisie détectée: " + query);

                if (query.length() >= 2) {
                    Log.d("DEBUG_UI", "Déclenchement de fetchStationSuggestions avec: " + query);
                    fetchStationSuggestions(query, editText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lors de la sélection d'une gare, on stocke son ID (fourni par l'API)
        editText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStationName = (String) parent.getItemAtPosition(position);
            String stationId = stationIdMap.get(selectedStationName);
            Log.d("DEBUG_UI", "Sélection utilisateur: " + selectedStationName + " (ID: " + stationId + ")");


            if (stationId != null) {
                editText.setTag(stationId);
                Log.d("StationSelection", "Gare sélectionnée : " + selectedStationName + " (ID: " + stationId + ")");
            }
        });
    }

    // Appel API pour récupérer les suggestions de gares via l'API Navitia ou votre API SNCF
    private void fetchStationSuggestions(String query, AutoCompleteTextView editText) {
        Log.d("DEBUG_API", "Dans fetchSugg" + query);

        service.getStations(query).enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DEBUG_API", "Réponse API reçue avec succès !");

                    List<PlaceResponse.Place> places = response.body().getPlaces();
                    if (places == null || places.isEmpty()) {
                        Log.d("DEBUG_API", "Aucune gare trouvée pour la requête: " + query);
                        return;
                    }

                    List<String> stationNames = new ArrayList<>();
                    stationIdMap.clear();  // Réinitialiser le mapping

                    // Parcourir les résultats et filtrer les gares
                    for (PlaceResponse.Place place : response.body().getPlaces()) {
                        String stationName = place.getName();
                        String stationId = place.getId();  // stop_area:id fourni par l'API
                        Log.d("DEBUG_API", "Gare trouvée: " + stationName + " (ID: " + stationId + ")");


                        stationNames.add(stationName);
                        stationIdMap.put(stationName, stationId);
                    }

                    if (stationNames.isEmpty()) {
                        Log.d("DEBUG_API", "Aucune gare valide trouvée !");
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_dropdown_item_1line, stationNames);
                    editText.setAdapter(adapter);
                    Log.d("DEBUG_API", "Adapter mis à jour avec " + stationNames.size() + " suggestions.");

                } else {
                    Log.d("DEBUG_API", "Réponse API non réussie: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                Log.e("DEBUG_API", "Erreur de requête : " + t.getMessage());
            }
        });
    }
}