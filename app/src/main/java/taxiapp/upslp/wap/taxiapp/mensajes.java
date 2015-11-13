package taxiapp.upslp.wap.taxiapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
public class mensajes extends AppCompatActivity implements LocationListener{
    LocationManager ubicacion;
    private EditText telefono;
    Button panico;
    String provider;
    static final int PICK_CONTACT=1;

    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "imposible leer ubicacion desde el servidor");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }


    public void getContacto(View view){
        final int PICK_CONTACT=1;
        try
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT);
        }
        catch (Exception e) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        try
        {


            if (requestCode == PICK_CONTACT)
            {
                Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
                cursor.moveToNext();
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String  name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String phone=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String phoneNumber="test";

                if ( phone.equalsIgnoreCase("1"))
                    phone = "true";
                else
                    phone = "false" ;

                if (Boolean.parseBoolean(phone))
                {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                    while (phones.moveToNext())
                    {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }
                Toast.makeText(this, "You are selected Contact name "+name, Toast.LENGTH_LONG).show();
                if(telefono.getText().length()!=0)
                {
                    telefono.setText(telefono.getText().toString()+","+phoneNumber);
                }
                else
                {
                    telefono.setText(phoneNumber);
                }

            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }














    protected void onResume() {
        super.onResume();
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            ubicacion.requestLocationUpdates(provider, 400, 1, this);
        }

    }
    protected void onPause() {
        super.onPause();
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {

            ubicacion.removeUpdates(this);
        }

    }
    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void mensajecontacto(View view){
        //Toast contacto = Toast.makeText(this,"Se enviará tu ubicación cada 5 minutos", Toast.LENGTH_LONG);
        //contacto.show();
        btnFisico();
        //Intent mnscontacto = new Intent(this, MainActivity.class);
        //startActivity(mnscontacto);
    }

    public void volvercontacto(View view){
        Intent mnscontacto = new Intent(this, MainActivity.class);
        startActivity(mnscontacto);
    }




    private class ReadLocationJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                String direccion="";
                JSONObject jsonObject = new JSONObject(result);
                direccion=""+jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                direccion=remove1(direccion);

                Toast.makeText(getBaseContext(),"Direccion Actual: "+direccion  ,Toast.LENGTH_SHORT).show();

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(telefono.getText().toString(), null, "Estoy en peligro en " + direccion,null, null);

            } catch (Exception e) {
                Log.d("Error de ubicacion", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
    //////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);
        telefono=(EditText)findViewById(R.id.editText4);
        panico=(Button)findViewById(R.id.button7);
        panico.setEnabled(false);






        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        telefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!telefono.getText().toString().trim().isEmpty())
                    panico.setEnabled(true);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        ubicacion= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria=new Criteria();
        String proveedor=ubicacion.getBestProvider(criteria,true);
        provider=ubicacion.getBestProvider(criteria,true);
        String latitud="";
        String longitud="";


        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            ubicacion.requestLocationUpdates(provider, 400, 1, this);
            Location posActual=ubicacion.getLastKnownLocation(proveedor);
            latitud=""+posActual.getLatitude();
            longitud=""+posActual.getLongitude();


        }









    }

    public static String remove1(String input) {

        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        //funcion para quitar acentos y caracteres especiales y solo enviar mensaje
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    public void btnGetUbicacion(View view) {
        String latitud;
        String longitud;
        ubicacion= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria=new Criteria();
        String proveedor=ubicacion.getBestProvider(criteria,true);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {


        Location posActual=ubicacion.getLastKnownLocation(proveedor);
        latitud=""+posActual.getLatitude();
        longitud=""+posActual.getLongitude();
        }
        else {
            latitud="";
            longitud="";


        }




        new ReadLocationJSONFeedTask().execute(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                        latitud+","+
                        longitud);
    }
    public void btnFisico() {


        ubicacion= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria=new Criteria();
        String proveedor=ubicacion.getBestProvider(criteria,true);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {


            Location posActual = ubicacion.getLastKnownLocation(proveedor);
            String latitud = "" + posActual.getLatitude();
            String longitud = "" + posActual.getLongitude();




            new ReadLocationJSONFeedTask().execute(
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                            latitud + "," +
                            longitud);
        }
    }

}

