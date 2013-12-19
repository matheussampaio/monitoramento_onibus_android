package com.sony.monitoramento_onibus.screens.pontoonibus;

import java.util.ArrayList;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.asynctasks.SendPOSTTask;
import com.sony.monitoramento_onibus.model.PontoOnibus;
import com.sony.monitoramento_onibus.screens.RemoverDialog;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class PontoOnibusActivity extends Activity {
    ArrayList<PontoOnibus> myPontoOnibusArray;
    RemoverDialog remDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_pontoonibus);

        this.myPontoOnibusArray = new ArrayList<PontoOnibus>();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.registerForContextMenu(this
                .findViewById(R.id.list_view_pontoonibus));

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                    + "/web-api/pontoonibus");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add_pontoonibus:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                // final Intent intentAddPontoOnibus = new Intent(this,
                // AddPontoOnibusActivity.class);
                // this.startActivity(intentAddPontoOnibus);
                this.adicionarPontoOnibus();
            }
            return true;
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void adicionarPontoOnibus() {
        final AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        addDialog.setTitle(R.string.title_dialog_add_ponto_onibus);

        final View view = inflater.inflate(R.layout.dialog_add_ponto_onibus,
                null);

        addDialog.setView(view);

        addDialog.setPositiveButton(R.string.adicionar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final EditText nome = (EditText) view
                                .findViewById(R.id.editTextNome);
                        final EditText lat = (EditText) view
                                .findViewById(R.id.editTextLat);
                        final EditText lng = (EditText) view
                                .findViewById(R.id.editTextLng);

                        if ((nome.getText().length() > 0)
                                && (lat.getText().length() > 0)
                                && (lng.getText().length() > 0)) {

                            final String paramns = "nome=" + nome.getText()
                                    + "&lat=" + lat.getText() + "&lng="
                                    + lng.getText();

                            new SendPOSTTask(PontoOnibusActivity.this).execute(
                                    "http://" + Utils.getURL()
                                            + "/web-api/pontoonibus/adicionar",
                                    paramns);

                        } else {
                            MyLog.Debug("Parando");
                        }

                    }
                });

        addDialog.setNegativeButton(R.string.cancelar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        addDialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.context_menu_pontoonibus:
            this.removerPontoOnibus((int) info.id);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.pontoonibus, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_pontoonibus, menu);
    }

    private void removerPontoOnibus(int position) {

        final PontoOnibus po = this.myPontoOnibusArray.get(position);

        final String paramns = "id_pontoonibus=" + po.getID();

        this.remDialog = new RemoverDialog(this);
        this.remDialog.setTitle(this.getString(R.string.removing) + "...");
        this.remDialog.setCancelable(false);
        this.remDialog.setCanceledOnTouchOutside(false);
        this.remDialog.show();

        new SendPOSTTask(this).execute("http://" + Utils.getURL()
                + "/web-api/pontoonibus/remover", paramns);

    }

    public void callBackPOST(String result) {
        JSONObject json;

        MyLog.Info("CallBackPost: " + result);

        try {
            json = new JSONObject(result);

            if (json.getString("detail").equals("rem_pontoonibus")) {
                this.handlerRmPontoOnibus(json);
            } else if (json.getString("detail").equals("add_pontoonibus")) {
                this.handlerAddPontoOnibus(json);
            }

        } catch (final JSONException e) {
            MyLog.Debug(result);
            MyLog.Error(e.getMessage());
        }

    }

    private void handlerRmPontoOnibus(JSONObject json) {
        MyLog.Debug("HandlingRemOnibus: " + json.toString());

        final ProgressBar progressBar = (ProgressBar) this.remDialog
                .findViewById(R.id.progressBarDialog);
        progressBar.setVisibility(View.GONE);

        final TextView textView = (TextView) this.remDialog
                .findViewById(R.id.textViewDialog);

        try {

            if (!json.isNull("queryResult")) {
                final JSONObject result = json.getJSONObject("queryResult");

                if (!result.isNull("command")
                        && result.getString("command").equals("DELETE")
                        && !result.isNull("rowCount")
                        && (result.getInt("rowCount") == 1)) {

                    for (final PontoOnibus po : this.myPontoOnibusArray) {
                        if (po.getID() == json.getJSONObject("paramns").getInt(
                                "id_pontoonibus")) {
                            this.myPontoOnibusArray.remove(po);
                            break;
                        }
                    }

                    ((ListView) this.findViewById(R.id.list_view_pontoonibus))
                            .invalidateViews();

                    if (this.myPontoOnibusArray.size() == 0) {
                        final TextView textViewEmpty = (TextView) this
                                .findViewById(R.id.textViewEmptyPontoOnibus);

                        textViewEmpty.setVisibility(View.VISIBLE);
                    }

                    this.remDialog.dismiss();
                }

            } else if (!json.isNull("err")) {
                final JSONObject err = json.getJSONObject("err");

                this.remDialog.setTitle(this.getString(R.string.error));
                textView.setText(err.getString("detail"));
                textView.setVisibility(View.VISIBLE);

                final Button btn = (Button) this.remDialog
                        .findViewById(R.id.buttonDialog);

                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PontoOnibusActivity.this.remDialog.dismiss();
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

    private void handlerAddPontoOnibus(JSONObject json) {
        MyLog.Debug("HandlingAddOnibus: " + json.toString());

        final ListView listView = (ListView) this
                .findViewById(R.id.list_view_pontoonibus);
        final ProgressBar progress = (ProgressBar) this
                .findViewById(R.id.progressBarPO);

        listView.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                + "/web-api/pontoonibus");
    }

    public void callBackJSON(JSONObject json) {
        MyLog.Info("CallBackJSON: " + json.toString());

        final ListView listView = (ListView) this
                .findViewById(R.id.list_view_pontoonibus);
        final ProgressBar progress = (ProgressBar) this
                .findViewById(R.id.progressBarPO);
        final TextView textView = (TextView) this
                .findViewById(R.id.textViewEmptyPontoOnibus);

        textView.setVisibility(View.INVISIBLE);

        try {
            if (json.getInt("rowCount") > 0) {
                final JSONArray rowsArrays = json.getJSONArray("rows");

                this.myPontoOnibusArray.clear();

                for (int i = 0; i < rowsArrays.length(); i++) {
                    this.myPontoOnibusArray.add(new PontoOnibus(rowsArrays
                            .getJSONObject(i).getInt("id_pontoonibus"),
                            rowsArrays.getJSONObject(i).getString("nome")));
                }

                final PontoOnibusAdapter adapter = new PontoOnibusAdapter(
                        this.getApplicationContext(), this.myPontoOnibusArray);
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
    }
}
