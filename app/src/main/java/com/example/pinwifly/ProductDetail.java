package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Producto;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetail extends AppCompatActivity {

    TextView product_name,product_price,product_description;
    ImageView product_image;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    FloatingActionButton btncarrito;
    ElegantNumberButton mNumberButton;
    Producto productoactual;
    String ProductoId="";

    FirebaseDatabase database;
    DatabaseReference detalles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        detalles = database.getReference("Producto");

        //Iniciamos vista
        mNumberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btncarrito = (FloatingActionButton)findViewById(R.id.btncarrito);

        btncarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCarrito(new Pedido(
                        ProductoId,
                        productoactual.getName(),
                        mNumberButton.getNumber(),
                        productoactual.getPrice(),
                        productoactual.getDiscount()
                ));
                Toast.makeText(ProductDetail.this,"Añadido al Carrito",Toast.LENGTH_SHORT).show();
            }
        });

        product_description= (TextView)findViewById(R.id.product_description);
        product_name= (TextView)findViewById(R.id.product_name);
        product_price= (TextView)findViewById(R.id.product_price);

        product_image=(ImageView)findViewById(R.id.img_product);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Obtenemos el ProductID del Intent
        if(getIntent() !=null)
            ProductoId = getIntent().getStringExtra("ProductID");
        if(!ProductoId.isEmpty()){
            obtenerdetalles(ProductoId);
        }
    }

    private void obtenerdetalles(final String productoId) {
        detalles.child(productoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productoactual = dataSnapshot.getValue(Producto.class);
                //Colocamos imagen
                Picasso.with(getBaseContext()).load(productoactual.getImage())
                        .into(product_image);
                mCollapsingToolbarLayout.setTitle(productoactual.getName());
                product_price.setText(productoactual.getPrice());
                product_name.setText(productoactual.getName());
                product_description.setText((productoactual.getDescription()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
