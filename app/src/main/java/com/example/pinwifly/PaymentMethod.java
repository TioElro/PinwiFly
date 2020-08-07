package com.example.pinwifly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Common.Config;
import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Peticion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PaymentMethod extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    //Paypal metodo de pago
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //SANDBOX TEST , CAMBIR A PRODUCTION
            .clientId(Config.PAYPAL_CLIENT_ID);

    String direccion,precio,latlng;
    Float cantidad;
    Button paypalbtn;
    Button stripe;
    Button efectivobtn;
    List<Pedido> carrito=  new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView preciotv;

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
        setContentView(R.layout.activity_payment_method);

        //Iniciamos servicio Paypal
        Intent intent = new Intent(PaymentMethod.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        direccion = getIntent().getStringExtra("direccion");
        cantidad = getIntent().getFloatExtra("cantidad",0.0f);
        precio = getIntent().getStringExtra("precio");
        latlng = getIntent().getStringExtra("latlng");

        carrito = new Database(this).getCarritos();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Pedidos");

        preciotv = (TextView)findViewById(R.id.precio);
        preciotv.setText("$"+cantidad);

        efectivobtn = (Button)findViewById(R.id.efectivo);
        efectivobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Peticion peticion = new Peticion(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        direccion,
                        precio,
                        "0",
                        "Efectivo",
                        latlng,
                        carrito
                );

                //Enviar a Firebase
                //Usaremos System.CurrentMilli para llave
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(peticion);
                //Borramos carrito
                new Database(getBaseContext()).cleanCarrito();
                Toast.makeText(PaymentMethod.this, "Elegiste pagar en efectivo, pronto estaremos contigo, gracias!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PaymentMethod.this,PedidoStatus.class);
                startActivity(intent);
                finish();

            }
        });

        paypalbtn = (Button)findViewById(R.id.paypal);
        paypalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(cantidad),
                        "MXN",
                        "Pedido de PinwiFly",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent pp = new Intent(getApplicationContext(), PaymentActivity.class);
                pp.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                pp.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(pp,PAYPAL_REQUEST_CODE);

            }
        });

        stripe = (Button)findViewById(R.id.stripe);
        stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethod.this,StripePayment.class);
                intent.putExtra("cantidad",cantidad);
                intent.putExtra("direccion",direccion);
                intent.putExtra("precio",precio);
                intent.putExtra("latlng",latlng);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try{
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);



                Peticion peticion = new Peticion(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        direccion,
                        precio,
                        "0",
                        jsonObject.getJSONObject("response").getString("state"),
                        latlng,
                        carrito
                );

                //Enviar a Firebase
                //Usaremos System.CurrentMilli para llave
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(peticion);
                //Borramos carrito
                new Database(getBaseContext()).cleanCarrito();
                Toast.makeText(PaymentMethod.this, "Gracias, pedido realizado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentMethod.this,PedidoStatus.class);
                startActivity(intent);
                finish();


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(PaymentMethod.this, "Pedido Cancelado", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(PaymentMethod.this, "Pago invalidado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
