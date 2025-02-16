package com.example.exo8;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName;
    private TextInputEditText inputPrenom, inputNom, inputBirthdate, inputPhone;
    private RadioButton radioMadame, radioMonsieur;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d("DEBUG_PROF", "dans oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);

        // Initialisation des vues
        userName = findViewById(R.id.user_name);
        inputPrenom = findViewById(R.id.input_prenom);
        inputNom = findViewById(R.id.input_nom);
        inputBirthdate = findViewById(R.id.input_birthdate);
        inputPhone = findViewById(R.id.input_phone);
        radioMadame = findViewById(R.id.radio_madame);
        radioMonsieur = findViewById(R.id.radio_monsieur);
        btnSave = findViewById(R.id.btn_save);

        // Simulation de données utilisateur
        loadUserProfile();

        // Gestion du bouton "Enregistrer"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUserProfile() {
        // Exemple : récupérer les infos de l'utilisateur (remplacer par une vraie source de données)
        String prenom = "Etienne";
        String nom = "Moussa";
        String birthdate = "21/07/2003";
        String phone = "0683973969";
        boolean isMale = true; // Si l'utilisateur est un homme

        // Affectation aux champs
        inputPrenom.setText(prenom);
        inputNom.setText(nom);
        inputBirthdate.setText(birthdate);
        inputPhone.setText(phone);
        radioMonsieur.setChecked(isMale);
        radioMadame.setChecked(!isMale);

        // Mise à jour du nom affiché
        userName.setText(prenom + " " + nom);
    }

    private void saveProfile() {
        String prenom = inputPrenom.getText().toString().trim();
        String nom = inputNom.getText().toString().trim();
        String birthdate = inputBirthdate.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        boolean isMale = radioMonsieur.isChecked();

        if (prenom.isEmpty() || nom.isEmpty() || birthdate.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enregistrement fictif (ajouter ici la logique pour sauvegarder les données)
        Toast.makeText(this, "Profil enregistré avec succès !", Toast.LENGTH_SHORT).show();
    }
}
