package com.marlostrinidad.wegeek.nerdzone.Fragments.perfil;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Topico;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.Main;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Topico_Perfil_Fragment extends Fragment  implements Main {

    private DatabaseReference mDatabasetopico;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerTopico;
    private Adapter_Topico adapter_topico;
    private ArrayList<Topico> ListaTopico = new ArrayList<>();
    private ChildEventListener valueEventListenerTopico;
    private LinearLayoutManager mManager;
    private Usuario user;
    public Topico_Perfil_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_topico__perfil_, container, false);

        recyclerTopico = view.findViewById(R.id.lista_topico_perfil);
        mDatabasetopico = ConfiguracaoFirebase.getFirebaseDatabase().child("topico");
        //Configuracao Adapter
        adapter_topico =new Adapter_Topico(ListaTopico,getActivity());
        //Adapter
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerTopico.setLayoutManager(layoutManager);
        recyclerTopico.setHasFixedSize(true);
        recyclerTopico.setAdapter(adapter_topico);
       return view;
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    public void onStop() {
        super.onStop();
        mDatabasetopico.removeEventListener(valueEventListenerTopico);
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

        RecuperarTopico(account);
        //  Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();

    }

    public void RecuperarTopico(String id){
        ListaTopico.clear();
        valueEventListenerTopico=mDatabasetopico.orderByChild("idauthor").equalTo(id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Topico topico = dataSnapshot.getValue(Topico.class);
                        ListaTopico.add(0,topico);

                        adapter_topico.notifyDataSetChanged();

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
