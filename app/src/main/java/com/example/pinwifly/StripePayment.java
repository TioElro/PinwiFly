package com.example.pinwifly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Peticion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import info.hoang8f.widget.FButton;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StripePayment extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "https://stripe-pagos-backend.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private Float cantidad;
    String direccion,precio,latlng;
    FButton payButton;
    List<Pedido> carrito=  new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference requests;

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
        setContentView(R.layout.activity_stripe_payment);

        payButton = findViewById(R.id.payButton);
        carrito = new Database(this).getCarritos();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Pedidos");
        cantidad = getIntent().getFloatExtra("cantidad",0.0f);
        direccion = getIntent().getStringExtra("direccion");
        precio = getIntent().getStringExtra("precio");
        latlng = getIntent().getStringExtra("latlng");
        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51H53BmAuveH9mf4gwpSInUnLnc8ojeGqLyMT3ve59La03S5i5BliU7LVJ5tQ3IOrT3kRQXgAwxgJKhEqz5em6Rhi00KE8fRrkc")
        );
        startCheckout();
    }

    private void startCheckout() {
        double amount=cantidad.floatValue()*100;;
        Map<String,Object> payMap=new HashMap<>();
        Map<String,Object> itemMap=new HashMap<>();
        List<Map<String,Object>> itemList =new ArrayList<>();
        payMap.put("currency","mxn");
        itemMap.put("id","photo_subscription");
        itemMap.put("amount",amount);
        itemList.add(itemMap);
        payMap.put("items",itemList);
        String json = new Gson().toJson(payMap);
        Log.i("TAG", "startCheckout: "+json);

        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        /*String json = "{"
                + "\"currency\":\"mxn\","
                + "\"items\":["
                + "{\"id\":\"photo_subscription\"}"
                + "]"
                + "}";*/



        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));
        // Hook up the pay button to the card widget and stripe instance

        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<StripePayment> activityRef;
        PayCallback(@NonNull StripePayment activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final StripePayment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final StripePayment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<StripePayment> activityRef;
        PaymentResultCallback(@NonNull StripePayment activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final StripePayment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                /*activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent)
                );*/
                Toast.makeText(activity,"Pago realizado, gracias!",Toast.LENGTH_SHORT).show();

                Peticion peticion = new Peticion(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        activity.direccion,
                        activity.precio,
                        "0",
                        "Tarjeta",
                        activity.latlng,
                        activity.carrito
                );

                //Enviar a Firebase
                //Usaremos System.CurrentMilli para llave
                activity.requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(peticion);
                //Borramos carrito
                new Database(activity).cleanCarrito();
                Intent intent = new Intent(activity,PedidoStatus.class);
                activity.startActivity(intent);
                activity.finish();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }


        @Override
        public void onError(@NonNull Exception e) {
            final StripePayment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }

}