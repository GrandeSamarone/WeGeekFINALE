package com.marlostrinidad.wegeek.nerdzone.Config;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;


import static android.content.ContentValues.TAG;

/**
 * Created by fulanoeciclano on 19/05/2018.
 */

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;
    private static FirebaseDatabase data;
    private  static FirebaseFirestore db_store;
    private static DocumentReference docRef;
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



    public static FirebaseFirestore getFirebaseFirestore(){
        if(db_store==null){
            db_store = FirebaseFirestore.getInstance();
        }
        return db_store;
    }
    public static DocumentReference getFirebasedoref(String id ){
        db_store = FirebaseFirestore.getInstance();

            docRef = db_store.collection("Usuarios").document(id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Usuario user = document.toObject(Usuario.class);
                           //eturn user.getNome();

                            Log.i("sdsd24",user.getNome());
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        return docRef;
    }



}

