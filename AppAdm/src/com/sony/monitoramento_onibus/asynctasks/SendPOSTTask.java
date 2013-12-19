package com.sony.monitoramento_onibus.asynctasks;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.sony.monitoramento_onibus.screens.fugaRota.FugaRotaActivity;
import com.sony.monitoramento_onibus.screens.horarios.HorariosManagerActivity;
import com.sony.monitoramento_onibus.screens.onibus.OnibusActivity;
import com.sony.monitoramento_onibus.screens.pontoonibus.PontoOnibusActivity;
import com.sony.monitoramento_onibus.screens.rotas.RotasActivity;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class SendPOSTTask extends AsyncTask<String, Void, String> {
	private final Context context;

	public SendPOSTTask(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... paramns) {

		int trys = 10;
		String result = null;

		while (trys > 0) {
			trys--;

			try {

				MyLog.Info("POST: " + paramns[0] + " - " + paramns[1]);

				result = Utils.sendPOST(paramns[0], paramns[1]);

				if (result != null) {
					break;
				}

			} catch (final IOException e) {
				MyLog.Debug("Unable to retrieve web page. URL may be invalid: "
						+ paramns[0] + " - " + paramns[1]);
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
		MyLog.Info("POST_RESULT: " + result);

		if (this.context instanceof PontoOnibusActivity) {
			((PontoOnibusActivity) this.context).callBackPOST(result);
		} else if (this.context instanceof OnibusActivity) {
			((OnibusActivity) this.context).callBackPOST(result);
		} else if (this.context instanceof RotasActivity) {
			((RotasActivity) this.context).callBackPOST(result);
		} else if (this.context instanceof HorariosManagerActivity) {
			((HorariosManagerActivity) this.context).callBackPOST(result);
		} else if (this.context instanceof FugaRotaActivity) {
			((FugaRotaActivity) this.context).callBackPOST(result);
		} else {
			MyLog.Error("CallBackPost: " + this.context.getClass()
					+ " not handled.");
		}

	}
}
