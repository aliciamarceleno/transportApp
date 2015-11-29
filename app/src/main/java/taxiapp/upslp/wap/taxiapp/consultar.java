package taxiapp.upslp.wap.taxiapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class consultar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);
       // WebView we =(WebView)findViewById(R.id.webView);
        //we.loadUrl("http://google.com");
        Spinner spinner= (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.Tipo2,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



    public void volver(View view){
        Intent volver = new Intent(this, MainActivity.class);
        startActivity(volver);
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
     public void consultarunidad1(View view){
         Spinner tipo=(Spinner)findViewById(R.id.spinner);
         String tipo1 = tipo.getSelectedItem().toString();

         EditText numero= (EditText)findViewById(R.id.editText3);
         String num1= numero.getText().toString();

         WebView consultarunidad= (WebView)findViewById(R.id.webView);
         consultarunidad.loadUrl("http://transapp.netne.net/consulta.php?tipo="+tipo+"&numero="+numero+"");
     }


}
