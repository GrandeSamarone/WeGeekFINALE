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
public class PretoeBrancoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int SELECAO_ICONE = 34;
    private RecyclerView recyclerViewPreto_e_branco;
    private SwipeRefreshLayout swipeiconerecyclerViewPreto_e_branco;
    private DatabaseReference Icons_recyclerViewPreto_e_branco;
    private ArrayList<Icones> ListrecyclerViewPreto_e_branco = new ArrayList<>();
    private IconeAdapter adapterrecyclerViewPreto_e_branco;
    private ValueEventListener valueEventListenerrecyclerViewPreto_e_branco;

    public PretoeBrancoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pretoe_branco, container, false);

//ATUALIZA
        swipeiconerecyclerViewPreto_e_branco = view.findViewById(R.id.swipe_fragment_pretoebranco);
        swipeiconerecyclerViewPreto_e_branco.setRefreshing(true);
        swipeiconerecyclerViewPreto_e_branco.setOnRefreshListener(PretoeBrancoFragment.this);
        swipeiconerecyclerViewPreto_e_branco.post(new Runnable() {
            @Override
            public void run() {
                RecuperarAleatorio();
            }
        });

        recyclerViewPreto_e_branco = view.findViewById(R.id.mRecylcerpretoebranco);
        Icons_recyclerViewPreto_e_branco =  ConfiguracaoFirebase.getFirebaseDatabase().child("icones").child("iconespretoebranco");

        //Configurar Adapter
        adapterrecyclerViewPreto_e_branco = new IconeAdapter(getContext(),ListrecyclerViewPreto_e_branco);

        //Configuracao RecycleView
        recyclerViewPreto_e_branco.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewPreto_e_branco.setAdapter(adapterrecyclerViewPreto_e_branco);


        recyclerViewPreto_e_branco.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewPreto_e_branco, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0) {
                    if (getActivity().getIntent().hasExtra("minhaconta")) {
                        Intent intent = new Intent( getActivity(), MinhaConta.class);
                        intent.putExtra("caminho_foto", ListrecyclerViewPreto_e_branco.get(position).getUrl());
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
        Icons_recyclerViewPreto_e_branco.removeEventListener(valueEventListenerrecyclerViewPreto_e_branco);
    }
    @Override
    public void onRefresh() {
        RecuperarAleatorio();
    }

    public void RecuperarAleatorio(){
        ListrecyclerViewPreto_e_branco.clear();
        swipeiconerecyclerViewPreto_e_branco.setRefreshing(true);
        valueEventListenerrecyclerViewPreto_e_branco = Icons_recyclerViewPreto_e_branco.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:  dataSnapshot.getChildren()){
                    Icones icone = dados.getValue(Icones.class);
                    ListrecyclerViewPreto_e_branco.add(icone);
                }
                adapterrecyclerViewPreto_e_branco.notifyDataSetChanged();
                swipeiconerecyclerViewPreto_e_branco.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

