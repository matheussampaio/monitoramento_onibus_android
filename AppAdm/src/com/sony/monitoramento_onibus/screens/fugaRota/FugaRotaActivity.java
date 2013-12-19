package com.sony.monitoramento_onibus.screens.fugaRota;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.asynctasks.SendPOSTTask;
import com.sony.monitoramento_onibus.model.FugaRota;
import com.sony.monitoramento_onibus.screens.RemoverDialog;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FugaRotaActivity extends Activity {
	ArrayList<FugaRota> myFugaRotaArray;
	RemoverDialog resolverDialog;
	AlertDialog.Builder addDialog;
	View view;
	boolean vendoHistorico = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuga_rota);

		this.myFugaRotaArray = new ArrayList<FugaRota>();

		final ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		this.registerForContextMenu(this.findViewById(R.id.list_view_fugarota));

		if (!Utils.isConnected(this)) {
			Utils.showNotConnected(this);
		} else {
			new DownloadJSONTask(this).execute("http://" + Utils.getURL()
					+ "/web-api/fugarota");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fuga_rota, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_menu_fugarota:
			this.resolverFugaRota((int) info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		final MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.context_menu_fugarota, menu);
	}

	public void callBackPOST(String result) {
		JSONObject json;

		MyLog.Info("CallBackPost: " + result);

		try {
			json = new JSONObject(result);
			if (json.getString("detail").equals("resolverfuga")) {
				this.handlerResolverFugaRota(json);
			}
		} catch (final JSONException e) {
			MyLog.Debug(result);
			MyLog.Error(e.getMessage());
		}

	}

	public void callBackJSON(JSONObject json) {

		final ListView listView;
		final ProgressBar progress;
		final TextView textView;

		if (vendoHistorico) {
			listView = (ListView) view.findViewById(R.id.list_view_FH);
			progress = (ProgressBar) view.findViewById(R.id.progressBarFH);
			textView = (TextView) view.findViewById(R.id.textViewEmptyFH);
		} else {
			listView = (ListView) this.findViewById(R.id.list_view_fugarota);
			progress = (ProgressBar) this.findViewById(R.id.progressBarFR);
			textView = (TextView) this.findViewById(R.id.textViewEmptyFugarota);
		}

		try {

			textView.setVisibility(View.INVISIBLE);

			if (json.getInt("rowCount") > 0) {
				final JSONArray rowsArrays = json.getJSONArray("rows");

				this.myFugaRotaArray.clear();

				for (int i = 0; i < rowsArrays.length(); i++) {
					this.myFugaRotaArray.add(new FugaRota(rowsArrays
							.getJSONObject(i).getInt("id_fugarota"), rowsArrays
							.getJSONObject(i).getString("placa"), rowsArrays
							.getJSONObject(i).getString("horarioinicio"),
							rowsArrays.getJSONObject(i).getString(
									"horariofinal"), rowsArrays
									.getJSONObject(i).getInt("id_onibus")));
				}

				final FugaRotaAdapter adapter = new FugaRotaAdapter(
						this.getApplicationContext(), this.myFugaRotaArray);
				listView.setAdapter(adapter);

				progress.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);

			} else {
				progress.setVisibility(View.GONE);

				textView.setVisibility(View.VISIBLE);
			}

		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		vendoHistorico = false;
	}

	private void handlerResolverFugaRota(JSONObject json) {
		final ProgressBar progressBar = (ProgressBar) this.resolverDialog
				.findViewById(R.id.progressBarDialog);
		progressBar.setVisibility(View.GONE);

		final TextView textView = (TextView) this.resolverDialog
				.findViewById(R.id.textViewDialog);

		try {

			if (!json.isNull("queryResult")) {
				final JSONObject result = json.getJSONObject("queryResult");

				if (!result.isNull("command")
						&& result.getString("command").equals("UPDATE")
						&& !result.isNull("rowCount")
						&& (result.getInt("rowCount") == 1)) {

					for (final FugaRota po : this.myFugaRotaArray) {
						if (po.getId() == json.getJSONObject("paramns").getInt(
								"idFugaRota")) {
							this.myFugaRotaArray.remove(po);
							break;
						}
					}

					((ListView) this.findViewById(R.id.list_view_fugarota))
							.invalidateViews();

					if (this.myFugaRotaArray.size() == 0) {
						final TextView textViewEmpty = (TextView) this
								.findViewById(R.id.textViewEmptyFugarota);

						textViewEmpty.setVisibility(View.VISIBLE);
					}

					this.resolverDialog.dismiss();
				}

			} else if (!json.isNull("err")) {
				final JSONObject err = json.getJSONObject("err");

				this.resolverDialog.setTitle(this.getString(R.string.error));
				textView.setText(err.getString("detail"));
				textView.setVisibility(View.VISIBLE);

				final Button btn = (Button) this.resolverDialog
						.findViewById(R.id.buttonDialog);

				btn.setVisibility(View.VISIBLE);

				btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						FugaRotaActivity.this.resolverDialog.dismiss();
					}
				});

			} else {
				MyLog.Debug(json.toString());
			}

		} catch (final JSONException e) {
			MyLog.Debug(json.toString());
			MyLog.Error(e.getMessage());
		}
	}

	private void resolverFugaRota(int position) {

		final FugaRota po = this.myFugaRotaArray.get(position);

		final String paramns = "idFugaRota=" + po.getId() + "&idOnibus="
				+ po.getIdOnibus();

		this.resolverDialog = new RemoverDialog(this);
		this.resolverDialog.setTitle(this.getString(R.string.processing)
				+ "...");
		this.resolverDialog.setCancelable(false);
		this.resolverDialog.setCanceledOnTouchOutside(false);
		this.resolverDialog.show();

		new SendPOSTTask(this).execute("http://" + Utils.getURL()
				+ "/web-api/resolverFuga", paramns);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_historico:
			if (!Utils.isConnected(this)) {
				Utils.showCantWithoutConnection(this);
			} else {
				// final Intent intentAddPontoOnibus = new Intent(this,
				// AddPontoOnibusActivity.class);
				// this.startActivity(intentAddPontoOnibus);
				this.verHistorico();
			}
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void verHistorico() {
		vendoHistorico = true;
		addDialog = new AlertDialog.Builder(this);
		
		new DownloadJSONTask(FugaRotaActivity.this).execute("http://"
				+ Utils.getURL() + "/web-api/fugarotahistorico");

		final LayoutInflater inflater = this.getLayoutInflater();

		addDialog.setTitle(R.string.history);

		view = inflater.inflate(R.layout.dialog_fugarota, null);

		addDialog.setView(view);

		addDialog.setNegativeButton(R.string.cancelar,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});

		addDialog.show();

	}
}