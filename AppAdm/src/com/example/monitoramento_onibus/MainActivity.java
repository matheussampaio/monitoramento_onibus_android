package com.example.monitoramento_onibus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.example.monitoramento_onibus.utils.Utils;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {
    private static final int SHOW_SUBACTIVITY_SETTINGS = 1001;
    private static final int SHOW_SUBACTIVITY_HORARIOS = 1002;
    private static final int SHOW_SUBACTIVITY_ONIBUS = 1003;
    private static final int SHOW_SUBACTIVITY_ROTES = 1004;
    private WebView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            this.startMapView();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_horarios:

            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                Intent intentHorarios = new Intent(this, RotasActivity.class);
                this.startActivityForResult(intentHorarios,
                        SHOW_SUBACTIVITY_HORARIOS);
            }
            return true;
        case R.id.action_pontoonibus:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                Intent intentPontoOnibus = new Intent(this,
                        PontoOnibusActivity.class);
                this.startActivityForResult(intentPontoOnibus,
                        SHOW_SUBACTIVITY_HORARIOS);
            }
            return true;
        case R.id.action_settings:
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            this.startActivityForResult(intentSettings,
                    SHOW_SUBACTIVITY_SETTINGS);
            return true;
        case R.id.action_onibus:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                Intent intentOnibus = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(intentOnibus,
                        SHOW_SUBACTIVITY_ONIBUS);
            }
            return true;
        case R.id.action_rotes:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                Intent intentRotes = new Intent(this,
                        RotesActivity.class);
                this.startActivityForResult(intentRotes,
                        SHOW_SUBACTIVITY_HORARIOS);
            }
            return true;
        case R.id.action_help:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public WebView startMapView() {

        if (this.mapView == null) {
            this.mapView = (WebView) this.findViewById(R.id.webView_Map);

            Point size = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);

            this.mapView.addJavascriptInterface(new JavaScriptService(size.x,
                    size.y), "mainService");

            this.mapView.getSettings().setJavaScriptEnabled(true);

            this.mapView.loadUrl("file:///android_asset/index.html");

            ImageButton btn = (ImageButton) this
                    .findViewById(R.id.imageButton1);

            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!Utils.isConnected(MainActivity.this)) {
                        Utils.showCantWithoutConnection(MainActivity.this);
                    } else {
                        MainActivity.this.mapView
                                .loadUrl("javascript:refresh()");
                    }
                }
            });
        }

        return this.mapView;

    }

}
