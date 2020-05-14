package com.example.pinwifly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Interface.ItemClickListener;
import com.example.pinwifly.Model.Categoria;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseDatabase database;
    DatabaseReference categoria;

    TextView tvnombrecompleto;
    FirebaseRecyclerAdapter<Categoria, MenuViewHolder> adapter;
    RecyclerView recyclermenu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Iniciamos firebase
        database = FirebaseDatabase.getInstance();
        categoria = database.getReference("Categoria");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent carrito = new Intent(Home.this,Carrito.class);
                startActivity(carrito);
            }
        });
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_compra, R.id.nav_compra)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        break;
                    case R.id.nav_compra:
                        Intent carrito = new Intent(Home.this,Carrito.class);
                        startActivity(carrito);
                        break;
                    case R.id.nav_pedido:
                        Intent pedido = new Intent(Home.this, PedidoStatus.class);
                        startActivity(pedido);
                        break;
                    case R.id.nav_salir:
                        Intent salir = new Intent(Home.this,LoginActivity.class);
                        salir.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(salir);
                        break;

                }
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();

                return false;
            }
        });

        //Colocamos el nombre completo del usuario ON
        View headerView = navigationView.getHeaderView(0);
        tvnombrecompleto = (TextView)headerView.findViewById(R.id.tvfullname);
        tvnombrecompleto.setText(Common.currentUser.getName());

        //Cargamos categorias
        recyclermenu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyclermenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclermenu.setLayoutManager(layoutManager);

        cargarmenu();




    }

    private void cargarmenu() {
        adapter = new FirebaseRecyclerAdapter<Categoria, MenuViewHolder>(Categoria.class, R.layout.category_item, MenuViewHolder.class, categoria) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Categoria categoria, int i) {
                menuViewHolder.txtcategoriaName.setText(categoria.getName());
                Picasso.with(getBaseContext()).load(categoria.getImage())
                        .into(menuViewHolder.imageView);
                final Categoria clickItem = categoria;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Obtenemos ID de la categoria y mandamos a ProductsList
                        Intent productos = new Intent(Home.this,ProductsList.class);
                        productos.putExtra("CategoriaID",adapter.getRef(position).getKey());
                        startActivity(productos);
                    }
                });
            }
        };
        recyclermenu.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
