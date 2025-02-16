# SNCFApp

## Introduction

SNCFApp est une application mobile permettant de rechercher des trajets en fonction des gares et des dates entrées par l'utilisateur. Elle permet également de consulter les billets achetés et de gérer le profil utilisateur.

Pour configurer le projet avec Android Studio, suivez les étapes suivantes :

1. Clonez le dépôt :
   ```bash
   git clone [URL du dépôt]
   ```
2. Ouvrez Android Studio et sélectionnez "Open an existing project".
3. Naviguez jusqu'au répertoire du projet cloné et ouvrez-le.
4. Laissez Android Studio télécharger les dépendances et configurer le projet.
5. Une fois la configuration terminée, cliquez sur "Run" pour exécuter l'application sur un émulateur ou un appareil physique.

## Interface UI

### Acceuil avec inputs et datepiker 

Choix des gares et de la date de départ pour rechercher les trajets.

### Footer

Footer permatant la navigation entre les différents page (le bouton et la page offre ne sont pas implémentés)

### Page d'affichage des billets 

Page récapitulant les gares et dates choisis sur la partie haute et affichant les différents billets sous forme de MaterialCard avec Gare de départ, d'arrivée, durée, type de train et prix. 

### Page profil

Page récapitulant les infos du profil utilisateur


## Fonctionnalités Backend

### Autocomplétion des inputs de l'accueil

L'application utilise l'autocomplétion pour les champs de saisie sur la page d'accueil. Cela permet aux utilisateurs de saisir rapidement des informations en leur proposant des suggestions basées sur les entrées précédentes ou des données prédéfinies.

Exemple de code pour l'autocomplétion :
```java
// MainActivity.java
private void setupAutoComplete(AutoCompleteTextView editText) {
    editText.setThreshold(2); // Démarre la suggestion après 2 lettres

    editText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String query = s.toString().trim();
            if (query.length() >= 2) {
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
        if (stationId != null) {
            editText.setTag(stationId);
        }
    });
}

private void fetchStationSuggestions(String query, AutoCompleteTextView editText) {
    service.getStations(query).enqueue(new Callback<PlaceResponse>() {
        @Override
        public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<PlaceResponse.Place> places = response.body().getPlaces();
                List<String> stationNames = new ArrayList<>();
                stationIdMap.clear();  // Réinitialiser le mapping

                for (PlaceResponse.Place place : places) {
                    String stationName = place.getName();
                    String stationId = place.getId();
                    stationNames.add(stationName);
                    stationIdMap.put(stationName, stationId);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, stationNames);
                editText.setAdapter(adapter);
            }
        }

        @Override
        public void onFailure(Call<PlaceResponse> call, Throwable t) {
            // Handle failure
        }
    });
}
```

### Intégration API SNCF

L'application se connecte à l'API SNCF pour récupérer des informations sur les trajets. Cela inclut les horaires, les prix et les disponibilités des billets. L'intégration est gérée par `SncfApiClient` et `SncfApiService`.

Exemple de code pour l'intégration API SNCF :
```java
// SncfApiService.java
public interface SncfApiService {
    @GET("v1/coverage/sncf/journeys")
    Call<JourneyResponse> getJourneys(
            @Query("from") String from,
            @Query("to") String to,
            @Query("datetime") String datetime
    );

    @GET("v1/coverage/sncf/places")
    Call<PlaceResponse> getStations(@Query("q") String query);
}

// SncfApiClient.java
public class SncfApiClient {
    private static final String BASE_URL = "https://api.sncf.com/";

    public static Retrofit getClient(String apiKey) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Basic " + Base64.encodeToString((apiKey + ":").getBytes(), Base64.NO_WRAP))
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
```

### Affichage des trajets de l'API

Les trajets récupérés via l'API SNCF sont affichés dans l'application. Les utilisateurs peuvent voir les détails des trajets, y compris les horaires de départ et d'arrivée, les prix et les options de réservation.

Exemple de code pour l'affichage des trajets :
```java
// AfficheTrajets.java
private void fetchJourneys(String villedep, String villarr, String from, String to, String datet) {
    String datetime = convertToApiDateFormat(datet);
    Call<JourneyResponse> call = service.getJourneys(from, to, datetime);

    call.enqueue(new Callback<JourneyResponse>() {
        @Override
        public void onResponse(Call<JourneyResponse> call, Response<JourneyResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                trajets.clear();
                for (JourneyResponse.Journey journey : response.body().getJourneys()) {
                    String departureTime = extractHour(journey.getDepartureTime());
                    String arrivalTime = extractHour(journey.getArrivalTime());
                    String price = journey.getFare();
                    String typeTrain = journey.getTypeTrain();

                    trajets.add(new Trajet(villedep, villarr, departureTime, arrivalTime, price, typeTrain));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(AfficheTrajets.this, "Aucun trajet trouvé", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<JourneyResponse> call, Throwable t) {
            Toast.makeText(AfficheTrajets.this, "Erreur lors de la récupération des trajets", Toast.LENGTH_LONG).show();
        }
    });
}

private String convertToApiDateFormat(String inputDate) {
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
```

### Page listant les billets

Cette page affiche une liste des billets achetés par l'utilisateur. Chaque billet inclut des informations telles que le trajet, la date et l'heure, ainsi que le QR code pour l'embarquement.

Exemple de code pour la liste des billets :
```java
// ShowTikets.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.show_tikets);

    listeTickets = findViewById(R.id.listeTikets);
    tickets = new ArrayList<>();

    // Données en dur
    tickets.add(new Ticket("Saint-Malo", "18:11", "Gare de Paris Montparnasse", "21:01", "Saint-Malo"));
    tickets.add(new Ticket("Colmar", "07:30", "Paris Gare de l'Est", "10:45", "Colmar"));
    tickets.add(new Ticket("Lyon", "12:15", "Paris Gare de Lyon", "14:45", "Lyon Part-Dieu"));

    adapter = new TicketAdapter(this, tickets);
    listeTickets.setAdapter(adapter);

    bottomNavigationView = findViewById(R.id.footer);

    // Gérer la navigation dans le footer
    bottomNavigationView.setOnItemSelectedListener(item -> {
        if (item.getItemId() == R.id.nav_voyager) {
            Intent intent = new Intent(ShowTikets.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.nav_compte) {
            Intent intent = new Intent(ShowTikets.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    });
}
```

## Utilisation de Retrofit

Retrofit est utilisé pour effectuer des appels API vers l'API SNCF. Il simplifie la gestion des requêtes HTTP et la conversion des réponses JSON en objets Java.

Exemple de configuration de Retrofit :
```java
// SncfApiClient.java
public class SncfApiClient {
    private static final String BASE_URL = "https://api.sncf.com/";

    public static Retrofit getClient(String apiKey) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Basic " + Base64.encodeToString((apiKey + ":").getBytes(), Base64.NO_WRAP))
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
```

## Améliorations possibles

### Réservation fonctionnelle

Ajouter la fonctionnalité de réservation pour permettre aux utilisateurs de réserver des billets directement depuis l'application.

### Liste de billets fonctionnelle

Améliorer la gestion et l'affichage des billets pour offrir une meilleure expérience utilisateur. Cela pourrait inclure des filtres, des options de tri et des notifications de rappel.

### Modification du profil

Permettre aux utilisateurs de modifier leurs informations de profil directement depuis l'application, y compris la mise à jour de leur photo de profil et d'autres détails personnels.

### Amélioration de l'UI

Des améliorations de l'UI peuvent être pertinantes notament pour la liste des billets qui est une UI basique. 


