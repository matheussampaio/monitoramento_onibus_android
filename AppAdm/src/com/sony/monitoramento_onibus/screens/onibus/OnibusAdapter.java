package com.sony.monitoramento_onibus.screens.onibus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.Onibus;

public class OnibusAdapter extends BaseAdapter {
    private final List<Onibus> listOnibus;
    private final LayoutInflater minflater;

    public OnibusAdapter(Context context, List<Onibus> listOnibus) {
        this.listOnibus = listOnibus;
        this.minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.listOnibus.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listOnibus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.minflater.inflate(R.layout.adapter_onibus, null);
        Onibus Onibus = this.listOnibus.get(position);

        TextView tv = (TextView) convertView
                .findViewById(R.id.textViewOnibusNome);
        tv.setText(Onibus.getPlaca());

        return convertView;
    }
}
