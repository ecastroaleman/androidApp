package com.example.testingmenudrawer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Detalles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);


        Bundle mibundle = this.getIntent().getExtras();

        Intent inte  = getIntent();
        TextView tv = (TextView) findViewById(R.id.textView2);
       tv.setText(mibundle.getString("value").toString());

      //  tv.setText("Hola");
    }
}
