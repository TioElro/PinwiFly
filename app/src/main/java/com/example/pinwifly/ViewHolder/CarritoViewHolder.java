package com.example.pinwifly.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.R;

public class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvcarritoname,tvcarritoprice;
    public ImageView img_carrito_count;

    public RelativeLayout view_back;
    public LinearLayout view_front;

    private ItemClickListener itemClickListener;

    public void setTvcarritoname(TextView tvcarritoname) {
        this.tvcarritoname = tvcarritoname;
    }

    public CarritoViewHolder(@NonNull View itemView) {
        super(itemView);
        tvcarritoname = (TextView)itemView.findViewById(R.id.carrito_item_name);
        tvcarritoprice = (TextView)itemView.findViewById(R.id.carrito_item_price);
        img_carrito_count = (ImageView)itemView.findViewById(R.id.carrito_item_count);

        view_back = (RelativeLayout)itemView.findViewById(R.id.view_back);
        view_front = (LinearLayout)itemView.findViewById(R.id.view_front);
    }

    @Override
    public void onClick(View v) {

    }
}