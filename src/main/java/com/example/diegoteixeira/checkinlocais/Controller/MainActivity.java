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

    public ArrayAdapter<String> adaptador;
    public ArrayList<String> locaisVisitados = new ArrayList<>();
    public ArrayList<String> categoria = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // busca locais visitados no BD e popula arrayList para exibir no autoCompleteView
        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin", new String[]{"Local"}, "", "");

        while(c.moveToNext()){
            int L = c.getColumnIndex("Local");
            locaisVisitados.add(c.getString(L));
        }

        c.close();

        adaptador = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, locaisVisitados);
        AutoCompleteTextView Local = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        Local.setAdapter(adaptador);

        adaptador.setNotifyOnChange(false);

        // busca categorias no BD e popula arrayList para exibir no autoCompleteView
        c = BancoDadosSingleton.getInstance().buscar("Categoria", new String[]{"nome"}, "", "");

        while(c.moveToNext()){
            int nome = c.getColumnIndex("nome");
            categoria.add(c.getString(nome));
        }

        c.close();

        Spinner combo = (Spinner) findViewById(R.id.spinnerCategoria);
        combo.setOnItemSelectedListener(this); //configura método de seleção

        //configura adaptador
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoria);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //informa qual é o adaptador
        combo.setAdapter(adaptador);

        // Habilitar novamente a notificacao
        adaptador.setNotifyOnChange(true);
        // Notifica o Spinner de que houve mudanca no modelo
        adaptador.notifyDataSetChanged();
    }


    public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
        Toast.makeText(this, "Item: " + categoria.get(posicao), Toast.LENGTH_SHORT).show();
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