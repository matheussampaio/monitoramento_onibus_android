package com.sony.monitoramento_onibus.asynctasks;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import com.sony.monitoramento_onibus.screens.MainActivity;
import com.sony.monitoramento_onibus.screens.fugaRota.FugaRotaActivity;
import com.sony.monitoramento_onibus.screens.horarios.HorariosManagerActivity;
import com.sony.monitoramento_onibus.screens.horarios.RotaHorarioActivity;
import com.sony.monitoramento_onibus.screens.onibus.OnibusActivity;
import com.sony.monitoramento_onibus.screens.pontoonibus.PontoOnibusActivity;
import com.sony.monitoramento_onibus.screens.rotas.RotasActivity;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class DownloadJSONTask extends AsyncTask<String, Void, String> {
	private final Context context;
	private String[] urls;

	public DownloadJSONTask(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... urls) {
		this.urls = urls;

		// params comes from the execute() call: params[0] is the url.

		int trys = 10;
		String result = null;

		while (trys > 0) {
			trys--;

			try {

				MyLog.Info("GET: " + urls[0]);

				result = Utils.downloadUrl(urls[0]);

				if (result != null) {
					break;
				}

			} catch (final IOException e) {
				MyLog.Debug("Unable to retrieve web page. URL may be invalid: "
						+ urls[0]);
			}

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				MyLog.Error(e.getMessage());
			}
		}

		return result;
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		JSONObject mainObject;

		try {
			mainObject = new JSONObject(result);

			if (this.context instanceof PontoOnibusActivity) {
				((PontoOnibusActivity) this.context).callBackJSON(mainObject);
			} else if (this.context instanceof RotaHorarioActivity) {
				((RotaHorarioActivity) this.context).callBackJSON(mainObject);
			} else if (this.context instanceof OnibusActivity) {
				((OnibusActivity) this.context).callBackJSON(mainObject);
			} else if (this.context instanceof HorariosManagerActivity) {
				((HorariosManagerActivity) this.context)
						.callBackJSON(mainObject);
			} else if (this.context instanceof RotasActivity) {
				((RotasActivity) this.context).callBackJSON(mainObject);
			} else if (this.context instanceof FugaRotaActivity) {
				((FugaRotaActivity) this.context).callBackJSON(mainObject);
			} else if (this.context instanceof MainActivity) {
				((MainActivity) this.context).callBackJSON(mainObject,
						this.urls[1]);

			}

		} catch (final JSONException e) {
			MyLog.Debug(result);
			MyLog.Error(e.getMessage());

		}
	}
}
