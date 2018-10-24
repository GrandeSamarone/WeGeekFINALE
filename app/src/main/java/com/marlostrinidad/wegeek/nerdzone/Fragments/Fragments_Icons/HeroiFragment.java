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
public class HeroiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int SELECAO_ICONE = 34;
    private RecyclerView recyclerViewheroi;
    private SwipeRefreshLayout swipeiconeheroi;
    private DatabaseReference Icons_heroi;
    private ArrayList<Icones> Listheroi = new ArrayList<>();
    private IconeAdapter adapterheroi;
    private String item;
    private ValueEventListener valueEventListenerheroi;

    public HeroiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heroi, container, false);

//ATUALIZA
        swipeiconeheroi = view.findViewById(R.id.swipe_fragment_heroi);
        swipeiconeheroi.setRefreshing(true);
        swipeiconeheroi.setOnRefreshListener(HeroiFragment.this);
        swipeiconeheroi.post(new Runnable() {
            @Override
            public void run() {
                RecuperarAleatorio();
            }
        });

        recyclerViewheroi = view.findViewById(R.id.mRecylcerheroi);
        Icons_heroi =  ConfiguracaoFirebase.getFirebaseDatabase().child("icones").child("iconesheroi");

           //Configurar Adapter
        adapterheroi = new IconeAdapter(getContext(),Listheroi);

        //Configuracao RecycleView
        recyclerViewheroi.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewheroi.setAdapter(adapterheroi);

        recyclerViewheroi.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewheroi, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0) {
                    if (getActivity().getIntent().hasExtra("minhaconta")) {
                        Intent intent = new Intent( getActivity(), MinhaConta.class);
                        intent.putExtra("caminho_foto", Listheroi.get(position).getUrl());
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
        Icons_heroi.removeEventListener(valueEventListenerheroi);
    }
    @Override
    public void onRefresh() {
        RecuperarAleatorio();
    }

    public void RecuperarAleatorio(){
        Listheroi.clear();
        swipeiconeheroi.setRefreshing(true);
        valueEventListenerheroi = Icons_heroi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:  dataSnapshot.getChildren()){
                    Icones icone = dados.getValue(Icones.class);
                    Listheroi.add(icone);
                }
                adapterheroi.notifyDataSetChanged();
                swipeiconeheroi.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
