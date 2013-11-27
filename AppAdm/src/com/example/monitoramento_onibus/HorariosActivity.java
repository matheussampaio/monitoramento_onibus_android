package com.example.monitoramento_onibus;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.monitoramento_onibus.core.Horario;

public class HorariosActivity extends Activity {
    ArrayList<Horario> myHorarioArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);
        
        myHorarioArray = new ArrayList<Horario>();
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
            getActionBar().setTitle(getString(R.string.rota_for_title) + " " + extras.getString("title"));
            
            try {
                JSONArray rowsArrays = new JSONObject(extras.getString("json")).getJSONArray("rows");
                
                ListView listView = (ListView) findViewById(R.id.list_view_horarios);
                
                for (int i = 0; i < rowsArrays.length(); i++) {
                    if (rowsArrays.getJSONObject(i).get("nome").equals(extras.getString("title"))) {
                        myHorarioArray.add(new Horario(rowsArrays.getJSONObject(i).getInt("id_pontoonibus"), rowsArrays.getJSONObject(i).getString("tempo")));
                    }
                }
                
                HorarioAdapter adapter = new HorarioAdapter(getApplicationContext(), myHorarioArray);
                listView.setAdapter(adapter);
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }
        
    }
}
