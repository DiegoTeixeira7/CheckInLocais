package com.example.diegoteixeira.checkinlocais.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.Toast;

import com.example.diegoteixeira.checkinlocais.R;
import com.example.diegoteixeira.checkinlocais.Util.BancoDadosSingleton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    public ArrayAdapter<String> adaptador;
    public ArrayList<String> locaisVisitados = new ArrayList<>();
    public ArrayList<String> categoria = new ArrayList<>();

    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;

    private String localDigitado = "";
    private String categoriaLocal = "";
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
        c = BancoDadosSingleton.getInstance().buscar("Categoria", new String[]{"nome"}, "", "");

        while (c.moveToNext()) {
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

            //Obtem atualizações de posição
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
                Log.i("Permission", "Pede a permissão");
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
        this.categoriaLocal = categoria.get(posicao);
        //Toast.makeText(this, "Item: " + categoria.get(posicao), Toast.LENGTH_SHORT).show();
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
        if(latitude.equals("") || longitude.equals("")) {
            Log.d("LOCATION", "Posição estática");
            latitude = "-20.755921";
            longitude = "-42.8804686";
        }

        EditText autoC = findViewById(R.id.autoCompleteTextView);
        this.localDigitado = autoC.getText().toString();

        Toast.makeText(this, "latitude: "+latitude+"\n"+"longitude: "+longitude+"\n"
                +"localDigitado: "+localDigitado+"\n"+"categoriaLocal: "+categoriaLocal+"\n", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // obtem atributos na localização atual
        double lat = location.getLatitude();
        double longi = location.getLongitude();

        this.latitude = String.valueOf(lat);
        this.longitude = String.valueOf(longi);

        Toast.makeText(this, "latitude: "+lat+"longitude: "+longi, Toast.LENGTH_LONG).show();
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
}

// AIzaSyCK4U3KIbWkwxNVsHA8FwUbn4zKEO3H7E8