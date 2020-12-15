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

    private String latitude = "";
    private String longitude = "";

    public LatLng MY_POSITION = new LatLng(0, 0);

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);
        setTitle("MapaCheckin");

        Intent it = getIntent();
        latitude =  it.getStringExtra("latitude");
        longitude =  it.getStringExtra("longitude");

        MY_POSITION = new LatLng(parseDouble(latitude), parseDouble(longitude));

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
            case GESTAO:
                Intent it2 = new Intent(this, GestaoCheckin.class);
                startActivity(it2);
                return true;

            case LUGARES:
                Intent it3 = new Intent(this, Relatorio.class);
                startActivity(it3);
                return true;

            case NORMAL:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(MY_POSITION, 17);
                map.animateCamera(update);

                return true;

            case HIBRIDO:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                CameraUpdate update2 = CameraUpdateFactory.newLatLngZoom(MY_POSITION, 17);
                map.animateCamera(update2);

                return true;

            case VOLTAR:
                finish();
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(MY_POSITION, 17);
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

        Toast.makeText(this, "Nenhum local encontradao!", Toast.LENGTH_LONG).show();

        c.close();
    }
}