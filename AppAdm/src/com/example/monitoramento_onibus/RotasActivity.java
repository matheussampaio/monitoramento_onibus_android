package com.example.monitoramento_onibus;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.example.monitoramento_onibus.utils.Utils;

public class RotasActivity extends Activity {
    private static final int SHOW_SUBACTIVITY_ROTA = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rotas);

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this)
                    .execute("http://192.168.1.244:4567/web-api/horarios");
        }
    }

    public void callBack(JSONObject json) {

        try {
            ProgressBar progress = (ProgressBar) RotasActivity.this
                    .findViewById(R.id.progressBarRota);

            progress.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) RotasActivity.this
                    .findViewById(R.id.linearLayoutRota);
            LayoutParams lp = new ActionBar.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextSize(24f);
            ll.addView(tv, lp);

            if (json.getInt("rowCount") > 0) {

                tv.setText(RotasActivity.this.getString(R.string.rotas));

                for (int i = 0; i < json.getJSONArray("rotas").length(); i++) {
                    String nomeRota = json.getJSONArray("rotas")
                            .getJSONObject(i).getString("nome");
                    Button b = new Button(this);
                    b.setText(nomeRota);

                    b.setOnClickListener(new ClickOnRotaListener(json, this,
                            nomeRota) {

                        @Override
                        public void onClick(View v) {
                            Intent intentRota = new Intent(this.context,
                                    HorariosActivity.class);

                            intentRota.putExtra("json",
                                    this.jsonObject.toString());
                            intentRota.putExtra("title", this.nomeRota);

                            RotasActivity.this.startActivityForResult(
                                    intentRota, SHOW_SUBACTIVITY_ROTA);
                        }
                    });

                    ll.addView(b, lp);
                }

            } else {
                tv.setText(RotasActivity.this.getString(R.string.no_rota));
            }

        } catch (JSONException je) {
            // TODO: handle exception: Não há nenhum horário.
        }
    }
}
