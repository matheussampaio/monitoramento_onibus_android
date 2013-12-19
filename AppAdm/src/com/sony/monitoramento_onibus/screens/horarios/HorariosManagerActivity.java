package com.sony.monitoramento_onibus.screens.horarios;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.asynctasks.SendPOSTTask;
import com.sony.monitoramento_onibus.model.Horario;
import com.sony.monitoramento_onibus.model.Onibus;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class HorariosManagerActivity extends Activity {
	private Spinner spOnibus;
	private List<Onibus> myOnibus;
	private List<Integer> idPontos;
	private ArrayList<Horario> myHorarioArray;
	private Hashtable<String, Integer> placaIdOni = new Hashtable<String, Integer>();
	private Spinner spOnibus2;
	private Spinner spPonto;
	private View view;
	private boolean baixandoPontos = false;
	private boolean baixandoOnibus = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_gerenciar_horarios);

		this.myOnibus = new ArrayList<Onibus>();
		this.idPontos = new ArrayList<Integer>();
		this.myHorarioArray = new ArrayList<Horario>();
		this.placaIdOni = new Hashtable<String, Integer>();

		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (!Utils.isConnected(this)) {
			Utils.showNotConnected(this);
		} else {
			new DownloadJSONTask(this).execute("http://" + Utils.getURL()
					+ "/web-api/horariosAdmin");
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		this.getMenuInflater().inflate(R.menu.context_menu_horario, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.horarioadmin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_horario:

			if (!Utils.isConnected(this)) {
				Utils.showCantWithoutConnection(this);
			} else {
				baixandoOnibus = true;
				new DownloadJSONTask(this).execute("http://" + Utils.getURL()
						+ "/web-api/onibus");
			}
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void callBackJSON(JSONObject json) {
		if (baixandoPontos) {
			CallBackPontos(json);
		} else if (baixandoOnibus) {
			CallBackOnibus(json);
		} else {
			CallBackHorarios(json);
		}
	}

	private void CallBackOnibus(JSONObject json) {
		try {

			if (json.getInt("rowCount") > 0) {
				if (json.getString("detail").equals("get_onibus")) {

					this.adicionaHorarioaoOnibus(json);
				}
			}
		} catch (JSONException je) {
			// TODO: handle exception: Não há nenhum horário.
		}
		baixandoPontos = true;
		baixandoOnibus = false;
		new DownloadJSONTask(this).execute("http://" + Utils.getURL()
				+ "/web-api/pontoonibus");
	}

	private void CallBackPontos(JSONObject json) {
		try {
			JSONArray rowsArrays = json.getJSONArray("rows");

			for (int i = 0; i < rowsArrays.length(); i++) {
				final int nomePonto = rowsArrays.getJSONObject(i).getInt(
						"id_pontoonibus");
				idPontos.add(nomePonto);
			}
		} catch (final JSONException e) {
			e.printStackTrace();
		}

		ArrayAdapter<Integer> arrayAdapterPO = new ArrayAdapter<Integer>(
				getApplicationContext(), android.R.layout.simple_spinner_item,
				idPontos);
		arrayAdapterPO
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spPonto = (Spinner) view.findViewById(R.id.pontoSpinner01);
		spPonto.setAdapter(arrayAdapterPO);

		baixandoPontos = false;
	}

	private void CallBackHorarios(JSONObject json) {
		try {
			if (json.getInt("rowCount") > 0) {
				final JSONArray rowsArrays = json.getJSONArray("rows");
				List<String> placas = new ArrayList<String>();

				for (int i = 0; i < rowsArrays.length(); i++) {
					Onibus onibus = new Onibus(rowsArrays.getJSONObject(i)
							.getInt("id_onibus"), rowsArrays.getJSONObject(i)
							.getString("placa"));
					if (!myOnibus.contains(onibus)) {
						this.myOnibus.add(onibus);
					}
				}

				for (int i = 0; i < myOnibus.size(); i++) {
					placas.add(myOnibus.get(i).getPlaca());
				}

				// cria spinner pra listar onibus
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_spinner_item, placas);
				arrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_item);
				spOnibus = (Spinner) findViewById(R.id.onibusSpinner);
				spOnibus.setAdapter(arrayAdapter);

				// botão para visualizar horarios
				Button btVisualizarHorarios = (Button) findViewById(R.id.visualizarHorariosAdmin);
				btVisualizarHorarios.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						myHorarioArray.clear();
						ListView listView = (ListView) findViewById(R.id.list_view_horarios);

						for (int i = 0; i < rowsArrays.length(); i++) {

							try {
								if (rowsArrays.getJSONObject(i)
										.getString("placa")
										.equals(spOnibus.getSelectedItem())) {
									try {
										myHorarioArray
												.add(new Horario(
														rowsArrays
																.getJSONObject(
																		i)
																.getInt("id_pontoonibus"),
														rowsArrays
																.getJSONObject(
																		i)
																.getString(
																		"tempo")));
									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						HorarioAdapter adapter = new HorarioAdapter(
								getApplicationContext(), myHorarioArray);
						listView.setAdapter(adapter);
					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// NOVO <<<<<<<<<<<<<<<<<<<
	private void adicionaHorarioaoOnibus(JSONObject json) {
		List<String> placas = new ArrayList<String>();

		try {
			JSONArray rowsArrays = json.getJSONArray("rows");

			for (int i = 0; i < rowsArrays.length(); i++) {

				placaIdOni.put(rowsArrays.getJSONObject(i).getString("placa"),
						rowsArrays.getJSONObject(i).getInt("id_onibus"));
				if (!placas.contains(rowsArrays.getJSONObject(i).getString(
						"placa"))) {
					placas.add(rowsArrays.getJSONObject(i).getString("placa"));
				}
			}

		} catch (final JSONException e) {
			e.printStackTrace();
		}
		AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		addDialog.setTitle(R.string.title_dialog_add_horario);

		view = inflater.inflate(R.layout.dialog_add_horario, null);

		addDialog.setView(view);

		ArrayAdapter<String> arrayAdapterO = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_spinner_item,
				placas);
		arrayAdapterO
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spOnibus2 = (Spinner) view.findViewById(R.id.onibusSpinner2);
		spOnibus2.setAdapter(arrayAdapterO);

		final TimePicker timep = (TimePicker) view
				.findViewById(R.id.timePicker1);

		addDialog.setPositiveButton(R.string.adicionar,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						final String paramns = "idOnibus="
								+ placaIdOni.get(spOnibus2.getSelectedItem())
								+ "&tempo=" + timep.getCurrentHour() + ":"
								+ timep.getCurrentMinute() + "&idPontoOnibus="
								+ spPonto.getSelectedItem();

						new SendPOSTTask(HorariosManagerActivity.this).execute(
								"http://" + Utils.getURL()
										+ "/web-api/horariosAdmin/adicionar",
								paramns);

					}
				});

		addDialog.setNegativeButton(R.string.cancelar,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						baixandoOnibus = false;
						baixandoPontos = false;
						dialog.dismiss();
					}
				});

		addDialog.show();

	}

	// NOVO <<<<<<<<<<<<<<<<<<<
	public void callBackPOST(String result) {
		JSONObject json;

		MyLog.Info("Resultado do post");

		try {
			json = new JSONObject(result);
			if (json.getString("detail").equals("add_horario_onibus")) {
				Toast toast = Toast.makeText(this.getApplicationContext(),
						"Horário adicionado com Sucesso", Toast.LENGTH_SHORT);
				toast.show();
			}
		} catch (final JSONException e) {
			MyLog.Debug(result);
			MyLog.Error(e.getMessage());
		}

	}
}
