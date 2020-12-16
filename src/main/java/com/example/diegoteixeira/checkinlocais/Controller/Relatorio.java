package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diegoteixeira.checkinlocais.R;

public class Relatorio extends AppCompatActivity {
    public static final int VOLTAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        setTitle("Relatorio");

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutConteudo);

        for (int i = 0; i < 100; i++) {
            TextView text = new TextView(this);
            //é obrigatório o layout_width e layout_height
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setText("Texto: " + i);
            layout.addView(text);
        }
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