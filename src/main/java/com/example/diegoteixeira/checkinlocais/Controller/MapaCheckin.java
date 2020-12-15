package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static java.lang.Double.parseDouble;

public class MapaCheckin extends AppCompatActivity implements OnMapReadyCallback {
    public static final int VOLTAR = 1;
    public static final int GESTAO = 2;
    public static final int LUGARES = 3;
    public static final int TIPOS = 4;
    public static final int NORMAL = 5;
    public static final int HIBRIDO = 6;

    public Marker marker;

    private String latitude = "";
    private String longitude = "";

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);
        setTitle("MapaCheckin");

        Intent it = getIntent();
        latitude =  it.getStringExtra("latitude");
        longitude =  it.getStringExtra("longitude");

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync((OnMapReadyCallback) this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem item = menu.add(0, VOLTAR, 1, "Voltar");
        item = menu.add(0, GESTAO, 2, "Gestão de Check-in");
        item = menu.add(0, GESTAO, 3, "Lugares mais visitados");

        SubMenu subMenu = menu.addSubMenu(0, TIPOS, 4, "Tipos de Mapa");
        item = subMenu.add(0, NORMAL, 5, "MAPA NORMAL");
        item = subMenu.add(0, HIBRIDO, 6, "MAPA HÍBRIDO");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case VOLTAR:
                finish();
                return true;

            case GESTAO:
                String titulo2 = item.getTitle().toString();
                Toast.makeText(this, "Item2: " + titulo2, Toast.LENGTH_SHORT).show();
                return true;

            case LUGARES:
                String titulo3 = item.getTitle().toString();
                Toast.makeText(this, "Item3: " + titulo3, Toast.LENGTH_SHORT).show();
                return true;

            case TIPOS:
                String titulo4 = item.getTitle().toString();
                Toast.makeText(this, "Item4: " + titulo4, Toast.LENGTH_SHORT).show();
                return true;

            case NORMAL:
                String titulo5 = item.getTitle().toString();
                Toast.makeText(this, "Item5: " + titulo5, Toast.LENGTH_SHORT).show();
                return true;

            case HIBRIDO:
                String titulo6 = item.getTitle().toString();
                Toast.makeText(this, "Item6: " + titulo6, Toast.LENGTH_SHORT).show();
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            LatLng MY_POSITION = new LatLng(parseDouble(latitude), parseDouble(longitude));

            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(MY_POSITION, 10);
            map.animateCamera(update);

            marcarLugaresVisitados();

        } catch (Exception e) {
            Toast.makeText(this, "Problema com a latitute de longitude", Toast.LENGTH_SHORT).show();
        }

    }

    private void marcarLugaresVisitados() {
        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin ch, Categoria ca",
                new String[]{"ch.Local local","ch.qtdVisitas qtVisit","ch.latitude lat","ch.longitude longi","ca.nome nome"},
                "ch.cat=ca.idCategoria", "");

        if(c.getCount() > 0){

            while (c.moveToNext()) {
                int local = c.getColumnIndex("local");
                int qtd = c.getColumnIndex("qtVisit");
                int catNome = c.getColumnIndex("nome");
                int lat = c.getColumnIndex("lat");
                int longi = c.getColumnIndex("longi");

                LatLng pos = new LatLng(parseDouble(c.getString(lat)), parseDouble(c.getString(longi)));

                map.addMarker(new MarkerOptions().position(pos).title(c.getString(local)).snippet("Categoria: " + c.getString(catNome) +" Visitas: "+c.getInt(qtd)));
            }
            //Toast.makeText(this, aux, Toast.LENGTH_LONG).show();
        }

        c.close();
    }
}