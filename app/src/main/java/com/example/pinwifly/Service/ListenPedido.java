package com.example.pinwifly.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.Peticion;
import com.example.pinwifly.PedidoStatus;
import com.example.pinwifly.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenPedido extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference peticion;

    public ListenPedido() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        peticion = db.getReference("Pedidos");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        peticion.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //Trigger aqui
        Peticion peticion = dataSnapshot.getValue(Peticion.class);
        mostrarnotificacion(dataSnapshot.getKey(),peticion);
    }

    private void mostrarnotificacion(String key, Peticion peticion) {
        Intent intent = new Intent(getBaseContext(), PedidoStatus.class);
        intent.putExtra("userPhone",peticion.getPhone());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("PinwiFly")
                .setContentInfo("Tu pedido fue actualizado")
                .setContentText("Pedido #"+key+" fue actualizado al estado: "+ Common.convertirCodigoStatus(peticion.getStatus()))
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
