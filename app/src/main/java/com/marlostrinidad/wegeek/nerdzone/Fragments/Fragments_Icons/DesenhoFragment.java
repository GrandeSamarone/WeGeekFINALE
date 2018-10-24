package com.marlostrinidad.wegeek.nerdzone.Fragments.Fragments_Icons;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.IconeAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Model.Icones;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DesenhoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewdesenho;
    public static final int SELECAO_ICONE = 34;
    private SwipeRefreshLayout swipeiconedesenho;
    private DatabaseReference Icons_desenho;
    private ArrayList<Icones> ListDesenho = new ArrayList<>();
    private IconeAdapter adapterDesenho;
    private ValueEventListener valueEventListenerDesenho;

    public DesenhoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_desenho, container, false);

//ATUALIZA
        swipeiconedesenho = view.findViewById(R.id.swipe_fragment_desenho);
        swipeiconedesenho.setRefreshing(true);
        swipeiconedesenho.setOnRefreshListener(DesenhoFragment.this);
        swipeiconedesenho.post(new Runnable() {
            @Override
            public void run() {
                RecuperarAleatorio();
            }
        });

        recyclerViewdesenho = view.findViewById(R.id.mRecylcerdesenho);
        Icons_desenho =  ConfiguracaoFirebase.getFirebaseDatabase().child("icones").child("iconesdesenho");

//Configurar Adapter
        adapterDesenho = new IconeAdapter(getContext(),ListDesenho);

        //Configuracao RecycleView
        recyclerViewdesenho.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewdesenho.setAdapter(adapterDesenho);

        recyclerViewdesenho.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewdesenho, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0) {
                    if (getActivity().getIntent().hasExtra("minhaconta")) {
                        Intent intent = new Intent( getActivity(), MinhaConta.class);
                        intent.putExtra("caminho_foto", ListDesenho.get(position).getUrl());
                        intent.putExtra("selecaoicone", SELECAO_ICONE);
                        startActivity(intent);
                        getActivity().finish();

                    }
                }
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
    public void onStop() {
        super.onStop();
        Icons_desenho.removeEventListener(valueEventListenerDesenho);
    }
    @Override
    public void onRefresh() {
        RecuperarAleatorio();
    }

    public void RecuperarAleatorio(){
        ListDesenho.clear();
        swipeiconedesenho.setRefreshing(true);
        valueEventListenerDesenho = Icons_desenho.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:  dataSnapshot.getChildren()){
                    Icones icone = dados.getValue(Icones.class);
                    ListDesenho.add(icone);
                }
                adapterDesenho.notifyDataSetChanged();
                swipeiconedesenho.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

