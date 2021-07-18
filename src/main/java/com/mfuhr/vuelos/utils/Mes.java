package com.mfuhr.vuelos.utils;

import java.util.Arrays;

public enum Mes {
    
    ENE("01","Enero","JAN"),
    FEB("02","Febrero","FEB"),
    MAR("03","Marzo","MAR"),
    ABR("04","Abril","APR"),
    MAY("05","Mayo","MAY"),
    JUN("06","Junio","JUN"),
    JUL("07","Julio","JUL"),
    AGO("08","Agosto","AUG"),
    SEP("09","Septiembre","SEP"),
    OCT("10","Octubre","OCT"),
    NOV("11","Noviembre","NOV"),
    DIC("12","Diciembre","DEC");


    private String nro;
    private String nombreMes;
    private String codMes;

    Mes(String nro, String nombreMes,String codMes){
        this.nro = nro;
        this.nombreMes = nombreMes;
        this.codMes = codMes;
    }


    public String getNro() {
        return nro;
    }

    public String getNombreMes() {
        return nombreMes;
    }

    public String getCodMes() {
        return codMes;
    }
    
    public static Mes getMesByEnum(String valor){
        return Arrays.stream(Mes.values()).filter( mesEncontrado -> mesEncontrado.name().equals(valor)).findFirst().get();
    }

    public static Mes getMesByCod(String valor){
        return Arrays.stream(Mes.values()).filter( mesEncontrado -> mesEncontrado.getCodMes().equals(valor)).findFirst().get();
    }
    
}
