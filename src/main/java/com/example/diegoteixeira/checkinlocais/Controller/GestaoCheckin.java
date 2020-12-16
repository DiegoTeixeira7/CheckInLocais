package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static java.lang.Double.parseDouble;

public class GestaoCheckin extends AppCompatActivity {
    public static final int VOLTAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_checkin);
        setTitle("GestaoCheckin");

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutConteudo);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.layoutDeletar);

        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin",
                new String[]{"Local"}, "", "Local");

        if(c.getCount() > 0) {

            while (c.moveToNext()) {
                int local = c.getColumnIndex("Local");

                TextView text = new TextView(this);
                text.setPadding(0,27,0,25);
                //é obrigatório o layout_width e layout_height
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setText(c.getString(local));
                layout.addView(text);

                ImageButton btn = new ImageButton(this);
                btn.setTag(c.getString(local));
                btn.setImageResource(R.drawable.ic_baseline_delete_forever_24);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletarC(v);
                    }
                });
                layout.setVerticalGravity(4);
                layout2.addView(btn);
            }
        }
    }

    private void deletarC(View v) {
        final String tag = v.getTag().toString();

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Exclusão");
        alerta.setMessage("Tem certeza que deseja excluir " + tag + "?");

        // Configura Método executado se escolher Sim
        alerta.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BancoDadosSingleton.getInstance().deletar("Checkin", "Local='"+tag+"'");
                recreate();
            }
        });

        // Configura Método executado se escolher Não
        alerta.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alerta.show();
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