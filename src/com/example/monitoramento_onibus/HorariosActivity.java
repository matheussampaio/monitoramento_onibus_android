package com.example.monitoramento_onibus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.monitoramento_onibus.utils.Utils;

public class HorariosActivity extends Activity {
    private final static String DEBUG_TAG = "DEBUG_TAG";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        if (!Utils.isConnected((Activity) this)) {
            showNotConnected();
        } else {
            new DownloadWebpageTask().execute("http://192.168.1.244:3001/onibus?placa=ABC-0001");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.horarios, menu);
        return true;
    }
    
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONObject mainObject;
            JSONArray rowsObject;
            
            try {
                mainObject = new JSONObject(result);
                Log.d(DEBUG_TAG, mainObject.toString());
                
                rowsObject = mainObject.getJSONArray("rows");
                Log.d(DEBUG_TAG, rowsObject.toString());
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
    
    public void showNotConnected() {
        AlertDialog alerta;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        builder.setTitle("Error"); 
        
        builder.setMessage("Você deve estar conectado a internet."); 
        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                moveTaskToBack(true);
            }
        }); 
        
        alerta = builder.create(); 
        alerta.show();
    }
    
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 1024*2;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");        
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
