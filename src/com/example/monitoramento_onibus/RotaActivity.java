package com.example.monitoramento_onibus;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;

public class RotaActivity extends Activity {
    private final static String DEBUG_TAG = "DEBUG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("title");
            getActionBar().setTitle("Rota " + value);
            
            JSONObject jsonObject;
            
            try {
                jsonObject = new JSONObject(extras.getString("json"));
                Log.d(DEBUG_TAG, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout1);
            LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            
            
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.horarios, menu);
        return true;
    }
}
