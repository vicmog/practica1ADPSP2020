package com.example.practica1adpsp2020.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.practica1adpsp2020.util.LlamadaEntrante;
import com.example.practica1adpsp2020.util.LlamadaEntranteComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.example.practica1adpsp2020.MainActivity.TAG;


public class InComingCallsReceiver extends BroadcastReceiver {


    private Context contexto;
    private String numeroTelefono;
    private String nombreLlamada;

    private LlamadaEntrante llamada;



    @Override
    public void onReceive(Context context, Intent intent) {
        
        contexto = context;

        String estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(estado.equals(TelephonyManager.EXTRA_STATE_IDLE)){

                Calendar fecha = Calendar.getInstance();
                int año = fecha.get(Calendar.YEAR);
                int mes = fecha.get(Calendar.MONTH) + 1;
                int dia = fecha.get(Calendar.DAY_OF_MONTH);
                int hora = fecha.get(Calendar.HOUR_OF_DAY);
                int minuto = fecha.get(Calendar.MINUTE);
                int segundo = fecha.get(Calendar.SECOND);


                numeroTelefono = obtnerNumeroTelefono(contexto.getContentResolver());
                Log.v(TAG,numeroTelefono+"  receiver");

                lanzaHebraObtnerNombre();

                llamada = new LlamadaEntrante(nombreLlamada,año,mes,dia,hora,minuto,segundo,numeroTelefono);
                lanzaHebra();



            }


    }

    private void lanzaHebraObtnerNombre() {
        Thread hebraBuscaContacto = new Thread(){
            @Override
            public void run() {

                nombreLlamada = obtenerNombreContacto(contexto.getContentResolver());
            }
        };
        hebraBuscaContacto.start();
        try {
            hebraBuscaContacto.join();
        } catch (InterruptedException e) {

        }
    }

    private void lanzaHebra() {
        Thread hebraGuardaLlamada = new Thread(){

            @Override
            public void run() {

                saveLlamada(llamada);
            }
        };

        hebraGuardaLlamada.start();
        try {
            hebraGuardaLlamada.join();
        } catch (InterruptedException e) {

        }
    }


    public  String obtnerNumeroTelefono(ContentResolver cr){


            String numero="";

            Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                    while(cur.moveToNext()){
                        if(cur.moveToLast()){
                            numero = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));

                        }
                    }


            return numero;
        }



    private void saveLlamada(LlamadaEntrante llamada) {
        saveLlamadaFileDir(llamada);
        saveLlamadaExternalFilesDir(llamada);
    }

    private void saveLlamadaExternalFilesDir(LlamadaEntrante llamada) {
        ArrayList<LlamadaEntrante> listaLlamadas=getListLlamadasExternal();
        listaLlamadas.add(llamada);
        Collections.sort(listaLlamadas,new LlamadaEntranteComparator());
        File f = new File(contexto.getExternalFilesDir(null),"llamadas.csv");
        FileWriter fw= null;
        try {
            fw = new FileWriter(f);
            for(LlamadaEntrante llamadaNueva :listaLlamadas){
                fw.write(llamadaNueva.toCsv2()+"\n");

            }

            fw.flush();
            fw.close();

        } catch (IOException e) {

        }

    }

    private ArrayList<LlamadaEntrante> getListLlamadasExternal() {
        ArrayList<LlamadaEntrante> listaLlamadas = new ArrayList<>();
        File f = new File(contexto.getExternalFilesDir(null), "llamadas.csv");
        BufferedReader br= null;
        try {
            br = new BufferedReader(new FileReader(f));
            String linea;

            while((linea = br.readLine()) != null) {

            listaLlamadas.add(LlamadaEntrante.fromStringToLlamada2(";",linea));

            }

            br.close();

        } catch (IOException e) {

        }

        return listaLlamadas;
    }

    private void saveLlamadaFileDir(LlamadaEntrante llamada) {
        ArrayList<LlamadaEntrante> listaLlamadas=getListLlamadasFileDir();
        listaLlamadas.add(llamada);
        File f = new File(contexto.getFilesDir(),"historial.csv");
        FileWriter fw= null;
        try {
            fw = new FileWriter(f);
            for(LlamadaEntrante llamadaNueva :listaLlamadas){
                fw.write(llamadaNueva.toCsv()+"\n");

            }

            fw.flush();
            fw.close();

        } catch (IOException e) {

        }


    }

    private ArrayList<LlamadaEntrante> getListLlamadasFileDir() {
        ArrayList<LlamadaEntrante> listaLlamadas = new ArrayList<>();
        File f = new File(contexto.getFilesDir(), "historial.csv");
        BufferedReader br= null;
        try {
            br = new BufferedReader(new FileReader(f));
            String linea;

            while((linea = br.readLine()) != null) {

                listaLlamadas.add(LlamadaEntrante.fromStringToLlamada(";",linea));

            }

            br.close();

        } catch (IOException e) {

        }

        return listaLlamadas;
    }


    private String obtenerNombreContacto(ContentResolver cr) {
        String nombre ="Desconocido";


            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor cur2 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);

                        while (cur2.moveToNext()) {

                            String phoneNumber = cur2.getString(cur2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String numero ="";

                            for(int i =0;i<phoneNumber.length();i++){
                                if(Character.isDigit(phoneNumber.charAt(i))){
                                    numero = numero + phoneNumber.charAt(i);
                                }

                            }

                            if(numero.equals(numeroTelefono)){
                                nombre=name;

                            }

                        }
                        cur2.close();
                    }
                }
            }

        return nombre;


    }


}