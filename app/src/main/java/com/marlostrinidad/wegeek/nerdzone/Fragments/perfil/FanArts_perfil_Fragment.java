package com.marlostrinidad.wegeek.nerdzone.Fragments.perfil;


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
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem_Art;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_FanArts;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.Main;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FanArts_perfil_Fragment extends Fragment  implements Main {

    private DatabaseReference mDatabaseart;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerart;
    private Adapter_FanArts adapter_art;
    private ArrayList<FanArts> ListaArt = new ArrayList<>();
    private ChildEventListener valueEventListenerConto;
    private LinearLayout nadaencontrado;
    public FanArts_perfil_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fan_arts_perfil_, container, false);
         nadaencontrado=view.findViewById(R.id.linear_nada_cadastrado_perfil_fanart);
        recyclerart = view.findViewById(R.id.perfil_lista_art);
        mDatabaseart = ConfiguracaoFirebase.getFirebaseDatabase().child("arts");
        //Configuracao Adapter
        adapter_art =new Adapter_FanArts(ListaArt,getActivity());
        //Adapter
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, LinearLayoutManager.VERTICAL);

        recyclerart.setLayoutManager(staggeredGridLayoutManager);
        recyclerart.setHasFixedSize(true);
        recyclerart.setAdapter(adapter_art);

        recyclerart.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerart, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FanArts arteselecionada = ListaArt.get(position);
                Intent it = new Intent(getContext(), AbrirImagem_Art.class);
                it.putExtra("id_foto",arteselecionada.getArtfoto());
                it.putExtra("id",arteselecionada.getId());
                it.putExtra("categoria",arteselecionada.getCategoria());
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

    public void onStop() {
        super.onStop();
        mDatabaseart.removeEventListener(valueEventListenerConto);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        this.unregisterEventBus();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerEventBus();
    }

    @Override
    public void registerEventBus() {

        try {
            EventBus.getDefault().register(this);
        }catch (Exception Err){


        }

    }

    @Override
    public void unregisterEventBus() {
        try {
            EventBus.getDefault().unregister(this);
        }catch (Exception e){

        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void UsuarioSelecionado(String account) {

        RecuperarArt(account);
        //  Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();

    }

    public void RecuperarArt(final String id ){
        ListaArt.clear();
        nadaencontrado.setVisibility(View.VISIBLE);
        valueEventListenerConto=mDatabaseart
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                            FanArts conto = categoria.getValue(FanArts.class);
                            if (conto.getIdauthor().equals(id)) {
                                ListaArt.add(0, conto);
                                if (ListaArt.size() > 0) {
                                    nadaencontrado.setVisibility(View.GONE);
                                }
                                adapter_art.notifyDataSetChanged();
                            }
                        }
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