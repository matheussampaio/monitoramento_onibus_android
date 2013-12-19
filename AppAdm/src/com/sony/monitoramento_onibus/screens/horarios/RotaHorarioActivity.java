package com.sony.monitoramento_onibus.screens.horarios;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.utils.Utils;

public class RotaHorarioActivity extends Activity {
    private static final int SHOW_SUBACTIVITY_ROTA = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rotas);

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                    + "/web-api/horarios");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callBackJSON(JSONObject json) {

        try {
            final ProgressBar progress = (ProgressBar) RotaHorarioActivity.this
                    .findViewById(R.id.progressBarRota);

            progress.setVisibility(View.GONE);

            final LinearLayout ll = (LinearLayout) RotaHorarioActivity.this
                    .findViewById(R.id.linearLayoutRota);
            final LayoutParams lp = new ActionBar.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            final TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextSize(24f);
            ll.addView(tv, lp);

            if (json.getInt("rowCount") > 0) {

                tv.setText(RotaHorarioActivity.this.getString(R.string.rotas));

                for (int i = 0; i < json.getJSONArray("rotas").length(); i++) {
                    final String nomeRota = json.getJSONArray("rotas")
                            .getJSONObject(i).getString("nome");
                    final Button b = new Button(this);
                    b.setText(nomeRota);

                    b.setOnClickListener(new ClickOnRotaListener(json, this,
                            nomeRota) {

                        @Override
                        public void onClick(View v) {
                            final Intent intentRota = new Intent(this.context,
                                    HorariosActivity.class);

                            intentRota.putExtra("json",
                                    this.jsonObject.toString());
                            intentRota.putExtra("title", this.nomeRota);

                            RotaHorarioActivity.this.startActivityForResult(
                                    intentRota, SHOW_SUBACTIVITY_ROTA);
                        }
                    });
                    ll.addView(b, lp);
                }
            } else {
                tv.setText(RotaHorarioActivity.this.getString(R.string.no_rota));
            }

        } catch (final JSONException je) {
            // TODO: handle exception: Não há nenhuma rota.
        }
    }
}
