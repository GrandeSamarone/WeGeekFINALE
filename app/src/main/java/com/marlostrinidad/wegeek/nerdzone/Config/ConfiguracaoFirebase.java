package com.marlostrinidad.wegeek.nerdzone.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by fulanoeciclano on 19/05/2018.
 */

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;
    private static FirebaseDatabase data;
    //retorna a instance do firebase

    public static FirebaseDatabase getDatabase() {
        if (data == null) {
            data = FirebaseDatabase.getInstance();
            data.setPersistenceEnabled(true);
        }
        return data;
    }

    public static DatabaseReference getFirebaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();


        }
        return database;

    }

    // retorna a instance do auth;

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(auth == null){
            auth=FirebaseAuth.getInstance();
        }
        return  auth;
    }

    public static StorageReference getFirebaseStorage(){
        if(storage==null){
            storage= FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
    public  static String getIdUsuario(){
        FirebaseAuth autenticacao = getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }
}

