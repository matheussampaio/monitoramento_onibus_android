package com.sony.monitoramento_onibus.screens.fugaRota;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.model.FugaRota;

public class FugaRotaAdapter extends BaseAdapter {
	private final List<FugaRota> listFugaRota;
	private final LayoutInflater minflater;

	public FugaRotaAdapter(Context context, List<FugaRota> listFugaRota) {
		this.listFugaRota = listFugaRota;
		this.minflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return this.listFugaRota.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listFugaRota.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = this.minflater
				.inflate(R.layout.adapter_pontoonibus, null);
		FugaRota fugaRota = this.listFugaRota.get(position);

		TextView tv = (TextView) convertView
				.findViewById(R.id.textViewPontoOnibusNome);
		tv.setText("Placa:" + fugaRota.getPlacaOnibus() + " Início:"
				+ fugaRota.getInicio() + " Fim:" + fugaRota.getFim());

		return convertView;
	}
}