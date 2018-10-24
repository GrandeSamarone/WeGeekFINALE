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
public class AleatorioFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {

    public static final int SELECAO_ICONE = 34;
    private RecyclerView recyclerViewaleatorio;
    private SwipeRefreshLayout swipeicone;
    private DatabaseReference Icons_aleatorio;
    private ArrayList<Icones> ListAleatorio = new ArrayList<>();
    private IconeAdapter adapterAleatorio;
    private ValueEventListener valueEventListenerAleatorio;
    public AleatorioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_aleatorio, container, false);

//ATUALIZA
        swipeicone = view.findViewById(R.id.swipe_fragment_aleatorio);
        swipeicone.setRefreshing(true);
        swipeicone.setOnRefreshListener(AleatorioFragment.this);
        swipeicone.post(new Runnable() {
            @Override
            public void run() {
               RecuperarAleatorio();
            }
        });

    recyclerViewaleatorio = view.findViewById(R.id.mRecylceraleatorio);
    Icons_aleatorio =  ConfiguracaoFirebase.getFirebaseDatabase().child("icones").child("iconesaleatorio");

//Configurar Adapter
        adapterAleatorio = new IconeAdapter(getContext(),ListAleatorio);

        //Configuracao RecycleView
        recyclerViewaleatorio.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewaleatorio.setAdapter(adapterAleatorio);

        recyclerViewaleatorio.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewaleatorio, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0) {
                    if (getActivity().getIntent().hasExtra("minhaconta")) {
                        Intent intent = new Intent( getActivity(), MinhaConta.class);
                        intent.putExtra("caminho_foto", ListAleatorio.get(position).getUrl());
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
        Icons_aleatorio.removeEventListener(valueEventListenerAleatorio);
    }
    @Override
    public void onRefresh() {
     RecuperarAleatorio();
    }

    public void RecuperarAleatorio(){
        ListAleatorio.clear();
       swipeicone.setRefreshing(true);
        valueEventListenerAleatorio = Icons_aleatorio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dados:  dataSnapshot.getChildren()){
                    Icones icone = dados.getValue(Icones.class);
                    ListAleatorio.add(icone);
                }
                adapterAleatorio.notifyDataSetChanged();
                swipeicone.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
