package com.example.practica1adpsp2020.util;

import java.util.Comparator;

public class LlamadaEntranteComparator implements Comparator<LlamadaEntrante> {


    @Override
    public int compare(LlamadaEntrante llamada1, LlamadaEntrante llamada2) {
        int sort=0;
        sort = llamada1.getNombreContacto().compareTo(llamada2.getNombreContacto());
        if(sort==0){
            sort = llamada1.getYear()-llamada2.getYear();
            if(sort==0){
                sort= llamada1.getHora()-llamada2.getHora();

            }
        }
        return sort;
    }
}
