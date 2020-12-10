package com.example.diegoteixeira.checkinlocais;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

public class MapaCheckin extends AppCompatActivity {
    public static final int VOLTAR = 1;
    public static final int GESTAO = 2;
    public static final int LUGARES = 3;
    public static final int TIPOS = 4;
    public static final int NORMAL = 5;
    public static final int HIBRIDO = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);
        setTitle("MapaCheckin");
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

}