package com.marlostrinidad.wegeek.nerdzone.Fragments.MinhaConta;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem_Art;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_FanArts;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Art_MinhaConta_Fragment extends Fragment {

    private DatabaseReference mDatabaseart;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerArt;
    private Adapter_FanArts adapter_art;
    private ArrayList<FanArts> ListaArts = new ArrayList<>();
    private ChildEventListener valueEventListenerArt;

    public Art_MinhaConta_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_art__perfil_, container, false);

        recyclerArt = view.findViewById(R.id.lista_farArts);
        mDatabaseart = ConfiguracaoFirebase.getFirebaseDatabase().child("arts");
        //Configuracao Adapter
        adapter_art = new Adapter_FanArts(ListaArts, getActivity());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, LinearLayoutManager.VERTICAL);

        recyclerArt.setLayoutManager(staggeredGridLayoutManager);
        recyclerArt.setHasFixedSize(true);
        recyclerArt.setAdapter(adapter_art);


        recyclerArt.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerArt, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FanArts arteselecionada = ListaArts.get(position);
                Intent it = new Intent(getContext(), AbrirImagem_Art.class);
                it.putExtra("id_foto",arteselecionada.getArtfoto());
                it.putExtra("id",arteselecionada.getId());
                it.putExtra("nome_foto",arteselecionada.getLegenda());
                startActivity(it);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        return view;
    }
        @Override
        public void onStart() {
            super.onStart();
            RecuperarArt();

        }

        public void onStop() {
            super.onStop();
            mDatabaseart.removeEventListener(valueEventListenerArt);
        }




        public void RecuperarArt(){
            ListaArts.clear();
            String identificadoUsuario= UsuarioFirebase.getIdentificadorUsuario();
            valueEventListenerArt=mDatabaseart.orderByChild("idauthor").equalTo(identificadoUsuario)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            FanArts fanArts = dataSnapshot.getValue(FanArts.class);
                            ListaArts.add(0,fanArts);

                            adapter_art.notifyDataSetChanged();

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