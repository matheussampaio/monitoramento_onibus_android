package com.sony.monitoramento_onibus.screens;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.sony.monitoramento_onibus.R;

public class RemoverDialog extends Dialog {
    private final Context context;

    public RemoverDialog(Context context) {
        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_dialog_remove);
    }

}
