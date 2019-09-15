package com.example.consumojson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Text;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



public class MainActivity extends AppCompatActivity {


    private RequestQueue queue;
    private TextView vusuario,vclave ;
    private Text usr;
    private ImageView img;
    private Button btn;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vusuario =  findViewById(R.id.usuario);
        vclave =  findViewById(R.id.clave);
        btn =  findViewById(R.id.ingresar);
        queue = Volley.newRequestQueue(this);
       final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage ("Cargando .. . . ..  espere");
        //pd.show();
        //obtenerDatosVolley(pd);


        vusuario.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    vusuario.setText("");
                    vusuario.setBackgroundColor(1010525);
                }else if (vusuario.length() == 0) {
                     vusuario.setText("Ingrese Usuario");
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ingresarSistema(v);
            }
        });
    }

    public String EncriptaJS(String pcad){
        System.out.println("Cadena a encriptar"+pcad);
        Object[] params = new Object[] {pcad.trim()};
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        String result = "";
        try {
            Scriptable scope = rhino.initStandardObjects();

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
           // String javaScriptCode ="function sumar(numero1, numero2) {return numero1 + numero2;}";
            //rhino.evaluateString(scope, javaScriptCode, "JavaScript", 1, null);

          //  Reader in = new FileReader("sha512.js");
            InputStream is  = getAssets().open("sha512.js");

            Reader in = new InputStreamReader(is);
            rhino.evaluateReader(scope,in,"JavaScript",1,null);

            // Get the functionName defined in JavaScriptCode
            Object obj = scope.get("hex_sha512", scope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                // Call the function with params
                Object jsResult = jsFunction.call(rhino, scope, scope, params);
                // Parse the jsResult object to a String
                result = Context.toString(jsResult);
                System.out.println("Resultado ----------->"+result);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }

        return result;
    }

    public void ingresarSistema(final View view){

        if (vusuario.length() > 0 && vclave.length() > 0){
            if (vusuario.length() > 0) {
                if (vclave.length() > 0){
                    final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage ("Validando Usuario y Password ......");
                    pd.show();
                    String PassEnc = EncriptaJS(vclave.getText().toString());
                    String url = "http://192.168.1.108/servicio/Credenciales.php?";
                       url = "http://sanlazaro.milenium.tech/wsrest?";
                        url += "user="+vusuario.getText().toString();
                        url += "&pass="+PassEnc;



                    StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(String response) {
                            //      loading.dismiss();

                            try {

                                JSONObject obj2 = new JSONObject(response);
                                System.out.println("Codigo ->"+obj2.getString("CodMensaje"));
                                System.out.println("Mensaje -> "+obj2.getString("Mensaje"));

                                pd.dismiss();
                                int time = 10;

                                if (obj2.getString("CodMensaje").toString().equals("0")){
                                    Toast.makeText(MainActivity.this,"Bienvenido al Sistema",time).show();
                                   Intent intent;
                                    intent = new Intent(MainActivity.this,Inicio.class);
                                    startActivity(intent);

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
                                    Toast.makeText(MainActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                                    System.out.println("Error -------------->"+error.getMessage());
                                    pd.dismiss();
                                }
                            });

                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);

                }else {
                    Toast.makeText(MainActivity.this,"Debe Ingresar Clave",Toast.LENGTH_LONG).show();
                    vclave.getFocusable();

                }



            }else {
                Toast.makeText(MainActivity.this,"Debe Ingresar Usuario",Toast.LENGTH_LONG).show();
                vusuario.getFocusable();

            }


        }else {
            Toast.makeText(MainActivity.this,"Debe Ingresar Usuario y Clave",Toast.LENGTH_LONG).show();
            vusuario.getFocusable();
        }

    }


        public void obtenerDatosVolley(final ProgressDialog pd) {
            String url = "https://api.androidhive.info/contacts/";
            url = "http://192.168.1.108/servicio/Numero.php?numeros=1";

            //final ProgressDialog loading = ProgressDialog.show(this, "Please wait...", "Fetching...", false, false);


            StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //      loading.dismiss();
                    System.out.println("Respuesta -------------->"+response);
                    vusuario.setText(response.toString());

                    try {

                        JSONObject obj2 = new JSONObject(response);
                        System.out.println("Codigo -> "+obj2.getString("CodMensaje"));
                        System.out.println("Mensaje -> "+obj2.getString("Mensaje"));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                pd.dismiss();
                            }
                        }, 4000L);

                    } catch (JSONException e) {
                        System.out.println("Error parseo -> "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                            System.out.println("Error -------------->"+error.getMessage());
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);



        }


    public void obtenerDatosVolley2() {
        String url = "https://api.androidhive.info/contacts/";
        url = "http://192.168.1.108/servicio/Numero.php?numero=1";

        //final ProgressDialog loading = ProgressDialog.show(this, "Please wait...", "Fetching...", false, false);


        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
          //      loading.dismiss();
                System.out.println("Respuesta -------------->"+response);
               vusuario.setText(response.toString());

                try {
                    JSONArray obj = new JSONArray(response);
                    System.out.println("Todo bien, todo correcto"+obj.getString(0));

                    JSONObject obj2 = new JSONObject(obj.getString(0));
                    System.out.println("Codigo -> "+obj2.getString("CodMensaje"));
                    System.out.println("Mensaje -> "+obj2.getString("Mensaje"));

                } catch (JSONException e) {
                    System.out.println("Error parseo -> "+e.getMessage());
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                        System.out.println("Error -------------->"+error.getMessage());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

}
        /*
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    int total = response.length();
                    Toast.makeText(MainActivity.this, "Total ->"+total, Toast.LENGTH_LONG).show();
                    System.out.println("Total -------------------->"+total);
                    Object dato = response.getString("CodMensaje");

                  /*  String myJsonS = dato.getString(0);
                    Toast.makeText(MainActivity.this, "Cargando", Toast.LENGTH_LONG).show();

                    txtv.setText("Datos ->"+myJsonS);

                   /* JSONArray mJsonArray = response.getJSONArray("contacts");
                    for (int milo = 0;milo < mJsonArray.length();milo++){

                        JSONObject myJsonObject = mJsonArray.getJSONObject(milo);
                        String name = myJsonObject.getString("name");
                        Toast.makeText(MainActivity.this, "Nombre: " + name, Toast.LENGTH_LONG).show();
                    }*//*
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("Error ---------------------ex ---------->"+ error.getMessage());
            }
        }

        );*/

      //  queue.add(request2);
    //}

//}
