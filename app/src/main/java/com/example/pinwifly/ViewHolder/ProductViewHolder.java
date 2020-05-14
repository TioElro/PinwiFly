package com.example.pinwifly.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductName;
    public ImageView producto_image;

    private ItemClickListener itemClickListener;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtproductName = (TextView)itemView.findViewById(R.id.product_name);
        producto_image = (ImageView)itemView.findViewById(R.id.product_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
