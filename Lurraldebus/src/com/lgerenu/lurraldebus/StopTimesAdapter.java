package com.lgerenu.lurraldebus;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StopTimesAdapter extends BaseAdapter {

  private List<StopTimes> geldiuneZerrenda;
  private LayoutInflater lInflater;

  public StopTimesAdapter(Context context, List<StopTimes> geldiuneak) {
    this.lInflater = LayoutInflater.from(context);
    this.geldiuneZerrenda = geldiuneak;
  }

  @Override
  public int getCount() {
    return geldiuneZerrenda.size();
  }

  @Override
  public Object getItem(int arg0) {
    return geldiuneZerrenda.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  @Override
  public View getView(int arg0, View arg1, ViewGroup arg2) {
    ContenedorView contenedor = null;
    if (arg1 == null) {
      arg1 = lInflater.inflate(R.layout.stop_times_layout, null);

      contenedor = new ContenedorView();
      contenedor.bidaia = (TextView) arg1.findViewById(R.id.bidaia);
      contenedor.ordua = (TextView) arg1.findViewById(R.id.izena);
      contenedor.hurrengoGeltokiak = (TextView) arg1.findViewById(R.id.hurrengoGeltokiak);

      arg1.setTag(contenedor);
    } else
      contenedor = (ContenedorView) arg1.getTag();

    StopTimes geldiuneak = (StopTimes) getItem(arg0);
    contenedor.bidaia.setText(geldiuneak.getRouteIzena());
    contenedor.bidaia.setTextColor(Color.GRAY);
    contenedor.ordua.setText(geldiuneak.getArrivalTime());
    contenedor.ordua.setTextColor(Color.BLACK);
    contenedor.ordua.setBackgroundColor(Color.GRAY);
    contenedor.hurrengoGeltokiak.setText(geldiuneak.getHurrengoGeltokiak());
    contenedor.hurrengoGeltokiak.setTextColor(Color.BLACK);

    return arg1;
  }

  class ContenedorView {
    TextView bidaia;
    TextView ordua;
    TextView hurrengoGeltokiak;
  }

}
