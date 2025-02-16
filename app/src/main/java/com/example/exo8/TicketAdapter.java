package com.example.exo8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.exo8.R;
import com.example.exo8.Ticket;

import java.util.List;

public class TicketAdapter extends BaseAdapter {
    private Context context;
    private List<Ticket> tickets;
    private LayoutInflater inflater;

    public TicketAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return tickets.size(); }

    @Override
    public Object getItem(int position) { return tickets.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_tickets, parent, false);
        }

        Ticket ticket = tickets.get(position);

        TextView tvDestination = convertView.findViewById(R.id.tv_destination);
        TextView tvDepartHeure = convertView.findViewById(R.id.tv_depart_heure);
        TextView tvDepartGare = convertView.findViewById(R.id.tv_depart_gare);
        TextView tvArriveeHeure = convertView.findViewById(R.id.tv_arrivee_heure);
        TextView tvArriveeGare = convertView.findViewById(R.id.tv_arrivee_gare);
        Button btnVoirBillet = convertView.findViewById(R.id.btn_voir_billet);

        tvDestination.setText("Voyage à " + ticket.getDestination());
        tvDepartHeure.setText(ticket.getHeureDepart());
        tvDepartGare.setText(" - " + ticket.getGareDepart());
        tvArriveeHeure.setText(ticket.getHeureArrivee());
        tvArriveeGare.setText(" - " + ticket.getGareArrivee());

        // Action sur le bouton "Voir votre billet"
        btnVoirBillet.setOnClickListener(v -> {
            // Ajoute ici une action (ex: ouvrir une nouvelle activité)
        });

        return convertView;
    }
}
