package com.example.pinwifly.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.pinwifly.Model.Pedido;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="PenguinDB.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Pedido> getCarritos(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlselect={"ProductName","ProductID","Quantity","Price","Discount"};
        String sqltable="OrderDetail";

        qb.setTables(sqltable);
        Cursor c = qb.query(db,sqlselect,null,null,null,null,null);

        final List<Pedido> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Pedido(c.getString(c.getColumnIndex("ProductID")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                        ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCarrito(Pedido pedido){
        SQLiteDatabase db = getReadableDatabase();
        String query= String.format("INSERT INTO OrderDetail(ProductID,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');",
                pedido.getProductID(),
                pedido.getProductName(),
                pedido.getQuantity(),
                pedido.getPrice(),
                pedido.getDiscount());

        db.execSQL(query);

    }

    public void cleanCarrito(){
        SQLiteDatabase db = getReadableDatabase();
        String query= String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }
}
