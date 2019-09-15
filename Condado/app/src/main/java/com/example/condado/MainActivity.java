package com.example.condado;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.jsc.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {


    private ImageView logo ;
    private Bitmap bitmap;
    private Button btn_ingresar;
    private EditText vusuario,vclave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo2);
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        rbd.setCircular(true);
        logo.setImageDrawable(rbd);

        btn_ingresar = findViewById(R.id.ingresar);
        vusuario =  findViewById(R.id.usuario);
        vclave =  findViewById(R.id.clave);
        btn_ingresar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ingresarSistema(v);
            }
        });
        btn_ingresar.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                v.setBackgroundResource(R.drawable.buttonstylefocused);
                return false;
            }
        });
        btn_ingresar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    v.setBackgroundResource(R.drawable.buttonstylefocused);
                }else {v.setBackgroundResource(R.drawable.buttonstyenormal);}
            }
        });

    }

    public String EncriptaJS(String pcad){
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
                    url = getString(R.string.WS_login);
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
                                JSONObject objDatos = null;

                                String pdatos = "";
                                if (obj2.getString("Datos").length() > 0 ) {
                                     objDatos = new JSONObject(obj2.getString("Datos"));
                                        pdatos = obj2.getString("Datos");
                                   // pusername  = objDatos.getString("username");

                                }else {objDatos = new JSONObject("");}
                                pd.dismiss();
                                int time = 10;

                                if (obj2.getString("CodMensaje").toString().equals("0")){
                                    Toast.makeText(MainActivity.this,"Bienvenido al Sistema",time).show();
                                    Intent intent;
                                    Bundle miBundle = new Bundle();
                                    miBundle.putString("Datos",pdatos);

                                    intent = new Intent(MainActivity.this,Inicio.class);
                                    intent.putExtras(miBundle);
                                    startActivity(intent);


                                }else{

                                    if (obj2.getString("CodMensaje").toString().equals("300")){

                                        Toast.makeText(MainActivity.this, obj2.getString("Mensaje").toString(), time).show();

                                        //Inicia Establecer Nuevo Pass
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setCancelable(true);
                                        builder.setTitle("Nueva Contraseña");

                                        builder.setIcon(R.drawable.recuperaclave);
                                        builder.setMessage("Debe Establecer una Nueva Contraseña");

                                        final EditText inputClave = new EditText(MainActivity.this);
                                      //  final EditText inputClave = (EditText)findViewById(R.id.clave);
                                        inputClave.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        builder.setView(inputClave);

                                        builder.setPositiveButton("Confirmar",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                                                        pd.setMessage ("Guardando Nueva Clave ......");
                                                        pd.show();
                                                        String value = inputClave.getText().toString().trim();

                                                        String url = "";
                                                        if (value.length() > 0) {
                                                            String valueEnc = EncriptaJS(value);
                                                            url = getString(R.string.WS_recuperaclave);
                                                            url += "user="+vusuario.getText().toString();
                                                            url += "&newpass="+valueEnc;
                                                        }else {
                                                            Toast.makeText(MainActivity.this,"Debe ingresar un nuevo Password!",Toast.LENGTH_SHORT).show();
                                                            pd.dismiss();
                                                            return;
                                                        }


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





                                                    }
                                                });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });

                                        AlertDialog dialog = builder.create();
                                        dialog.setIcon(R.drawable.recuperaclave);

                                        dialog.show();
                                            //Fin Establecer Nuevo Pass

                                    }else {
                                        Toast.makeText(MainActivity.this, "Ocurrió un Error Msj:" + obj2.getString("Mensaje"), time).show();
                                        pd.dismiss();
                                    }
                                }

                            } catch (JSONException e) {
                                System.out.println("Error parseo -> "+e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Ocurrió un Error Msj:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
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

                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);

                }else {
                    Toast.makeText(MainActivity.this,"Debe Ingresar Clave", LENGTH_LONG).show();
                    vclave.getFocusable();

                }



            }else {
                Toast.makeText(MainActivity.this,"Debe Ingresar Usuario", LENGTH_LONG).show();
                vusuario.getFocusable();

            }


        }else {
            Toast.makeText(MainActivity.this,"Debe Ingresar Usuario y Clave", LENGTH_LONG).show();
            vusuario.getFocusable();
        }

    }


    public void RecuperarClave(View view) {

        if (vusuario.getText().toString().length() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Recuperar Contraseña");

            builder.setIcon(R.drawable.recuperaclave);
            builder.setMessage("Se enviará su contraseña al Correo :" + vusuario.getText().toString());
            builder.setPositiveButton("Confirmar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setMessage ("Solicitando Reseteo de Clave ......");
                            pd.show();
                            String url = getString(R.string.WS_recuperaclave);
                            url += "user="+vusuario.getText().toString();




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
                                            Toast.makeText(MainActivity.this,"Correo Enviado Exitosamente!",Toast.LENGTH_SHORT).show();

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





                        }
                    });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setIcon(R.drawable.recuperaclave);

            dialog.show();
        } else {
            Toast.makeText(this,"Debe Ingresar su Usuario!", LENGTH_LONG).show();
        }
    }

    //metodo temporal para ir a ver el menu
    public void irAMenu(View view) {
        Intent castor = new Intent(MainActivity.this,Menu_Inicio.class);
        startActivity(castor);
    }
}
