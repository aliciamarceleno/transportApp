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
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
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

public class evaluar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluar);

        Spinner spinner= (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.Tipo,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }
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


    private class Enviarevaluacion extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                String direccion="";
                JSONObject jsonObject = new JSONObject(result);
                //direccion=""+jsonObject.getJSONArray("resultados").getJSONObject(0).getString("resultado");
                direccion=""+jsonObject.getString("resultados");


                Toast.makeText(getBaseContext(),direccion  ,Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d("error", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }


    public void mensaje(View view){
            Spinner sp1=(Spinner)findViewById(R.id.spinner);
            String tipo= sp1.getSelectedItem().toString();

        EditText ed1=(EditText)findViewById(R.id.editText);
        String numero= ed1.getText().toString();

        TextView ed2=(TextView)findViewById(R.id.textView7);
        String comentarios= ed2.getText().toString();

        RatingBar r1=(RatingBar)findViewById(R.id.ratingBar);
         String rating="4";//r1.getNumStars();


        new Enviarevaluacion().execute(
                "http://transapp.netne.net/inscomentario.php?tipo="+tipo+"&numero="+numero+"&comentarios="+comentarios+"&rating="+rating+"");

        Toast mns = Toast.makeText(this,"Se ha enviado tu comentario", Toast.LENGTH_LONG);
        mns.show();
        Intent mensaje = new Intent(this, MainActivity.class);
        startActivity(mensaje);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
