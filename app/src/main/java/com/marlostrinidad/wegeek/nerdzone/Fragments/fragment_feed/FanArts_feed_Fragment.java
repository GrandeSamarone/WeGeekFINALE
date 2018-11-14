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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_FanArt_Feed;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.FanArts.Detalhe_FarArts_Activity;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.Model.Feed_Art;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FanArts_feed_Fragment extends Fragment {

    private DatabaseReference mDatabasefeed,database_Fanarts;
    private LinearLayout nadaencontrado;
    private RecyclerView recyclerArts;
    private Adapter_FanArt_Feed adapter_arts;
    private ArrayList<FanArts> Listafanrts = new ArrayList<>();
    private ChildEventListener valueEventListenerFanarts,valueEventListenerFeed;
    public FanArts_feed_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_fan_arts_feed_, container, false);
        nadaencontrado=view.findViewById(R.id.linear_nada_cadastrado_feed_art);
        nadaencontrado.setVisibility(View.VISIBLE);
        recyclerArts = view.findViewById(R.id.lista_fanarts_feed);
        mDatabasefeed = ConfiguracaoFirebase.getFirebaseDatabase().child("feed-arts");
        database_Fanarts = ConfiguracaoFirebase.getDatabase().getReference().child("arts");
        //Configuracao Adapter
        adapter_arts =new Adapter_FanArt_Feed(Listafanrts,getActivity());
        //Adapter
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerArts.setLayoutManager(layoutManager);
        recyclerArts.setHasFixedSize(true);
        recyclerArts.setAdapter(adapter_arts);

        recyclerArts.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerArts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<FanArts> listFanArtsSelecionada = adapter_arts.getfanarts();

                if (listFanArtsSelecionada.size() > 0) {
                    FanArts fanartselecionada = listFanArtsSelecionada.get(position);
                    Intent it = new Intent(getActivity(), Detalhe_FarArts_Activity.class);
                    it.putExtra("id", fanartselecionada.getId());
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
                        Feed_Art feed_art = dataSnapshot.getValue(Feed_Art.class);

                        RecuperarArt(feed_art.getIdarts());

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
    private void RecuperarArt(final String idart){
        Listafanrts.clear();

        valueEventListenerFanarts = database_Fanarts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FanArts fanArts = dataSnapshot.getValue(FanArts.class);
                    if(idart.equals(fanArts.getId())) {
                        Listafanrts.add(0, fanArts);
                  if(Listafanrts.size()>0){
                      nadaencontrado.setVisibility(View.GONE);
                  }
                        adapter_arts.notifyDataSetChanged();
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
