package com.example.sirope.posaux;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.sirope.posaux.MESSAGE";
    private String server=" ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText tx= (EditText) findViewById(R.id.edit_message);
                server =tx.getText().toString();
                TextView out;
                Log.i("main","server: "+server);
                if ( server.length()>=1) {
                    Log.i("main","printa ngrok");
                    server = getResources().getString(R.string.ngrok) + server;
                    out = (TextView) findViewById(R.id.server_text);
                    out.setText(server);
                }
                else{
                    Log.i("main","vago");
                    server = "indica el server, vago";
                    out= (TextView) findViewById(R.id.server_text);
                    out.setText(server);
                    server="";
                }
            }
        }
        );
    }
    public void startMap(View view){
        if ( server.length()<1){
            server="indica el server, vago";
            TextView out= (TextView) findViewById(R.id.server_text);
            out.setText(server);
            server="";
        }
        else{
            Intent intent = new Intent(this, mapController.class);
            intent.putExtra(EXTRA_MESSAGE, server);
            startActivity(intent);
        }
    }

}
