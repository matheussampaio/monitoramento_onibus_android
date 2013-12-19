package com.sony.monitoramento_onibus.screens.onibus;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.utils.Utils;

public class AddOnibusActivity extends Activity {

    private EditText edPlacaOnibus;
    private Spinner spRota;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_onibus);

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.edPlacaOnibus = (EditText) this.findViewById(R.id.editPlaca);

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                    + "/web-api/horarios");
        }

        /*final Button btAdicionar = (Button) this.findViewById(R.id.btAddBus);
        btAdicionar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AddOnibusActivity.this.edPlacaOnibus.getText().toString()
                        .matches("([A-Z]{3})-([0-9]{4})")) {
                    final String myurl = "http://"
                            + Utils.getURL()
                            + "/web-api/adicionarOnibus?placa="
                            + AddOnibusActivity.this.edPlacaOnibus.getText()
                            + "&numero="
                            + String.valueOf(AddOnibusActivity.this.spRota
                                    .getSelectedItem());
                    new DownloadJSONTask(AddOnibusActivity.this.context)
                            .execute(myurl);
                } else {
                    final Toast toast = Toast.makeText(
                            AddOnibusActivity.this.getApplicationContext(),
                            AddOnibusActivity.this.getString(R.string.error_format),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });*/
    }
    
    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        this.getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void callBackJSON(JSONObject mainObject) {

       /* final ProgressBar progress = (ProgressBar) this
                .findViewById(R.id.progressBarAddBus);
        progress.setVisibility(View.GONE);
        try {

            if (mainObject.getInt("rowCount") > 0) {

                final List<String> rotas = new ArrayList<String>();

                for (int i = 0; i < mainObject.getJSONArray("rotas").length(); i++) {
                    final String nomeRota = mainObject.getJSONArray("rotas")
                            .getJSONObject(i).getString("nome");
                    rotas.add(nomeRota);

                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        this.getApplicationContext(),
                        android.R.layout.simple_spinner_item, rotas);
                final ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                spinnerArrayAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_item);
                this.spRota = (Spinner) this.findViewById(R.id.rota);
                this.spRota.setAdapter(spinnerArrayAdapter);

            }

        } catch (final JSONException je) {
            // TODO: handle exception: Não há nenhum horário.
        }*/
    }

    public void getError(String result) {
        if (result.split(result.substring(37, 39), 2)[0]
                .equals("Onibus criado com sucesso.")) {
            final Toast toast = Toast.makeText(this.getApplicationContext(),
                    this.getString(R.string.sucess_addBus), Toast.LENGTH_SHORT);
            toast.show();
            this.edPlacaOnibus.setText("");
        }
        if (result.split(result.substring(37, 39), 2)[0]
                .equals("Onibus com essa placa ja existe.")) {
            final Toast toast = Toast.makeText(this.getApplicationContext(),
                    this.getString(R.string.error_placaExistente),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        if (result.split(result.substring(37, 39), 2)[0]
                .equals("A rota que voce passou nao existe.")) {
            final Toast toast = Toast.makeText(this.getApplicationContext(),
                    this.getString(R.string.error_rotaInexistente),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
