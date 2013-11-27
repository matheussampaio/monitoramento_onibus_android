package com.example.monitoramento_onibus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.monitoramento_onibus.core.Horario;

public class HorarioAdapter extends BaseAdapter {
    private List<Horario> listHorario;
    private LayoutInflater minflater;
    
    public HorarioAdapter(Context context, List<Horario> listHorario) {
        this.listHorario = listHorario;
        this.minflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listHorario.size();
    }

    @Override
    public Object getItem(int position) {
        return listHorario.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = minflater.inflate(R.layout.adapter_horarios, null);
        Horario horario = listHorario.get(position);
        
        TextView tvPontoOnibus = (TextView) convertView.findViewById(R.id.textViewPontoOnibus2);
        tvPontoOnibus.setText(String.valueOf(horario.getPontoOnibus()));
        
        TextView tvHorarios = (TextView) convertView.findViewById(R.id.textViewHorario2);
        tvHorarios.setText(horario.getHorario());

        return convertView;
    }

}
