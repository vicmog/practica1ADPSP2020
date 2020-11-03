package com.example.practica1adpsp2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.practica1adpsp2020.settings.SettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "xzy";
    public static boolean permiso = false;
    private final int PERMISOSAPP = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private char modo;
    private TextView tvLlamada;
    private TextView tvTitulo;





    //Metodos del ciclo de vida
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflate = getMenuInflater();
        menuInflate.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAjustes:
                return abrirAjustes();
        }


        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        obtenerPreferenciasActuales();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        obtenerPreferenciasActuales();
        readLlamadas();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISOSAPP:
                int contador = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        contador++;

                    }
                }
                if (contador == grantResults.length) {
                    permiso = true;
                } else {

                }
                break;

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        obtenerPreferenciasActuales();
    }

    @Override
    protected void onStart() {
        super.onStart();
        obtenerPreferenciasActuales();
    }

    //Otros metodos
    private boolean abrirAjustes() {
        Intent intenteAjustes = new Intent(this, SettingsActivity.class);
        startActivity(intenteAjustes);
        return true;
    }

    private void init() {
        Button btVerHistorial = findViewById(R.id.btVerHistorial);
        listener = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        tvLlamada = findViewById(R.id.tvLlamada);
        tvTitulo = findViewById(R.id.tvTitulo);
        btVerHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermiso();
                if(permiso){
                    readLlamadas();

                }
            }
        });


    }

    private void mostrarInfromacionDetallada() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.titulo_permiso);
        builder.setMessage(R.string.mensaje_permiso);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, PERMISOSAPP);
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();

    }

    private void obtenerPreferenciasActuales() {
        boolean activado = true;

        if (sharedPreferences.getBoolean("swpFile", true)) {
            activado = sharedPreferences.getBoolean("swpExternal", false);
            if (activado) {

                modo = 'x';

            } else {

                modo = 'f';
            }

        }else{
            modo= 'e';
        }
    }

    private void pedirPermiso() {
        int permisoPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permisoReadCallLog = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int permisoReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (permisoPhoneState == PackageManager.PERMISSION_GRANTED && permisoReadCallLog == PackageManager.PERMISSION_GRANTED && permisoReadContacts == PackageManager.PERMISSION_GRANTED)) {

            permiso = true;

        } else {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG) || shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                mostrarInfromacionDetallada();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, PERMISOSAPP);
            }

        }
    }
    private void readLlamadas() {
        obtenerPreferenciasActuales();
        if(modo=='f'){
            tvTitulo.setText(R.string.str_TituloDatosFileDir);
            File f = new File(getFilesDir(), "historial.csv");
            BufferedReader br= null;
            try {
                br = new BufferedReader(new FileReader(f));
                String linea;
                StringBuilder texto = new StringBuilder();
                while((linea = br.readLine()) != null) {
                    texto.append(linea);
                    texto.append('\n');
                }


                tvLlamada.setText(texto);

                br.close();

            } catch (IOException e) {

            }

        }else if(modo=='x'){
            tvTitulo.setText(R.string.str_TituloDatosExternalFileDir);
            File f = new File(getExternalFilesDir(null), "llamadas.csv");
            BufferedReader br= null;
            try {
                br = new BufferedReader(new FileReader(f));
                String linea;
                StringBuilder texto = new StringBuilder();
                while((linea = br.readLine()) != null) {
                    texto.append(linea);
                    texto.append('\n');
                }

                tvLlamada.setText(texto);

                br.close();

            } catch (IOException e) {

            }

        }else if(modo=='e'){
            tvTitulo.setText("");
        tvLlamada.setText(R.string.str_preferencias);
        }

    }
}
//Codigo util
/*else if (sharedPreferences.getBoolean("swpExternal", true)) {
            activado = sharedPreferences.getBoolean("swpFile", false);
            if (activado) {

                modo = 'e';
            } else {

                modo = 'x';
            }

        } else if (!sharedPreferences.getBoolean("swpFile", true)) {
            activado = sharedPreferences.getBoolean("swpExternal", false);
            if (activado) {

                modo = 'x';

            } else {

                modo = 'e';
            }
        } else if (!sharedPreferences.getBoolean("swpExternal", true)) {
            activado = sharedPreferences.getBoolean("swpFile", false);
            if (activado) {

                modo = 'f';

            } else {
                modo = 'e';
                tvLlamada.setText("");
                tvLlamada.setText(R.string.str_preferencias);
            }
        }*/