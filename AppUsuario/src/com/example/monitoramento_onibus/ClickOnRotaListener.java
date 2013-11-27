package com.example.monitoramento_onibus;

import org.json.JSONObject;

import android.content.Context;
import android.view.View.OnClickListener;

public abstract class ClickOnRotaListener implements OnClickListener {
    JSONObject jsonObject;
    Context context;
    String nomeRota;
    
    public ClickOnRotaListener(JSONObject jsonObject, Context context, String nomeRota) {
        this.jsonObject = jsonObject;
        this.context = context;
        this.nomeRota = nomeRota;
    }
};
