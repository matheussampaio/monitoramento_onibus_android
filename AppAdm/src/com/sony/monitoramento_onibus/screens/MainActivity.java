package com.sony.monitoramento_onibus.screens;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.screens.fugaRota.FugaRotaActivity;
import com.sony.monitoramento_onibus.screens.horarios.HorariosManagerActivity;
import com.sony.monitoramento_onibus.screens.horarios.RotaHorarioActivity;
import com.sony.monitoramento_onibus.screens.onibus.OnibusActivity;
import com.sony.monitoramento_onibus.screens.pontoonibus.PontoOnibusActivity;
import com.sony.monitoramento_onibus.screens.rotas.RotasActivity;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {
    private GoogleMap mMap;
    private final LatLng CAMPINA = new LatLng(-7.220629, -35.886168);

    private boolean ROTAS_REFRESHING = false;
    private boolean ONIBUS_REFRESHING = false;
    private boolean PONTO_ONIBUS_REFRESHING = false;
    private boolean REFRESHING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            if (this.mMap == null) {
                this.mMap = ((MapFragment) this.getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                // Check if we were successful in obtaining the map.
                if (this.mMap != null) {
                    // The Map is verified. It is now safe to manipulate the
                    // map.
                    this.configureMap();
                }
            }
        }

        final ImageButton imgBtn = (ImageButton) this
                .findViewById(R.id.imageButtonRefresh);

        imgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.this.refreshMap();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        this.refreshMap();
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
                final Intent intentRotas = new Intent(this,
                        RotaHorarioActivity.class);
                this.startActivity(intentRotas);
            }
            return true;
        case R.id.action_pontoonibus:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentPontoOnibus = new Intent(this,
                        PontoOnibusActivity.class);
                this.startActivity(intentPontoOnibus);
            }
            return true;
        case R.id.action_rotes:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentRotes = new Intent(this, RotasActivity.class);
                this.startActivity(intentRotes);
            }
            return true;
        case R.id.action_settings:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentSettings = new Intent(this,
                        SettingsActivity.class);
                this.startActivity(intentSettings);
            }
            return true;
        case R.id.action_onibus:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentOnibus = new Intent(this,
                        OnibusActivity.class);
                this.startActivity(intentOnibus);
            }
            return true;
        case R.id.action_gerenciar_horarios:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentGerenciarHorario = new Intent(this,
                        HorariosManagerActivity.class);
                this.startActivity(intentGerenciarHorario);
            }
            return true;
        case R.id.action_fugarotas:
            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
                final Intent intentRotes = new Intent(this,
                        FugaRotaActivity.class);
                this.startActivity(intentRotes);
            }
            return true;
        case R.id.action_help:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void configureMap() {
        if (this.mMap != null) {

            this.mMap.setMyLocationEnabled(true);
            this.mMap.getUiSettings().setZoomControlsEnabled(false);
            this.mMap.getUiSettings().setCompassEnabled(false);
            this.mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    this.CAMPINA, 15));

        }
    }

    public void refreshMap() {

        if (!this.REFRESHING) {
            MyLog.Info("Refreshing map...");

            this.REFRESHING = true;
            this.mMap.clear();

            if (!this.ROTAS_REFRESHING) {
                this.ROTAS_REFRESHING = true;
                this.fetchRotas();
            }

            if (!this.PONTO_ONIBUS_REFRESHING) {
                this.PONTO_ONIBUS_REFRESHING = true;
                this.fetchPontoOnibus();
            }

            if (!this.ONIBUS_REFRESHING) {
                this.ONIBUS_REFRESHING = true;
                this.startFetchOnibus();
            }

        } else {
            MyLog.Info("Map already refreshing...");
        }

    }

    public void startFetchOnibus() {
        new DownloadJSONTask(this)
                .execute(
                        "http://"
                                + Utils.getURLGeoserver()
                                + "/geoserver/wfs?service=wfs&version=2.0.0&request=GetFeature&outputFormat=application/json&typeNames=GO:lastlocalization",
                        "onibus");
    }

    public void fetchPontoOnibus() {
        new DownloadJSONTask(this)
                .execute(
                        "http://"
                                + Utils.getURLGeoserver()
                                + "/geoserver/wfs?service=wfs&version=2.0.0&request=GetFeature&outputFormat=application/json&typeNames=GO:pontoonibus",
                        "pontoonibus");
    }

    public void fetchRotas() {
        new DownloadJSONTask(this)
                .execute(
                        "http://"
                                + Utils.getURLGeoserver()
                                + "/geoserver/wfs?service=wfs&version=2.0.0&request=GetFeature&outputFormat=application/json&typeNames=GO:rota",
                        "rota");
    }

    public void callBackJSON(JSONObject mainObject, String feature) {
        if (this.mMap != null) {
            if (feature.equals("pontoonibus")) {
                this.handlerPontoOnibus(mainObject);
                this.PONTO_ONIBUS_REFRESHING = false;
            } else if (feature.equals("onibus")) {
                this.handlerOnibus(mainObject);
                this.ONIBUS_REFRESHING = false;
            } else if (feature.equals("rota")) {
                this.handlerRota(mainObject);
                this.ROTAS_REFRESHING = false;
            }

            if (!this.PONTO_ONIBUS_REFRESHING && !this.ONIBUS_REFRESHING
                    && !this.ROTAS_REFRESHING) {
                this.REFRESHING = false;
            }
        }
    }

    public void handlerPontoOnibus(JSONObject mainObject) {
        JSONArray features;

        try {
            features = mainObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                final JSONArray coords = (JSONArray) features.getJSONObject(i)
                        .getJSONObject("geometry").get("coordinates");

                Double lat = 0.0;
                Double lng = 0.0;

                if (coords.get(0) instanceof Integer) {
                    lat = ((Integer) coords.get(0)).doubleValue();
                } else {
                    lat = (Double) coords.get(0);
                }

                if (coords.get(0) instanceof Integer) {
                    lng = ((Integer) coords.get(1)).doubleValue();
                } else {
                    lng = (Double) coords.get(1);
                }

                final String title = features.getJSONObject(i)
                        .getJSONObject("properties").getString("nome");

                this.mMap
                        .addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(title)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handlerOnibus(JSONObject mainObject) {
        JSONArray features;

        try {
            features = mainObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                final JSONArray coords = (JSONArray) features.getJSONObject(i)
                        .getJSONObject("geometry").get("coordinates");

                final Double lat = (Double) coords.get(0);
                final Double lng = (Double) coords.get(1);

                final String title = features.getJSONObject(i)
                        .getJSONObject("properties").getString("placa");

                this.mMap
                        .addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(title)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }

        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void handlerRota(JSONObject mainObject) {
        JSONArray features;

        try {
            features = mainObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                final JSONArray coords = (JSONArray) features.getJSONObject(i)
                        .getJSONObject("geometry").get("coordinates");

                final String nome = features.getJSONObject(i)
                        .getJSONObject("properties").getString("nome");

                final PolylineOptions lineOptions = new PolylineOptions();

                lineOptions.color(Utils.getColor(nome));

                for (int j = 0; j < coords.length(); j++) {

                    final Double lat = (Double) coords.getJSONArray(j).get(0);
                    final Double lng = (Double) coords.getJSONArray(j).get(1);

                    lineOptions.add(new LatLng(lat, lng));
                }

                this.mMap.addPolyline(lineOptions.geodesic(true));

            }

        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
