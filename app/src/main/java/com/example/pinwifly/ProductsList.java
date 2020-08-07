package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.Model.Producto;
import com.example.pinwifly.ViewHolder.MenuViewHolder;
import com.example.pinwifly.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProductsList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference productoslist;

    String categoria;

    FirebaseRecyclerAdapter<Producto, ProductViewHolder> adapter;

    //Funcion busqueda
    FirebaseRecyclerAdapter<Producto,ProductViewHolder> adapterBusqueda;
    List<String> sugerirLista = new ArrayList<>();
    MaterialSearchBar mMaterialSearchBar;

    //Favoritos
    Database localDB;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/fuente.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_products_list);
        //Iniciamos firebase
        database = FirebaseDatabase.getInstance();
        productoslist = database.getReference("Producto");

        //Local DB favoritos
        localDB = new Database(this);

        //Cargamos productos
        recyclerView = (RecyclerView)findViewById(R.id.recycler_products);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Obtenemos Intent Home
        if(getIntent() != null)
            categoria = getIntent().getStringExtra("CategoriaID");
        if(!categoria.isEmpty() && categoria!=null) {
            cargarproductos(categoria);
        }

        //Busqueda
        mMaterialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        mMaterialSearchBar.setHint("¿Qué producto buscas?");
        // mMaterialSearchBar.setSpeechMode(false); no se requiere ya esta definido en XML
        cargarsugerencia();
        mMaterialSearchBar.setLastSuggestions(sugerirLista);
        mMaterialSearchBar.setCardViewElevation(10);
        mMaterialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Cuando el cliente coloca su texto, se cambiara la lista sugerida

                List<String> sugerido = new ArrayList<>();
                for(String search:sugerirLista){
                    if(search.toLowerCase().contains(mMaterialSearchBar.getText().toLowerCase()))
                        sugerido.add(search);
                }
                mMaterialSearchBar.setLastSuggestions(sugerido);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mMaterialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Cuando la barra de busqueda cierra
                //Se restaura el adaptador
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Cuando la busqueda termine
                //Muestra los resultados en el adaptador
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        adapterBusqueda = new FirebaseRecyclerAdapter<Producto, ProductViewHolder>(
                Producto.class,
                R.layout.product_item,
                ProductViewHolder.class,
                productoslist.orderByChild("Name").equalTo(text.toString())
        ) {

            @Override
            protected void populateViewHolder(ProductViewHolder productViewHolder, Producto producto, int i) {
                productViewHolder.txtproductName.setText(producto.getName());
                Picasso.with(getBaseContext()).load(producto.getImage())
                        .into(productViewHolder.producto_image);

                final Producto local = producto;
                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciamos nueva Activity Product Detail
                        Intent detail = new Intent(ProductsList.this,ProductDetail.class);
                        detail.putExtra("ProductID",adapterBusqueda.getRef(position).getKey());
                        startActivity(detail);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapterBusqueda); //Colocamos adapter para el RecyclerView es el resultado de la busqueda
    }

    private void cargarsugerencia() {
        productoslist.orderByChild("Categoria").equalTo(categoria)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Producto item = postSnapshot.getValue(Producto.class);
                            sugerirLista.add(item.getName()); //Agregar el nombre del producto sugerido

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void cargarproductos(String categoria) {
        adapter = new FirebaseRecyclerAdapter<Producto, ProductViewHolder>(Producto.class,
                R.layout.product_item,
                ProductViewHolder.class,
                productoslist.orderByChild("categoria").equalTo(categoria)
        ) {

            @Override
            protected void populateViewHolder(ProductViewHolder productViewHolder, Producto producto, int i) {
                productViewHolder.txtproductName.setText(producto.getName());
                productViewHolder.txtproductPrice.setText(String.format("Precio: $ %s",producto.getPrice().toString()));
                Picasso.with(getBaseContext()).load(producto.getImage())
                        .into(productViewHolder.producto_image);

                //Añadir favoritos
                if(localDB.isFavorite(adapter.getRef(i).getKey())){
                    productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //Click Favorito
                productViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(i).getKey())){
                            localDB.addToFavorites(adapter.getRef(i).getKey());
                            productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductsList.this,""+producto.getName()+ "ha sido añadido a favoritos",Toast.LENGTH_SHORT).show();
                        }else{
                            localDB.removeToFavorites(adapter.getRef(i).getKey());
                            productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }
                    }
                });

                final Producto local = producto;
                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciamos nueva Activity Product Detail
                        Intent detail = new Intent(ProductsList.this,ProductDetail.class);
                        detail.putExtra("ProductID",adapter.getRef(position).getKey());
                        startActivity(detail);
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

}
