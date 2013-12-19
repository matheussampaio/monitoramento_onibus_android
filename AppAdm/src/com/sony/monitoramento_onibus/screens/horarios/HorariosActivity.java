package com.sony.monitoramento_onibus.screens.horarios;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.Horario;

public class HorariosActivity extends Activity {
    ArrayList<Horario> myHorarioArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_horarios);

        this.myHorarioArray = new ArrayList<Horario>();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Bundle extras = this.getIntent().getExtras();

        if (extras != null) {
            this.getActionBar().setTitle(
                    this.getString(R.string.rota) + " "
                            + extras.getString("title"));

            try {
                final JSONArray rowsArrays = new JSONObject(
                        extras.getString("json")).getJSONArray("rows");

                final ListView listView = (ListView) this
                        .findViewById(R.id.list_view_horarios);

                for (int i = 0; i < rowsArrays.length(); i++) {
                    if (rowsArrays.getJSONObject(i).get("nome")
                            .equals(extras.getString("title"))) {
                        this.myHorarioArray
                                .add(new Horario(rowsArrays.getJSONObject(i)
                                        .getInt("id_pontoonibus"), rowsArrays
                                        .getJSONObject(i).getString("tempo")));
                    }
                }

                final HorarioAdapter adapter = new HorarioAdapter(
                        this.getApplicationContext(), this.myHorarioArray);
                listView.setAdapter(adapter);

            } catch (final JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
