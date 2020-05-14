package com.example.pinwifly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button botonregistro,botonlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botonregistro = (Button)findViewById(R.id.botonregistro);
        botonlogin = (Button)findViewById(R.id.botoniniciar);

        botonregistro.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent register = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });

        botonlogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });

    }
}
