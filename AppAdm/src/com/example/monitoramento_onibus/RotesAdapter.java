package com.example.monitoramento_onibus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.monitoramento_onibus.core.Rotes;

public class RotesAdapter extends BaseAdapter {
    private List<Rotes> listRotes;
    private LayoutInflater minflater;
    
    public RotesAdapter(Context context, List<Rotes> listRotes) {
        this.listRotes = listRotes;
        this.minflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listRotes.size();
    }

    @Override
    public Object getItem(int position) {
        return listRotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = minflater.inflate(R.layout.adapter_rotes, null);
        Rotes rotes = listRotes.get(position);
        
        TextView tv = (TextView) convertView.findViewById(R.id.textViewRotesNome);
        tv.setText(rotes.getNome());
        
        return convertView;
    }
}
