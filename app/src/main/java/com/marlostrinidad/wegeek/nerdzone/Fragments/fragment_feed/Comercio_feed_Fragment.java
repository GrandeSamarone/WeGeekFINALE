package com.marlostrinidad.wegeek.nerdzone.Fragments.fragment_feed;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Comercio_Feed;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Detalhe_Mercado;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Feed_Comercio;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comercio_feed_Fragment extends Fragment {

    private DatabaseReference mDatabasefeed,database_comercio;
    private LinearLayout nadaencontrado;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerMercado;
    private Adapter_Comercio_Feed adapter_mercado;
    private ArrayList<Comercio> ListaMercado = new ArrayList<>();
    private ChildEventListener valueEventListenerComercio,valueEventListenerFeed;
    public Comercio_feed_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          View view =inflater.inflate(R.layout.fragment_comercio_feed_, container, false);

          nadaencontrado = view.findViewById(R.id.linear_nada_cadastrado_feed_mercado);
        nadaencontrado.setVisibility(View.VISIBLE);
        recyclerMercado = view.findViewById(R.id.lista_comercio_feed);
        mDatabasefeed = ConfiguracaoFirebase.getFirebaseDatabase().child("feed-comercio");
        database_comercio = ConfiguracaoFirebase.getDatabase().getReference().child("comercio");
        //Configuracao Adapter
        adapter_mercado =new Adapter_Comercio_Feed(ListaMercado,getActivity());
        //Adapter
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerMercado.setLayoutManager(layoutManager);
        recyclerMercado.setHasFixedSize(true);
        recyclerMercado.setAdapter(adapter_mercado);

        recyclerMercado.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerMercado, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Comercio> listComercioAtualizado = adapter_mercado.getmercados();

                if (listComercioAtualizado.size() > 0) {
                    Comercio mercadoselecionado = listComercioAtualizado.get(position);
                    Intent it = new Intent(getActivity(), Detalhe_Mercado.class);
                    it.putExtra("mercadoelecionado", mercadoselecionado);
                    startActivity(it);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
        RecuperarFeed();
    return view;
    }
    public void RecuperarFeed(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        valueEventListenerFeed=mDatabasefeed.child(identificadorUsuario)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Feed_Comercio feed_comercio = dataSnapshot.getValue(Feed_Comercio.class);
                         String tt = feed_comercio.getIdmercado();
                        RecuperarComercio(feed_comercio.getIdmercado());

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
    private void RecuperarComercio(final String idcomercio){
        ListaMercado.clear();

        valueEventListenerComercio = database_comercio.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot categorias:dataSnapshot.getChildren()) {
                    for (DataSnapshot mercados : categorias.getChildren()) {
                        Comercio comercio = mercados.getValue(Comercio.class);
                        if (idcomercio.equals(comercio.getIdMercado())) {


                            ListaMercado.add(0, comercio);
                            if(ListaMercado.size()>0){
                                nadaencontrado.setVisibility(View.GONE);
                            }
                            adapter_mercado.notifyDataSetChanged();
                        }
                    }
                }

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
