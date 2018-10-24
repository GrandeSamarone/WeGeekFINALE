package com.marlostrinidad.wegeek.nerdzone.Helper;

import android.util.Base64;

/**
 * Created by fulanoeciclano on 21/05/2018.
 */

public class Base64Custom {

    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    public static String decodificarBase64(String textocodificado){
        return  new String(Base64.decode(textocodificado, Base64.DEFAULT));

    }
}
