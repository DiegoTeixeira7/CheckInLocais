package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    public LocationListener lm;
    public Criteria criteria;
    public String provider;

    ArrayAdapter<String> adaptador;

    ArrayList<String> empresas = new ArrayList<>();


    private String[] planetas = new String[] { "Mercúrio", "Venus", "Terra", "Marte", "Júptier",
            "Saturno", "Urano","Netuno", "Plutão" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin", new String[]{"Local"}, "", "");

        while(c.moveToNext()){
            int L = c.getColumnIndex("Local");
            empresas.add(c.getString(L));
        }

       // Toast.makeText(this, empresas.get(0), Toast.LENGTH_LONG).show();
        c.close();

        adaptador = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, empresas);
        AutoCompleteTextView Local = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        Local.setAdapter(adaptador);

        Spinner combo = (Spinner) findViewById(R.id.spinnerCategoria);
        combo.setOnItemSelectedListener(this); //configura método de seleção

        //configura adaptador
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, planetas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //informa qual é o adaptador
        combo.setAdapter(adaptador);
    }


    public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
        Toast.makeText(this, "Item: " + planetas[posicao], Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView arg0) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_mapa:
                Intent it = new Intent(this, MapaCheckin.class);
                startActivity(it);
                break;

            case R.id.action_gestao:
                Intent it2 = new Intent(this, GestaoCheckin.class);
                startActivity(it2);
                break;

            case R.id.action_lugares:
                Intent it3 = new Intent(this, Relatorio.class);
                startActivity(it3);
                break;

            default:
                break;
        }

        return true;
    }

    public void checkin(View view) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}

// AIzaSyCK4U3KIbWkwxNVsHA8FwUbn4zKEO3H7E8