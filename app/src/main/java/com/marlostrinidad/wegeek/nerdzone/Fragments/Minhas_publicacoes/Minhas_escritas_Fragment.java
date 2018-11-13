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
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes.Adapter_Meus_Topicos;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes.Adapter_meus_Contos;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Minhas_escritas_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView_meus_contos, recyclerView_meus_topico;
    private DatabaseReference database, databaseusuario;
    private Query meus_topicosref, meus_contos_ref;
    private String identificadoUsuario;
    private TextView nomeconto, nometopico, errobusca;
    private Evento evento;
    private List<Topico> lista_Meus_Topicos = new ArrayList<>();
    private List<Conto> lista_meus_contos = new ArrayList<>();
    private Adapter_Meus_Topicos mAdapter;
    private Adapter_meus_Contos adapterConto;
    private ChildEventListener childEventListenerMeus_Topico, childEventListenerMeus_Conto;
    private CircleImageView icone;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private SwipeRefreshLayout refresh;
    private LinearLayout linearLayout;

    public Minhas_escritas_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_minhas_escritas_, container, false);

//Configurações Originais
        linearLayout = view.findViewById(R.id.linear_nada_cadastrado_conto_topico);
        database = FirebaseDatabase.getInstance().getReference();
        nomeconto = view.findViewById(R.id.contotexto);
        nometopico = view.findViewById(R.id.topicotexto);
        databaseusuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        evento = new Evento();
        refresh = view.findViewById(R.id.atualizarminhas_contos_topicos_refresh);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                RecuperarMeus_Topicos();
                RecuperarMeus_Conto();
            }
        });
        refresh.setColorSchemeResources
                (R.color.colorPrimaryDark, R.color.amareloclaro,
                        R.color.accent);
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        meus_topicosref = database.child("meustopicos").child(identificadorUsuario);
        meus_contos_ref = database.child("meusconto").child(identificadorUsuario);

        mAdapter = new Adapter_Meus_Topicos(lista_Meus_Topicos, getActivity());
        adapterConto = new Adapter_meus_Contos(lista_meus_contos, getActivity());

        recyclerView_meus_contos = view.findViewById(R.id.recycler_meus_contos);
        RecyclerView.LayoutManager layoutManagercomercio = new LinearLayoutManager(getActivity());
        recyclerView_meus_contos.setLayoutManager(layoutManagercomercio);
        recyclerView_meus_contos.setHasFixedSize(true);
        recyclerView_meus_contos.setAdapter(adapterConto);

        recyclerView_meus_topico = view.findViewById(R.id.recycler_meus_topicos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_meus_topico.setLayoutManager(layoutManager);
        recyclerView_meus_topico.setHasFixedSize(true);
        recyclerView_meus_topico.setAdapter(mAdapter);


    return view;
    }

    @Override
    public void onRefresh() {
RecuperarMeus_Conto();
RecuperarMeus_Topicos();
    }


    private void RecuperarMeus_Topicos() {
        //Progress
        linearLayout.setVisibility(View.VISIBLE);

        lista_Meus_Topicos.clear();
        childEventListenerMeus_Topico = meus_topicosref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Topico topico = dataSnapshot.getValue(Topico.class);
                lista_Meus_Topicos.add(topico);
                if (lista_Meus_Topicos.size() > 0) {
                    nometopico.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    nometopico.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                }
                mAdapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
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

    private void RecuperarMeus_Conto() {
        lista_meus_contos.clear();
        childEventListenerMeus_Conto = meus_contos_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conto conto = dataSnapshot.getValue(Conto.class);
                lista_meus_contos.add(conto);
                if (lista_meus_contos.size() > 0) {
                    nomeconto.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    nomeconto.setVisibility(View.GONE);


                }
                adapterConto.notifyDataSetChanged();
                refresh.setRefreshing(false);
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
        meus_topicosref.removeEventListener(childEventListenerMeus_Topico);
        meus_contos_ref.removeEventListener(childEventListenerMeus_Conto);
    }
}