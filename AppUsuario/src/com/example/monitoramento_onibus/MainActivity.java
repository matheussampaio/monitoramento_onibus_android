package com.example.monitoramento_onibus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    
    private WebView mapView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (!Utils.isConnected((Activity) this)) {
            showNotConnected();
        }
        
        startMapView();
        
    }
    
    public WebView startMapView() {
    	
    	if (mapView == null) {
    		mapView = (WebView) findViewById(R.id.webView_Map);
            
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            
            mapView.addJavascriptInterface(new JavaScriptService(size.x, size.y), "mainService");
            
            mapView.getSettings().setJavaScriptEnabled(true);
            
            mapView.loadUrl("file:///android_asset/index.html");
            
            ImageButton btn = (ImageButton) findViewById(R.id.imageButton1);
            
            btn.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    mapView.loadUrl("javascript:refresh()");
                }
            });
    	}
    	
    	return mapView;
    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_horarios:
                Intent intentHorarios = new Intent(this, RotasActivity.class);
                startActivityForResult(intentHorarios, SHOW_SUBACTIVITY_HORARIOS);
                return true;
            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivityForResult(intentSettings, SHOW_SUBACTIVITY_SETTINGS);
                return true;
            case R.id.action_help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void showNotConnected() {
        AlertDialog alerta;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        builder.setTitle(getString(R.string.error)); 
        
        builder.setMessage(getString(R.string.you_must_connected)); 
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                moveTaskToBack(true);
            }
        }); 
        
        alerta = builder.create(); 
        alerta.show();
    }
    
}
