package com.example.monitoramento_onibus;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.example.monitoramento_onibus.core.PontoOnibus;
import com.example.monitoramento_onibus.utils.Utils;

public class PontoOnibusActivity extends Activity {
    ArrayList<PontoOnibus> myPontoOnibusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_pontoonibus);

        this.myPontoOnibusArray = new ArrayList<PontoOnibus>();

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.registerForContextMenu(this
                .findViewById(R.id.list_view_pontoonibus));

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this)
                    .execute("http://192.168.1.244:4567/web-api/pontoonibus");
        }
    }

    public void callBack(JSONObject json) {
        ListView listView = (ListView) this
                .findViewById(R.id.list_view_pontoonibus);
        ProgressBar progress = (ProgressBar) this
                .findViewById(R.id.progressBarPO);

        try {
            if (json.getInt("rowCount") > 0) {
                JSONArray rowsArrays = json.getJSONArray("rows");

                for (int i = 0; i < rowsArrays.length(); i++) {
                    this.myPontoOnibusArray.add(new PontoOnibus(rowsArrays
                            .getJSONObject(i).getString("nome")));
                }

                PontoOnibusAdapter adapter = new PontoOnibusAdapter(
                        this.getApplicationContext(), this.myPontoOnibusArray);
                listView.setAdapter(adapter);

                progress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            } else {
                // TODO Tratar sem ponto de onibus;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.context_menu_pontoonibus:
            // TODO Tratar clicar em remover
            System.out.println(info.id);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_pontoonibus, menu);
    }
}
