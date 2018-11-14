package com.marlostrinidad.wegeek.nerdzone.Fragments.perfil;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Conto;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.Main;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contos_perfil_Fragment extends Fragment implements Main {

    private DatabaseReference mDatabaseconto;
    private LinearLayout nadaencontrado;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerConto;
    private Adapter_Conto adapter_conto;
    private ArrayList<Conto> ListaConto = new ArrayList<>();
    private ChildEventListener valueEventListenerConto;
    public Contos_perfil_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_contos_perfil_, container, false);
        nadaencontrado=view.findViewById(R.id.linear_nada_cadastrado_perfil_conto);
        recyclerConto = view.findViewById(R.id.lista_conto_perfil);
        mDatabaseconto = ConfiguracaoFirebase.getFirebaseDatabase().child("conto");
        //Configuracao Adapter
        adapter_conto =new Adapter_Conto(ListaConto,getActivity());
        //Adapter
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerConto.setLayoutManager(layoutManager);
        recyclerConto.setHasFixedSize(true);
        recyclerConto.setAdapter(adapter_conto);



        return view;

    }


    public void onStop() {
        super.onStop();
        mDatabaseconto.removeEventListener(valueEventListenerConto);
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

        RecuperarConto(account);
        //  Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();

    }

    public void RecuperarConto(String id ){
        ListaConto.clear();
        nadaencontrado.setVisibility(View.VISIBLE);
        valueEventListenerConto=mDatabaseconto.orderByChild("idauthor").equalTo(id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Conto conto = dataSnapshot.getValue(Conto.class);
                        ListaConto.add(0,conto);
                         if(ListaConto.size()>0){
                             nadaencontrado.setVisibility(View.GONE);
                         }
                        adapter_conto.notifyDataSetChanged();

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