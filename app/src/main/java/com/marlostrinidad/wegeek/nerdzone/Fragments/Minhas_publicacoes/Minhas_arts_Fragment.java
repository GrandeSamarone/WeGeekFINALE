package com.marlostrinidad.wegeek.nerdzone.Fragments.Minhas_publicacoes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes.Adapter_Minhas_arts;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Minhas_arts_Fragment extends Fragment {

    private List<FanArts> ListaMinhas_FanArts = new ArrayList<>();
    RecyclerView recyclerViewminhasArts;
    private ChildEventListener ChildEventListenerFanArts;
    private DatabaseReference databaseart;
    private Adapter_Minhas_arts adapter_minhas_arts;

    public Minhas_arts_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view=inflater.inflate(R.layout.fragment_minhas_arts_, container, false);

        databaseart = ConfiguracaoFirebase.getDatabase().getReference().child("minhasarts");

        adapter_minhas_arts = new Adapter_Minhas_arts(getActivity(),ListaMinhas_FanArts);
        recyclerViewminhasArts = view.findViewById(R.id.recyclerMinhasArt);
        RecyclerView.LayoutManager layoutManagercomercio = new LinearLayoutManager(getActivity());
        recyclerViewminhasArts.setLayoutManager(layoutManagercomercio);
        recyclerViewminhasArts.setHasFixedSize(true);
        recyclerViewminhasArts.setAdapter(adapter_minhas_arts);


        RecuperarArtsRecycle();

         return view;
    }

    private void RecuperarArtsRecycle(){
        ListaMinhas_FanArts.clear();
          String identificadorUsuario =  UsuarioFirebase.getIdentificadorUsuario();
        ChildEventListenerFanArts = databaseart.child(identificadorUsuario).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FanArts fanArts = dataSnapshot.getValue(FanArts.class);
                ListaMinhas_FanArts.add(0,fanArts);
                adapter_minhas_arts.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
