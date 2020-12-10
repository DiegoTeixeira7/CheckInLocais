package com.example.diegoteixeira.checkinlocais;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Relatorio extends AppCompatActivity {
    public static final int VOLTAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        setTitle("Relatorio");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem item = menu.add(0, VOLTAR, 1, "Voltar");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == VOLTAR) {
            finish();
            return true;
        }

        return false;
    }
}