package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;

public class Relatorio extends AppCompatActivity {
    public static final int VOLTAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        setTitle("Relatorio");

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutConteudo);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.layoutVisitas);

        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin",
                new String[]{"Local", "qtdVisitas"}, "", "qtdVisitas desc,Local");

        if(c.getCount() > 0) {

            while (c.moveToNext()) {
                int local = c.getColumnIndex("Local");
                int qtdV = c.getColumnIndex("qtdVisitas");

                TextView text = new TextView(this);
                text.setPadding(0,27,0,25);
                //é obrigatório o layout_width e layout_height
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setText(c.getString(local));
                layout.setPadding(0,0,450,0);
                layout.addView(text);

                LinearLayout.LayoutParams l2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView text2 = new TextView(this);
                text2.setPadding(0,27,0,25);
                //é obrigatório o layout_width e layout_height
                text2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                text2.setText(String.valueOf(c.getInt(qtdV)));

                l2.gravity = Gravity.RIGHT;
                layout2.setPadding(0,0,15,0);
                text2.setLayoutParams(l2);

                layout2.addView(text2);
            }
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