package com.marlostrinidad.wegeek.nerdzone.Helper;

import androidx.appcompat.app.AppCompatActivity;

public class Reload {

    public static void finishApp(AppCompatActivity appCompatActivity){
appCompatActivity.finish();
    }
    public static void RefreshApp(AppCompatActivity appCompatActivity){
appCompatActivity.recreate();
    }

}
