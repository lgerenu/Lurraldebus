package com.lgerenu.lurraldebus;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class GeltokiakActivity extends Activity {

	private ListView listvGeltokiak;

	GeltokienZerrendaParcelabe geltokiZerrendaJasoa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geltokiak);

		Bundle container = getIntent().getExtras();
		geltokiZerrendaJasoa = container.getParcelable("array");

		// Geltokien zerrendaren trepeta lortu eta hustu
		listvGeltokiak = (ListView) findViewById(R.id.listvGeltokiak);
		listvGeltokiak.setAdapter(null);
		int size = geltokiZerrendaJasoa.size();
		List<Geltokia> geltokiZerrenda = new ArrayList<Geltokia>();
		geltokiZerrenda.clear();
		for (int i = 0; i < size; i++) {
			geltokiZerrenda.add(geltokiZerrendaJasoa.get(i));
		}
		GeltokiakAdapter geltokienAdapterra = new GeltokiakAdapter(
				getBaseContext(), geltokiZerrenda);
		geltokienAdapterra.getView(0, null, null);
		listvGeltokiak.setAdapter(geltokienAdapterra);
		listvGeltokiak.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.i("consola", "position = "+position+" id = "+id);
				Intent data = new Intent();
				data.setData(Uri.parse(String.valueOf(id)));
				setResult(RESULT_OK, data);
				Log.i("consola", "position String bezala = "+String.valueOf(id));
				finish();
			}
			
		}
				);

		/**
		 * Bueltatzeko botoia
		 */
		Button btnGeltokiakIrten = (Button) findViewById(R.id.btnGeltokiakIrten);
		btnGeltokiakIrten.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

}
