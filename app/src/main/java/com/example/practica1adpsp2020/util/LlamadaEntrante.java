package com.example.practica1adpsp2020.util;
import android.util.Log;

import java.util.Objects;
import static com.example.practica1adpsp2020.MainActivity.TAG;


public class LlamadaEntrante implements Comparable<LlamadaEntrante>{

    private String nombreContacto;
    private int year;
    private int mes;
    private int dia;
    private int hora;
    private int minutos;
    private int segundos;
    private String numeroTelefono;

    public LlamadaEntrante() {
    }

    public LlamadaEntrante(String nombreContacto, int year, int mes, int dia, int hora, int minutos, int segundos, String numeroTelefono) {

        this.nombreContacto = nombreContacto;
        this.year = year;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.minutos = minutos;
        this.segundos = segundos;
        this.numeroTelefono = numeroTelefono;
    }
    public LlamadaEntrante( int year, int mes, int dia, int hora, int minutos, int segundos, String numeroTelefono,String nombreContacto) {

        this.nombreContacto = nombreContacto;
        this.year = year;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.minutos = minutos;
        this.segundos = segundos;
        this.numeroTelefono = numeroTelefono;
    }


    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LlamadaEntrante that = (LlamadaEntrante) o;
        return year == that.year &&
                mes == that.mes &&
                dia == that.dia &&
                hora == that.hora &&
                minutos == that.minutos &&
                segundos == that.segundos &&
                Objects.equals(nombreContacto, that.nombreContacto) &&
                Objects.equals(numeroTelefono, that.numeroTelefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreContacto, year, mes, dia, hora, minutos, segundos, numeroTelefono);
    }

    @Override
    public String toString() {
        return "LlamadaEntrante{" +
                "nombreContacto='" + nombreContacto + '\'' +
                ", year=" + year +
                ", mes=" + mes +
                ", dia=" + dia +
                ", hora=" + hora +
                ", minutos=" + minutos +
                ", segundos=" + segundos +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                '}';
    }

    public  String toCsv(){
        return year+"; "+mes+"; "+dia+"; "+hora+"; "+minutos+"; "+segundos+"; "+numeroTelefono+"; "+nombreContacto;
    }
    public  String toCsv2(){
        return nombreContacto+"; "+year+"; "+mes+"; "+dia+"; "+hora+"; "+minutos+"; "+segundos+"; "+numeroTelefono;
    }
    public static LlamadaEntrante fromStringToLlamada(String separador , String linea){

        String [] trozos = linea.split(separador);

        LlamadaEntrante llamada = null;
        if(trozos.length==8){

            llamada = new LlamadaEntrante(Integer.parseInt(trozos[0].trim()),Integer.parseInt(trozos[1].trim()),Integer.parseInt(trozos[2].trim()),Integer.parseInt(trozos[3].trim()),Integer.parseInt(trozos[4].trim()),Integer.parseInt(trozos[5].trim()),trozos[6].trim(),(trozos[7].trim()));

        }

        return llamada;

    }


    public static LlamadaEntrante fromStringToLlamada2(String separador , String linea){

        String [] trozos = linea.split(separador);

        LlamadaEntrante llamada = null;
        if(trozos.length==8){

            llamada = new LlamadaEntrante(trozos[0].trim(),Integer.parseInt(trozos[1].trim()),Integer.parseInt(trozos[2].trim()),Integer.parseInt(trozos[3].trim()),Integer.parseInt(trozos[4].trim()),Integer.parseInt(trozos[5].trim()),Integer.parseInt(trozos[6].trim()),(trozos[7].trim()));

        }

        return llamada;

    }

    @Override
    public int compareTo(LlamadaEntrante llamada) {
        int sort = this.nombreContacto.compareTo(llamada.nombreContacto);
        if(sort == 0) {
            sort = this.year-llamada.year;
            if(sort == 0) {
                sort = this.hora-llamada.hora;
            }
        }
        return sort;
    }
}

