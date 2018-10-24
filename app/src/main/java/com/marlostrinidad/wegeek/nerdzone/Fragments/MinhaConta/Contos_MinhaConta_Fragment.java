package com.marlostrinidad.wegeek.nerdzone.Fragments.MinhaConta;


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
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Conto;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contos_MinhaConta_Fragment extends Fragment {

    private DatabaseReference mDatabaseconto;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerConto;
    private Adapter_Conto adapter_conto;
    private ArrayList<Conto> ListaConto = new ArrayList<>();
    private ChildEventListener valueEventListenerConto;
    public Contos_MinhaConta_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contos__perfil_, container, false);
        recyclerConto = view.findViewById(R.id.lista_conto_minha_conta);
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
    @Override
    public void onStart() {
        super.onStart();
        RecuperarConto();

    }

    public void onStop() {
        super.onStop();
        mDatabaseconto.removeEventListener(valueEventListenerConto);
    }




    public void RecuperarConto(){
        ListaConto.clear();
        String identificadoUsuario= UsuarioFirebase.getIdentificadorUsuario();
        valueEventListenerConto=mDatabaseconto.orderByChild("idauthor").equalTo(identificadoUsuario)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Conto conto = dataSnapshot.getValue(Conto.class);
                        ListaConto.add(0,conto);

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