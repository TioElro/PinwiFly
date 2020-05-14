package com.example.pinwifly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Peticion;
import com.example.pinwifly.ViewHolder.PedidoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PedidoStatus extends AppCompatActivity {

    public RecyclerView mRecyclerView;
    public RecyclerView.LayoutManager mLayoutManager;

    FirebaseRecyclerAdapter<Peticion, PedidoViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        pedidos = database.getReference("Pedidos");

        mRecyclerView = (RecyclerView)findViewById(R.id.listPedidos);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        cargarPedidos(Common.currentUser.getPhone());


    }

    private void cargarPedidos(String phone) {
        adapter = new FirebaseRecyclerAdapter<Peticion, PedidoViewHolder>(
                Peticion.class,
                R.layout.pedido_layout,
                PedidoViewHolder.class,
                pedidos.orderByChild("phone")
                    .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(PedidoViewHolder pedidoViewHolder, Peticion peticion, int i) {
                pedidoViewHolder.tvpedidoid.setText(adapter.getRef(i).getKey());
                pedidoViewHolder.tvpedidostatus.setText(convertcode(peticion.getStatus()));
                pedidoViewHolder.tvpedidoaddress.setText(peticion.getAddress());
                pedidoViewHolder.tvpedidophone.setText(peticion.getPhone());
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    private String convertcode(String status) {
        if(status.equals("0"))
            return "Pedido realizado";
        else if(status.equals("1"))
            return "En camino";
        else
            return "Entregado";
    }


}
