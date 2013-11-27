package com.example.monitoramento_onibus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.monitoramento_onibus.core.PontoOnibus;

public class PontoOnibusAdapter extends BaseAdapter {
    private List<PontoOnibus> listPontoOnibus;
    private LayoutInflater minflater;
    
    public PontoOnibusAdapter(Context context, List<PontoOnibus> listPontoOnibus) {
        this.listPontoOnibus = listPontoOnibus;
        this.minflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listPontoOnibus.size();
    }

    @Override
    public Object getItem(int position) {
        return listPontoOnibus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = minflater.inflate(R.layout.adapter_pontoonibus, null);
        PontoOnibus pontoOnibus = listPontoOnibus.get(position);
        
        TextView tv = (TextView) convertView.findViewById(R.id.textViewPontoOnibusNome);
        tv.setText(pontoOnibus.getNome());
        
        return convertView;
    }
}
