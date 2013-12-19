package com.sony.monitoramento_onibus.asynctasks;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.sony.monitoramento_onibus.screens.MainActivity;
import com.sony.monitoramento_onibus.utils.Utils;

public class FecthPositionsTask extends AsyncTask<String, Void, String> {
    private final Context context;
    private String[] urls;

    public FecthPositionsTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        this.urls = urls;

        // params comes from the execute() call: params[0] is the url.
        try {
            return Utils.downloadUrl(urls[0]);
        } catch (final IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        JSONObject mainObject;

        try {
            mainObject = new JSONObject(result);

            if (this.context instanceof MainActivity) {
                ((MainActivity) this.context).callBackJSON(mainObject,
                        this.urls[1]);

            }

        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }
}
