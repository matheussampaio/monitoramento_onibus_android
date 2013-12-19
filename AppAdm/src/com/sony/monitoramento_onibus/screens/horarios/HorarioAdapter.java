package com.sony.monitoramento_onibus.screens.horarios;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.Horario;

public class HorarioAdapter extends BaseAdapter {
    private final List<Horario> listHorario;
    private final LayoutInflater minflater;

    public HorarioAdapter(Context context, List<Horario> listHorario) {
        this.listHorario = listHorario;
        this.minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.listHorario.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listHorario.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.minflater.inflate(R.layout.adapter_horarios, null);
        Horario horario = this.listHorario.get(position);

        TextView tvPontoOnibus = (TextView) convertView
                .findViewById(R.id.textViewPontoOnibus2);
        tvPontoOnibus.setText(String.valueOf(horario.getPontoOnibus()));

        TextView tvHorarios = (TextView) convertView
                .findViewById(R.id.textViewHorario2);
        tvHorarios.setText(horario.getHorario());

        return convertView;
    }

}
