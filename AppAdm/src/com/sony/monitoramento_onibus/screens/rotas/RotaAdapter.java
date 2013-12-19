package com.sony.monitoramento_onibus.screens.rotas;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.Rota;

public class RotaAdapter extends BaseAdapter {
    private final List<Rota> listRotes;
    private final LayoutInflater minflater;

    public RotaAdapter(Context context, List<Rota> listRotes) {
        this.listRotes = listRotes;
        this.minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.listRotes.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listRotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.minflater.inflate(R.layout.adapter_rotes, null);
        final Rota rotes = this.listRotes.get(position);

        final TextView tv = (TextView) convertView
                .findViewById(R.id.textViewRotesNome);
        tv.setText(rotes.getNome());

        return convertView;
    }
}
