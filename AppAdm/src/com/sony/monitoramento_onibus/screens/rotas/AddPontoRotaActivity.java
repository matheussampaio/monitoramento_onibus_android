package com.sony.monitoramento_onibus.screens.rotas;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.utils.Utils;

public class AddPontoRotaActivity extends Activity {

	private Spinner spPontoAnterior;
	private Spinner spNovoPonto;
	private Spinner spPontoPosterior;
	private Context context = this;
	private String rota;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle extras = this.getIntent().getExtras();
		rota = extras.getString("rota");

		setContentView(R.layout.activity_add_ponto_rota);

		spNovoPonto = (Spinner) findViewById(R.id.spPontoNovo);
		spPontoAnterior = (Spinner) findViewById(R.id.spPontoAnterior);
		spPontoPosterior = (Spinner) findViewById(R.id.spPontoPosterior);

		if (!Utils.isConnected(this)) {
			Utils.showNotConnected(this);
		} else {
			DownloadJSONTask asyncPontos = new DownloadJSONTask(this);
			asyncPontos.execute("http://" + Utils.getURL()
					+ "/web-api/pontoonibus");
		}

		/*Button btAdicionar = (Button) findViewById(R.id.btAddPontoEmRota);
		btAdicionar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String novoPonto = String
						.valueOf(spNovoPonto.getSelectedItem()).split(":")[0];
				String pontoAnterior = String.valueOf(
						spPontoAnterior.getSelectedItem()).split(":")[0];
				String pontoPosterior = String.valueOf(
						spPontoPosterior.getSelectedItem()).split(":")[0];

				String myurl = "http://" + Utils.getURL()
						+ "/web-api/adicionarPontoRota?numeroRota=" + rota
						+ "&pontoNovo=" + novoPonto + "&pontoAnterior="
						+ pontoAnterior + "&pontoPosterior=" + pontoPosterior;
				DownloadJSONTask asyncAddPontoRota = new DownloadJSONTask(
						context);
				asyncAddPontoRota.execute(myurl);
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.add_onibus, menu);
		return true;
	}

	public void callBackJSON(JSONObject json) {
		/*ProgressBar progress = (ProgressBar) this
				.findViewById(R.id.progressBar1);
		progress.setVisibility(View.GONE);

		try {
			if (json.getInt("rowCount") > 0) {
				JSONArray rowsArrays = json.getJSONArray("rows");
				List<String> pontos = new ArrayList<String>();

				for (int i = 0; i < rowsArrays.length(); i++) {
					pontos.add(rowsArrays.getJSONObject(i).getString(
							"id_pontoonibus")
							+ ":"
							+ rowsArrays.getJSONObject(i).getString("nome"));
				}

				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_spinner_item, pontos);
				ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
				spinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_item);
				spNovoPonto.setAdapter(spinnerArrayAdapter);
				spPontoAnterior.setAdapter(spinnerArrayAdapter);
				spPontoPosterior.setAdapter(spinnerArrayAdapter);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public void getError(String result) {
		Toast toast = Toast.makeText(getApplicationContext(),
				result.split(result.substring(80, 82), 2)[0],
				Toast.LENGTH_SHORT);
		toast.show();
	}
}