package com.example.monitoramento_onibus.asynctasks;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.example.monitoramento_onibus.PontoOnibusActivity;
import com.example.monitoramento_onibus.RotasActivity;
import com.example.monitoramento_onibus.utils.Utils;

public class DownloadJSONTask extends AsyncTask<String, Void, String> {
    private final Context context;

    public DownloadJSONTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            return Utils.downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        JSONObject mainObject;

        try {
            mainObject = new JSONObject(result);

            if (this.context instanceof PontoOnibusActivity) {
                ((PontoOnibusActivity) this.context).callBack(mainObject);
            } else if (this.context instanceof RotasActivity) {
                ((RotasActivity) this.context).callBack(mainObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
