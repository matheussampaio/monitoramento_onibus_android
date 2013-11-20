package com.example.monitoramento_onibus;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.example.monitoramento_onibus.core.Horario;

public class ListHorarioActivity extends Activity {
    private final static String DEBUG_TAG = "DEBUG_TAG";
    
    ArrayList<Horario> myHorarioArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota);
        
        myHorarioArray = new ArrayList<Horario>();
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
            getActionBar().setTitle("Rota " +  extras.getString("title"));
            
            try {
                Log.d(DEBUG_TAG, "eu");
                JSONArray rowsArrays = new JSONObject(extras.getString("json")).getJSONArray("rows");
                
                ListView listView = (ListView) findViewById(R.id.list_view_horarios);
                
                for (int i = 0; i < rowsArrays.length(); i++) {
                    
                    if (rowsArrays.getJSONObject(i).get("nome").equals(extras.getString("title"))) {
                        myHorarioArray.add(new Horario(rowsArrays.getJSONObject(i).getInt("id_pontoonibus"), rowsArrays.getJSONObject(i).getString("tempo")));
                    }
                }
                
                Log.d(DEBUG_TAG, "" + myHorarioArray.size());
                Log.d(DEBUG_TAG, "criando adpter");
                HorarioAdapter adapter = new HorarioAdapter(getApplicationContext(), myHorarioArray);
                Log.d(DEBUG_TAG, "setnando adpter");
                listView.setAdapter(adapter);
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.horarios, menu);
        return true;
    }
}
