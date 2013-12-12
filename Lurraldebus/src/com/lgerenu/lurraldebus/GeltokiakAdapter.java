package com.lgerenu.lurraldebus;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeltokiakAdapter extends BaseAdapter {

	private List<Geltokia> geltokiZerrenda;
	private LayoutInflater lInflater;

	public GeltokiakAdapter(Context context, List<Geltokia> geltokiak) {
		this.lInflater = LayoutInflater.from(context);
		this.geltokiZerrenda = geltokiak;
	}

	@Override
	public int getCount() {
		return geltokiZerrenda.size();
	}

	@Override
	public Object getItem(int arg0) {
		return geltokiZerrenda.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ContenedorView contenedor = null;
		if (arg1 == null){
			arg1 = lInflater.inflate(R.layout.geltokiak_layout, null);

			contenedor = new ContenedorView();
			contenedor.izena = (TextView) arg1.findViewById(R.id.izena);
			contenedor.deskribapena = (TextView) arg1.findViewById(R.id.deskribapena);
			contenedor.distantzia = (TextView) arg1.findViewById(R.id.distantzia);

			arg1.setTag(contenedor);
		} else
			contenedor = (ContenedorView) arg1.getTag();

		Geltokia geltokiak = (Geltokia) getItem(arg0);
		contenedor.izena.setText(geltokiak.getName());
		contenedor.deskribapena.setText(geltokiak.getDesc());
		String strTemp = String.valueOf(geltokiak.getDistantzia());
		contenedor.distantzia.setText(strTemp+" metro");

		return arg1;
	}


	class ContenedorView {
		TextView izena;
		TextView deskribapena;
		TextView distantzia;
	}

}