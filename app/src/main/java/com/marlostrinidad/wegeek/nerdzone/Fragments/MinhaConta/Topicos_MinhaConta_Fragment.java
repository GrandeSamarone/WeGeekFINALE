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
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Topico;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Topicos_MinhaConta_Fragment extends Fragment  {
    private DatabaseReference mDatabasetopico;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerTopico;
    private Adapter_Topico adapter_topico;
    private ArrayList<Topico> ListaTopico = new ArrayList<>();
    private ChildEventListener valueEventListenerTopico;
    private LinearLayoutManager mManager;
    private Usuario user;


    public Topicos_MinhaConta_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_topicos__perfil_, container, false);

        recyclerTopico = view.findViewById(R.id.lista_topico_minha_conta);
        mDatabasetopico = ConfiguracaoFirebase.getFirebaseDatabase().child("topico");
        //Configuracao Adapter
        adapter_topico =new Adapter_Topico(ListaTopico,getActivity());
        //Adapter
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerTopico.setLayoutManager(layoutManager);
        recyclerTopico.setHasFixedSize(true);
        recyclerTopico.setAdapter(adapter_topico);
    return  view;


    }

    @Override
    public void onStart() {
        super.onStart();
        RecuperarTopico();

    }

    public void onStop() {
        super.onStop();
        mDatabasetopico.removeEventListener(valueEventListenerTopico);
    }






    public void RecuperarTopico(){
        String identificadoUsuario= UsuarioFirebase.getIdentificadorUsuario();
        ListaTopico.clear();
        valueEventListenerTopico=mDatabasetopico.orderByChild("idauthor").equalTo(identificadoUsuario)
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
