package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Database.Database;
import com.example.pinwifly.Helper.RecyclerItemTouchHelper;
import com.example.pinwifly.Interface.RecyclerItemTouchHelperListener;
import com.example.pinwifly.Model.Pedido;
import com.example.pinwifly.Model.Peticion;
import com.example.pinwifly.ViewHolder.CarritoAdapter;
import com.example.pinwifly.ViewHolder.CarritoViewHolder;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteFragment;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import info.hoang8f.widget.FButton;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Carrito extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RecyclerItemTouchHelperListener {

    int carritos = 0;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView tvTotalPrice;
    FButton btnrealizarpedido;

    List<Pedido> carrito=  new ArrayList<>();
    CarritoAdapter adapter;

    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG);
    AutocompleteSupportFragment places_fragment;

    RadioButton selectinmap;
    RadioButton selecthouse;

    Place direccionenvio;

    String direccion, latlng;

    RelativeLayout rootLayout;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;

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

        setContentView(R.layout.activity_carrito);

        //Runtime permission
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },LOCATION_REQUEST_CODE);
            }else{
                if(checkPlayServices()){
                    buildGoogleApiClient();
                    createLocationRequest();
                }
            }

        initPlaces();
        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Pedidos");

        //Init
        mRecyclerView = (RecyclerView)findViewById(R.id.listCarrito);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        //Deslizar para eliminar
            ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(mRecyclerView);

        tvTotalPrice = (TextView)findViewById(R.id.tvtotal);
        btnrealizarpedido = (FButton)findViewById(R.id.botonrealizarpedido);
        cargarListaProductos();

        btnrealizarpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.currentUser.getPhone() == null || Common.currentUser.getPhone().equals("")) {
                    Intent numberphone = new Intent(Carrito.this,VerifyNumber.class);
                    startActivity(numberphone);

                }else{

                    if(carritos == 0){
                        Toast.makeText(Carrito.this, "Tu carrito esta vacio.", Toast.LENGTH_SHORT).show();
                    }else{
                       showAlertDialog();
                    }

                }

            }
        });



    }

    private void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

            mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if(resultCode != ConnectionResult.SUCCESS){
                if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                    GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
                else{
                    Toast.makeText(Carrito.this,"Este dispositivo no es soportado",Toast.LENGTH_SHORT).show();
                    finish();
                }
                return false;
            }
            return true;
    }


    private void initPlaces() {
        Places.initialize(this,getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Carrito.this);

        TextView title =  new TextView(Carrito.this);

        alertDialog.setTitle("Un paso mas!");


        LayoutInflater inflater = this.getLayoutInflater();
        View order_address = inflater.inflate(R.layout.order_address,null);

        selecthouse = (RadioButton)order_address.findViewById(R.id.rbubicacionguardada);
        selectinmap = (RadioButton)order_address.findViewById(R.id.rbthisaddress);

        places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places_fragment.setPlaceFields(placeFields);
        EditText etPlace = (EditText) places_fragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setHint("Escriba una dirección");

        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                direccionenvio = place;
                selecthouse.setChecked(false);
                selectinmap.setChecked(false);
            }

            @Override
            public void onError(@NonNull Status status) {
                //Log.e("Error Frag",status.getStatusMessage());
            }
        });

        selectinmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this,SelectLocation.class);
                startActivity(intent);
            }
        });

        alertDialog.setView(order_address);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!selectinmap.isChecked()){
                    if(direccionenvio != null){
                        //Primero , obtenemos la direccion y comentario
                        direccion = direccionenvio.getAddress().toString();
                    }else{
                        Toast.makeText(Carrito.this, "Porfavor indique una dirección o escoja una opción", Toast.LENGTH_SHORT).show();
                        //Remover fragment
                        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                        return;
                    }
                }

                if(TextUtils.isEmpty(direccion)){
                    Toast.makeText(Carrito.this, "Porfavor indique una dirección o escoja una opción", Toast.LENGTH_SHORT).show();
                    //Remover fragment
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                    return;
                }

                String cantidadformat = tvTotalPrice.getText().toString()
                        .replace("$","")
                        .replace(",","");
                float cantidad = Float.parseFloat(cantidadformat);

                if(selectinmap.isChecked()) {

                    Intent pay = new Intent(Carrito.this,PaymentMethod.class);
                    pay.putExtra("direccion",Common.currentAddress.getDireccion());
                    pay.putExtra("cantidad",cantidad);
                    pay.putExtra("precio",tvTotalPrice.getText().toString());
                    pay.putExtra("latlng",String.format("%s,%s",Common.currentAddress.getLat(),Common.currentAddress.getLng()));
                    startActivity(pay);
                    Common.currentAddress = null;
                    finish();

                }else if(selecthouse.isChecked()){

                }else{

                Intent pay = new Intent(Carrito.this,PaymentMethod.class);
                pay.putExtra("direccion",direccion);
                pay.putExtra("cantidad",cantidad);
                pay.putExtra("precio",tvTotalPrice.getText().toString());
                pay.putExtra("latlng",String.format("%s,%s",direccionenvio.getLatLng().latitude,direccionenvio.getLatLng().longitude));
                startActivity(pay);
                finish();

                }
                /*

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

                 */

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Remover fragment
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentAddress != null){
            places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            places_fragment.setPlaceFields(placeFields);
            EditText etPlace = (EditText) places_fragment.getView().findViewById(R.id.places_autocomplete_search_input);
            etPlace.setText(Common.currentAddress.getDireccion());
            direccion = Common.currentAddress.getDireccion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
            break;
        }
    }

    private void cargarListaProductos() {
        carrito = new Database(this).getCarritos();
        adapter = new CarritoAdapter(carrito,this);
        mRecyclerView.setAdapter(adapter);
        //Calcular precio total
        double total = 0;
        for(Pedido pedido:carrito){
            total+=(Double.parseDouble(pedido.getPrice()))*(Double.parseDouble(pedido.getQuantity()));
            carritos++;
        }
        Locale locale = new Locale("es","MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        tvTotalPrice.setText(fmt.format(total));

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            Log.e("LOCATION","Tu ubicación: "+mLastLocation.getLatitude()+ ", "+mLastLocation.getLongitude());
        }else{
            Log.e("LOCATION","No se pudo obtener tu ubicación");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CarritoViewHolder){
            String name = ((CarritoAdapter)mRecyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            Pedido deleteItem = ((CarritoAdapter)mRecyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            int deleteindex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteindex);
            new Database(getBaseContext()).removeFromCarrito(deleteItem.getProductID());

            int total = 0;
            List<Pedido> pedidos = new Database(getBaseContext()).getCarritos();
            for (Pedido pedido: pedidos)
                total += (Integer.parseInt(pedido.getPrice())) * (Integer.parseInt(pedido.getQuantity()));
            Locale locale = new Locale("es","MX");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            tvTotalPrice.setText(fmt.format(total));

            //Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name + " borrado del carrito", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESHACER", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteindex);
                    new Database(getBaseContext()).addToCarrito(deleteItem);

                    int total = 0;
                    List<Pedido> pedidos = new Database(getBaseContext()).getCarritos();
                    for (Pedido pedido: pedidos)
                        total += (Integer.parseInt(pedido.getPrice())) * (Integer.parseInt(pedido.getQuantity()));
                    Locale locale = new Locale("es","MX");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    tvTotalPrice.setText(fmt.format(total));

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
