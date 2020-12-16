package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    public ArrayAdapter<String> adaptador;
    public ArrayList<String> locaisVisitados = new ArrayList<>();
    public ArrayList<String> categoria = new ArrayList<>();
    public Map<String, Integer> map = new HashMap<String, Integer>();

    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;

    private String localDigitado = "";
    private int categoriaLocal = -1;
    private String latitude = "";
    private String longitude = "";

    private final int LOCATION_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // busca locais visitados no BD e popula arrayList para exibir no autoCompleteView
        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin", new String[]{"Local"}, "", "");

        while (c.moveToNext()) {
            int L = c.getColumnIndex("Local");
            locaisVisitados.add(c.getString(L));
        }

        c.close();

        adaptador = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, locaisVisitados);
        AutoCompleteTextView Local = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        Local.setAdapter(adaptador);

        adaptador.setNotifyOnChange(false);

        // busca categorias no BD e popula arrayList para exibir no autoCompleteView
        c = BancoDadosSingleton.getInstance().buscar("Categoria", new String[]{"nome", "idCategoria"}, "", "");

        while (c.moveToNext()) {
            int nome = c.getColumnIndex("nome");
            int idCategoria = c.getColumnIndex("idCategoria");
            categoria.add(c.getString(nome));
            map.put(c.getString(nome), c.getInt(idCategoria));
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

        //Location Manager
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        //Testa se o aparelho tem GPS
        PackageManager packageManager = getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        //Estabelece critério de precisão
        if (hasGPS) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "Usando GPS");
        } else {
            Log.i("LOCATION", "Usando WI-FI ou dados");
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }
    }

    public void onStart() {
        super.onStart();

        //Obtem melhor provedor habilitado com o critério estabelecido
        provider = lm.getBestProvider(criteria, true);

        if (provider == null) {
            Log.e("PROVEDOR", "Nenhum provedor encontrado!");
        } else {
            Log.i("PROVEDOR", "Está sendo utilizado o provedor: " + provider);

            requestPermissions();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(provider, TEMPO_REQUISICAO_LATLONG, DISTANCIA_MIN_METROS, this);
        }
    }

    @Override
    public  void onDestroy() {
        //interrompe o Location Manager
        lm.removeUpdates((android.location.LocationListener) this);
        Log.w("PROVEDOR","Provedor " + provider + " parado!");

        super.onDestroy();
    }


    public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
        this.categoriaLocal = map.get(categoria.get(posicao));
        //Toast.makeText(this, "categoriaLocal: " + categoriaLocal, Toast.LENGTH_SHORT).show();
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
                requestPermissions();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Posicionamento global não localizado!",Toast.LENGTH_SHORT).show();
                } else {
                    if(latitude.equals("") || longitude.equals("")) {
                        Log.d("LOCATION", "Posição estática");
                        Toast.makeText(this, "Posicionamento global não localizado!",Toast.LENGTH_SHORT).show();
                        //latitude = "-20.755921";
                        //longitude = "-42.8804686";
                    }
                    else {
                        Intent it = new Intent(this, MapaCheckin.class);
                        it.putExtra("latitude", latitude);
                        it.putExtra("longitude", longitude);
                        startActivity(it);
                    }
                }

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
        EditText autoC = findViewById(R.id.autoCompleteTextView);
        this.localDigitado = autoC.getText().toString();

        if(localDigitado.equals("") || categoriaLocal == -1) {
            Toast.makeText(this, "Prenencha todos os campos!",Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions();

            if(latitude.equals("") || longitude.equals("")) {
                //Log.d("LOCATION", "Posição estática");
                //latitude = "-20.755921";
                //longitude = "-42.8804686";
                Toast.makeText(this, "Posicionamento global não localizado!",Toast.LENGTH_SHORT).show();
            }
            else {

//                Toast.makeText(this, "latitude: "+latitude+"\n"+"longitude: "+longitude+"\n"
//                        +"localDigitado: "+localDigitado+"\n"+"categoriaLocal: "+categoriaLocal+"\n", Toast.LENGTH_SHORT).show();

                int busca = busca();

                if(busca == -1){
                    inserir();
                } else {
                    atualizar(busca);
                }

                recreate();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // obtem atributos na localização atual
        double lat = location.getLatitude();
        double longi = location.getLongitude();

        this.latitude = String.valueOf(lat);
        this.longitude = String.valueOf(longi);

        TextView la = findViewById(R.id.latEdit);
        TextView lon = findViewById(R.id.longEdit);

        la.setText(this.latitude);
        lon.setText(this.latitude);

        //Toast.makeText(this, "latitude: "+lat+"longitude: "+longi, Toast.LENGTH_LONG).show();
    }

    private void requestPermissions() {
        //Obtem atualizações de posição
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);
            Log.i("Permission", "Pede a permissão");
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "Deu a permissão");
            } else {
                Log.i("Permission", "Não permitiu");
            }
            return;
        }
    }

    public void onProviderDisabled(String provider){
        Log.d("LOCATION", "Desabilitou o provedor");
    }

    public void onProviderEnabled(String provider){
        Log.d("LOCATION", "Habilitou o provedor");
    }

    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d("LOCATION", "Provedor mudou de estação");
    }

    private int busca() {
        Cursor c = BancoDadosSingleton.getInstance().buscar("Checkin", new String[]{"qtdVisitas"}, "Local='"+localDigitado+"'", "");

        if(c.getCount() == 1){
            int qtd = 0;
            while (c.moveToNext()) {
                int L = c.getColumnIndex("qtdVisitas");
                qtd = c.getInt(L);
            }

            c.close();
            return qtd;
        } else {
            c.close();
            return -1;
        }

    }

    private void inserir() {
        ContentValues valores = new ContentValues();
        valores.put("Local", localDigitado);
        valores.put("qtdVisitas", 1);
        valores.put("cat", categoriaLocal);
        valores.put("latitude", latitude);
        valores.put("longitude", longitude);

        BancoDadosSingleton.getInstance().inserir("Checkin", valores);
    }

    private void atualizar(int qtdVisitas) {
        ContentValues valores = new ContentValues();
        valores.put("qtdVisitas", qtdVisitas + 1);

        BancoDadosSingleton.getInstance().atualizar("Checkin", valores, "Local='"+localDigitado+"'");
    }
}