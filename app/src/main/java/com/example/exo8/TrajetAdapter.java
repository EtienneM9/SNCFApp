package com.example.exo8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class TrajetAdapter extends ArrayAdapter<Trajet> {

    private Context context;
    private List<Trajet> trajets;

    public TrajetAdapter(Context context, List<Trajet> trajets) {
        super(context, R.layout.item_trajet, trajets);
        this.context = context;
        this.trajets = trajets;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trajet, parent, false);
        }

        Trajet trajet = trajets.get(position);

        TextView txtGareArrivee = convertView.findViewById(R.id.txtGareArrivee);
        TextView txtGareDep= convertView.findViewById(R.id.txtGareDepart);
        TextView txtHeureDepart = convertView.findViewById(R.id.txtHeureDepart);
        TextView txtHeureArrivee = convertView.findViewById(R.id.txtHeureArrivee);
        TextView txtPrix = convertView.findViewById(R.id.txtPrix);
        TextView txtDuree = convertView.findViewById(R.id.txtDuree);
        TextView txtType = convertView.findViewById(R.id.txtTypeTrain);
        Button btnReserver = convertView.findViewById(R.id.btnReserver);

        txtGareDep.setText(trajet.getVilleDep());
        txtGareArrivee.setText(trajet.getVilleArr());
        txtHeureDepart.setText("Départ : " + trajet.getHeureDepart());
        txtHeureArrivee.setText("Arr : " + trajet.getHeureArrivee());
        txtPrix.setText("Prix : " + trajet.getPrix());
        txtDuree.setText(trajet.getDuree());
        txtType.setText(trajet.getType());

        btnReserver.setOnClickListener(v -> {
            Toast.makeText(context, "Réservé : " + trajet.getHeureDepart() + " - " + trajet.getHeureArrivee(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
