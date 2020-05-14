package com.example.pinwifly.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvcarritoname,tvcarritoprice;
    public ImageView img_carrito_count;

    private ItemClickListener itemClickListener;

    public void setTvcarritoname(TextView tvcarritoname) {
        this.tvcarritoname = tvcarritoname;
    }

    public CarritoViewHolder(@NonNull View itemView) {
        super(itemView);
        tvcarritoname = (TextView)itemView.findViewById(R.id.carrito_item_name);
        tvcarritoprice = (TextView)itemView.findViewById(R.id.carrito_item_price);
        img_carrito_count = (ImageView)itemView.findViewById(R.id.carrito_item_count);
    }

    @Override
    public void onClick(View v) {

    }
}

public class CarritoAdapter extends RecyclerView.Adapter<CarritoViewHolder>{

    private List<Pedido>listData = new ArrayList<>();
    private Context context;

    public CarritoAdapter(List<Pedido> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemview = inflater.inflate(R.layout.carrito_layout,parent,false);
        return new CarritoViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.img_carrito_count.setImageDrawable(drawable);
        Locale locale = new Locale("es","MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.tvcarritoprice.setText(fmt.format(price));
        holder.tvcarritoname.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {

        return listData.size();
    }
}
