package com.marlostrinidad.wegeek.nerdzone.Fragments.Minhas_publicacoes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes.Adapter_Meus_Comercio;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes.Adapter_Meus_Eventos;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Loja_Evento_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView_meus_eventos, recyclerView_meus_comercios;
    private DatabaseReference database, databaseusuario;
    private Query meus_eventosref, meus_comercioref;
    private String identificadoUsuario;
    private TextView nomeevento, nomecomercio, errobusca;
    private Evento evento;
    private List<Evento> lista_Meus_Eventos = new ArrayList<>();
    private List<Comercio> lista_meus_comercio = new ArrayList<>();
    private Adapter_Meus_Eventos mAdapter;
    private Adapter_Meus_Comercio adapterComercio;
    private ChildEventListener childEventListenerMeus_Eventos, childEventListenerMeus_Comercio;
    private CircleImageView icone;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private SwipeRefreshLayout refresh;
    private LinearLayout linearLayout;

    public Loja_Evento_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loja__evento_, container, false);

        //Configurações Originais
        linearLayout = view.findViewById(R.id.linear_nada_cadastrado_lojaevento);
        database = FirebaseDatabase.getInstance().getReference();
        nomeevento = view.findViewById(R.id.eventotexto);
        nomecomercio = view.findViewById(R.id.comerciotexto);
        databaseusuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        evento = new Evento();
        refresh = view.findViewById(R.id.atualizarminhas_lojas_eventos_refresh);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                RecuperarMeus_Comercio();
                RecuperarMeus_Eventos();
            }
        });
        refresh.setColorSchemeResources
                (R.color.colorPrimaryDark, R.color.amareloclaro,
                        R.color.accent);
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        meus_eventosref = database.child("meusevento").child(identificadorUsuario);
        meus_comercioref = database.child("meuscomercio").child(identificadorUsuario);

        mAdapter = new Adapter_Meus_Eventos(lista_Meus_Eventos, getActivity());
        adapterComercio = new Adapter_Meus_Comercio(lista_meus_comercio, getActivity());

        recyclerView_meus_comercios = view.findViewById(R.id.recycler_minhas_lojas);
        RecyclerView.LayoutManager layoutManagercomercio = new LinearLayoutManager(getActivity());
        recyclerView_meus_comercios.setLayoutManager(layoutManagercomercio);
        recyclerView_meus_comercios.setHasFixedSize(true);
        recyclerView_meus_comercios.setAdapter(adapterComercio);

        recyclerView_meus_eventos = view.findViewById(R.id.recycler_meus_eventos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_meus_eventos.setLayoutManager(layoutManager);
        recyclerView_meus_eventos.setHasFixedSize(true);
        recyclerView_meus_eventos.setAdapter(mAdapter);

        return view;

    }


    @Override
    public void onRefresh() {
        RecuperarMeus_Comercio();
        RecuperarMeus_Eventos();
    }

    private void RecuperarMeus_Eventos() {
        //Progress
        linearLayout.setVisibility(View.VISIBLE);
        refresh.setRefreshing(false);
        lista_Meus_Eventos.clear();
        childEventListenerMeus_Eventos = meus_eventosref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Evento evento = dataSnapshot.getValue(Evento.class);
                lista_Meus_Eventos.add(evento);
                if (lista_Meus_Eventos.size() > 0) {
                    nomeevento.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    nomeevento.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                }
                mAdapter.notifyDataSetChanged();

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

    private void RecuperarMeus_Comercio() {
        //Progress

        refresh.setRefreshing(false);
        lista_meus_comercio.clear();
        childEventListenerMeus_Comercio = meus_comercioref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comercio comercio = dataSnapshot.getValue(Comercio.class);
                lista_meus_comercio.add(comercio);
                if (lista_meus_comercio.size() > 0) {
                    nomecomercio.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    nomecomercio.setVisibility(View.GONE);


                }
                adapterComercio.notifyDataSetChanged();

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

    @Override
    public void onStop() {
        super.onStop();
        meus_eventosref.removeEventListener(childEventListenerMeus_Eventos);
        meus_comercioref.removeEventListener(childEventListenerMeus_Comercio);
    }
}