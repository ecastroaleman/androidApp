package com.example.condado;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Inicio extends AppCompatActivity {
private TextView username;
private ImageView vfoto;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        username = findViewById(R.id.correo);
        vfoto = findViewById(R.id.foto);
        Bundle mibundle = this.getIntent().getExtras();
        JSONObject objDatos = null;
        String pusername, puserid, ptipo,pcasa,ptelefono,psaldo,pcontador,ptusuario,ptcasa;
        if (mibundle != null){
            try {
                objDatos = new JSONObject(mibundle.getString("Datos"));
                pusername  = objDatos.getString("username");
                username.setText("Bienvenido al Sistema "+pusername);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] imageBytes = baos.toByteArray();
                String pfoto  = objDatos.getString("foto");
                imageBytes = Base64.decode(pfoto, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                vfoto.setImageBitmap(decodedImage);

                /*
                *  ImageView image =(ImageView)findViewById(R.id.image);

        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                * */

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
