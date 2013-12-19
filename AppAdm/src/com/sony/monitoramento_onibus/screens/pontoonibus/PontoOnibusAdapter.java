package com.sony.monitoramento_onibus.screens.pontoonibus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.PontoOnibus;

public class PontoOnibusAdapter extends BaseAdapter {
    private final List<PontoOnibus> listPontoOnibus;
    private final LayoutInflater minflater;

    public PontoOnibusAdapter(Context context, List<PontoOnibus> listPontoOnibus) {
        this.listPontoOnibus = listPontoOnibus;
        this.minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.listPontoOnibus.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listPontoOnibus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.minflater
                .inflate(R.layout.adapter_pontoonibus, null);
        PontoOnibus pontoOnibus = this.listPontoOnibus.get(position);

        TextView tv = (TextView) convertView
                .findViewById(R.id.textViewPontoOnibusNome);
        tv.setText(pontoOnibus.getNome());

        return convertView;
    }
}
