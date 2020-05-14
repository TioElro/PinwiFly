package com.example.pinwifly;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Peticion;
import com.example.pinwifly.ViewHolder.CarritoAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Carrito extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView tvTotalPrice;
    FButton btnrealizarpedido;

    List<Pedido> carrito=  new ArrayList<>();
    CarritoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Pedidos");

        //Init
        mRecyclerView = (RecyclerView)findViewById(R.id.listCarrito);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tvTotalPrice = (TextView)findViewById(R.id.tvtotal);
        btnrealizarpedido = (FButton)findViewById(R.id.botonrealizarpedido);

        btnrealizarpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        cargarListaProductos();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Carrito.this);
        alertDialog.setTitle("Un paso mas!");
        alertDialog.setMessage("¿A qué direccion quieres que llegue tu pedido?");

        final EditText etaddress = new EditText(Carrito.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        etaddress.setLayoutParams(lp);
        alertDialog.setView(etaddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Peticion peticion = new Peticion(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        etaddress.getText().toString(),
                        tvTotalPrice.getText().toString(),
                        carrito
                );

                //Enviar a Firebase
                //Usaremos System.CurrentMilli para llave
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(peticion);
                //Borramos carrito
                new Database(getBaseContext()).cleanCarrito();
                Toast.makeText(Carrito.this, "Gracias, pedido realizado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void cargarListaProductos() {
        carrito = new Database(this).getCarritos();
        adapter = new CarritoAdapter(carrito,this);
        mRecyclerView.setAdapter(adapter);

        //Calcular precio total
        double total = 0;
        for(Pedido pedido:carrito)
            total+=(Double.parseDouble(pedido.getPrice()))*(Double.parseDouble(pedido.getQuantity()));
        Locale locale = new Locale("es","MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        tvTotalPrice.setText(fmt.format(total));

    }
}
