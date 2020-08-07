package com.example.pinwifly.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.R;

public class PedidoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvpedidoid,tvpedidostatus,tvpedidophone,tvpedidoaddress;
    private ItemClickListener mItemClickListener;

    public PedidoViewHolder(@NonNull View itemView) {
        super(itemView);

        tvpedidoid = (TextView)itemView.findViewById(R.id.pedido_id);
        tvpedidostatus = (TextView)itemView.findViewById(R.id.pedido_status);
        tvpedidophone = (TextView)itemView.findViewById(R.id.pedido_phone);
        tvpedidoaddress = (TextView)itemView.findViewById(R.id.pedido_address);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
       // mItemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
