package com.example.foto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageView vfoto ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        vfoto = findViewById(R.id.foto);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        int wi = bitmap.getWidth();
        int hi = bitmap.getHeight();
        System.out.println("Ancho : "+wi+" Alto: "+hi);
      //  Bitmap resizedBitmap = Bitmap.createScaledBitmap(
        //        bitmap, 25, 25, false);

        vfoto.setImageBitmap(bitmap);
    }

    public void GuardaFoto(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);

        //Inicia Establecer Nuevo Pass
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Guardar Foto");


        builder.setMessage("Desea Guardar la Foto ?");

        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage ("Guardando Fotografia ......");
                        pd.show();


                        //    url = getString(R.string.WS_recuperaclave);
                         //   url += "user="+vusuario.getText().toString();
                          //  url += "&newpass="+valueEnc;

                            Toast.makeText(MainActivity.this,"Imagen Guardada Exitosamente.",Toast.LENGTH_SHORT).show();
                            pd.dismiss();

/*
                        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                            @SuppressLint("WrongConstant")
                            @Override
                            public void onResponse(String response) {
                                //      loading.dismiss();

                                try {

                                    JSONObject obj2 = new JSONObject(response);
                                    System.out.println("Codigo ->"+obj2.getString("CodMensaje"));
                                    System.out.println("Mensaje -> "+obj2.getString("Mensaje"));
                                    JSONObject objDatos = null;



                                    pd.dismiss();
                                    int time = 10;

                                    if (obj2.getString("CodMensaje").toString().equals("0")){
                                        Toast.makeText(MainActivity.this,"Clave Actualizada Correctamente!",Toast.LENGTH_SHORT).show();

                                    }else {
                                        Toast.makeText(MainActivity.this,"Ocurrió un Error Msj:"+obj2.getString("Mensaje"),time).show();
                                    }

                                } catch (JSONException e) {
                                    System.out.println("Error parseo -> "+e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this,error.getMessage().toString(), LENGTH_LONG).show();
                                        System.out.println("Error -------------->"+error.getMessage());
                                        pd.dismiss();
                                    }
                                });

                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        requestQueue.add(stringRequest);
*/




                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        //Fin Establecer Nuevo Pass


    }
}
