package com.example.exo8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ShowTikets extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private ListView listeTickets;
    private List<Ticket> tickets;
    private TicketAdapter adapter;



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
}
