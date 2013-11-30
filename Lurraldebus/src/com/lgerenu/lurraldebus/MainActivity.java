package com.lgerenu.lurraldebus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager locManager;
	private LocationListener locListener;
	private DBHelper datuBasea;

	/* Latitudean dagoen diferentziarik handiena geltoki hurbilena aurkitzeko */
	private static double MAX_LAT_DIFF = 0.05;
	/* Longitudean dagoen diferentziarik handiena geltoki hurbilena aurkitzeko */
	private static double MAX_LON_DIFF = 0.05;

	/* Oraingo ordutik zenbat segundu aurrera begiratuko diren autobusak */
	private static int MAX_BUS_STOP_TIME = 3600; // 1:00h
	/* Oraingo ordutik zenbat segundu atzera begiratuko diren autobusak */
	private static int MIN_BUS_STOP_TIME = 300; // 5min

	private TextView txtGeltokia;

	private Geltokia geltokiHurbilena;

	private ListView listvBidaiak;

	private List<StopTimes> geldiuneak; //Geltoki hurbilenean dauden geldiuneak ordu zehatz batzuen artean.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// LocationManager-a erabiliko dugu GPS-a erabiltzeko
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		List<String> zerrendaProviders = locManager.getAllProviders();
		Criteria req = new Criteria();
		req.setAccuracy(Criteria.ACCURACY_FINE);
		String providerOnena = locManager.getBestProvider(req, false);
		Log.i("consola", "Zerbitzari onena: "+providerOnena);
		locListener = new NireLocationListener();
		locManager.requestLocationUpdates(providerOnena, 0, 0, locListener);

		//Geltoki izenaren trepeta lortu
		txtGeltokia = (TextView)findViewById(R.id.txtGeltokia);

		//Bidaien zerrendaren trepeta lortu
		listvBidaiak = (ListView)findViewById(R.id.listvBidaiak);

		/*
		 * Sortu datu basea
		 */
		datuBasea = new DBHelper(getApplicationContext());
		try {
			datuBasea.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		/**
		 * Geltokia lortzeko botoia
		 */
		Button btnGeltokiaLortu = (Button) findViewById(R.id.btnGeltokiaLortu);
		btnGeltokiaLortu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/* Datu base ireki */
				datuBasea.openDataBase();
				/* Nire kokapena zehaztu */
				double actLat = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLatitude();
				double actLon = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLongitude();
				/* Datu basean hurbilen dauden geltokiak bilatu */
				List<Geltokia> geltokiak = datuBasea.geltokiakIrakurri(actLat+MAX_LAT_DIFF, actLat-MAX_LAT_DIFF,
						actLon+MAX_LON_DIFF, actLon-MAX_LON_DIFF);
				/* Datu basea itxi */
				datuBasea.close();
				/* Aukeratutako geltoki kopurua lortu */
				int geltokiKopurua = geltokiak.size();
				/* Geltokirik aurkitu denentz jakiteko aldagaia */
				boolean geltokiaAurkituta = false;
				/* Hurbilen dagoen geltokia aukeratu */
				int azkenDistantzia = 10000; // 10 Km.
				for(int i=0; i<geltokiKopurua; i++) {
					int distantzia = getDistance(actLat, actLon,
							geltokiak.get(i).getLat(), geltokiak.get(i).getLon()); 
					if (distantzia < azkenDistantzia) {
						azkenDistantzia = distantzia;
						geltokiHurbilena = geltokiak.get(i);
						geltokiaAurkituta = true; // Geltoki bat behintzat...
					}
				}
				if(geltokiaAurkituta) {
					txtGeltokia.setText(geltokiHurbilena.getName());
					/* Geltoki horri dagozkion geldiuneak aurkitu */
					bkgndHurrengoGeltokiakAurkitu task1 = new bkgndHurrengoGeltokiakAurkitu();
					int[] geltokiHurbilenaId = {0};
					geltokiHurbilenaId[0] = geltokiHurbilena.getId();
					task1.execute(geltokiHurbilenaId[0]);
				}
				else {
					txtGeltokia.setText("Ez dago geltokirik inguruan...");
				}
			}
		});

	}


	/**
	 * Tarea asinkronoa. Geltokiak lortzeko kodea hemen dago, tarea
	 * printzipala asko luzatu ez dadin.
	 * @author lander
	 *
	 */
	private class bkgndHurrengoGeltokiakAurkitu extends AsyncTask<Integer, Void, List<StopTimes>>
	{
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<StopTimes> doInBackground(Integer... geltokiaId) {
			int geltokiHurbilenareId = geltokiaId[0];
			/* Datu base ireki */
			datuBasea.openDataBase();
			/* Uneko ordua eta eguna lortu */
			Calendar dataOrdua = Calendar.getInstance();
			int orduaSegundutan = getDayTimeSeconds(dataOrdua.get(dataOrdua.HOUR_OF_DAY), dataOrdua.get(dataOrdua.MINUTE), dataOrdua.get(dataOrdua.SECOND));
			//orduaSegundutan = 46800; // 13:00:00ak direla simulatzeko
			//orduaSegundutan = 0; // 00:00:00ak direla simulatzeko
			/* Geltoki honetako geldiuneak atera */
			List<StopTimes> geldiuneak = datuBasea.geldialdiakIrakurri(geltokiHurbilenareId, orduaSegundutan+MAX_BUS_STOP_TIME, orduaSegundutan-MIN_BUS_STOP_TIME);
			int geldiuneKopurua = geldiuneak.size();
			Log.i("consola", "Geldiune kopurua: "+geldiuneKopurua);
			/* Datu basea itxi */
			datuBasea.close();
			return geldiuneak;
		}

		@Override
		protected void onPostExecute(List<StopTimes> geldiuneak) {

			if(geldiuneak.size() > 0) {
				StopTimesAdapter geldiuneAdapter = new StopTimesAdapter(getApplicationContext(), geldiuneak);
				geldiuneAdapter.getView(0, null, null);
				listvBidaiak.setAdapter(geldiuneAdapter);
				Iterator<StopTimes> iter = geldiuneak.iterator();
				Log.i("consola", "Hurrengo geltokiak bilatzen...");
				for(int i=0; i<geldiuneak.size(); i++) {
					Log.i("consola", "i="+i+" trip_id="+geldiuneak.get(i).getId()+" stop_id="+geldiuneak.get(i).getStopId());
					String buff = hurrengoGeltokiakLortu(geldiuneak.get(i).getId(), geldiuneak.get(i).getStopId());
					geldiuneak.get(i).setHurrengoGeltokiak(buff);
				}
				geldiuneAdapter.getView(0, null, null);
				listvBidaiak.setAdapter(geldiuneAdapter);
			}
		}
	}

	/**
	 * Ruta bat emanda, geltoki baten hurrengo geltokiak bilatu.
	 * @param trip_id
	 * @param stop_id
	 */
	private String hurrengoGeltokiakLortu(int trip_id, int stop_id) {
		String hurrengoGeltokiak;
		/* Datu base ireki */
		datuBasea.openDataBase();
		hurrengoGeltokiak = datuBasea.hurrengoGeltokiakLortu(trip_id, stop_id);
		/* Datu basea itxi */
		datuBasea.close();
		return hurrengoGeltokiak;
	}

	/**
	 * Eguneko ordua segundutan ematen du (gauerditik pasa diren segunduak).
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return Gauerditik pasatu den segundu kopurua.
	 */
	public int getDayTimeSeconds(int hours, int minutes, int seconds) {
		int minutuakGuztira = hours*60 + minutes;
		int segunduakGuztira = minutuakGuztira*60 + seconds;
		return segunduakGuztira;		
	}

	/**
	 * Bi punturen arteko distantzia kalkulatzen du.
	 * @param lat_a A puntuaren latitudea.
	 * @param lng_a A puntuaren longitudea.
	 * @param lat_b B puntuaren latitudea.
	 * @param lon_b B puntuaren longitudea.
	 * @return Distantzia metrotan.
	 */
	public static int getDistance(double lat_a, double lng_a, double lat_b, double lon_b){
		int Radius = 6371000; //Radio de la tierra
		double lat1 = lat_a;
		double lat2 = lat_b;
		double lon1 = lng_a;
		double lon2 = lon_b;
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon /2) * Math.sin(dLon/2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return (int) (Radius * c);  

	}

	/**
	 * Aplikazio honi buruzko lehioa ateratzen du pantailan.
	 */
	public void showHelp() {
		FragmentManager fragmentManager = getFragmentManager();
		HoniBuruzLehioa honiBuruz = new HoniBuruzLehioa();
		honiBuruz.show(fragmentManager, "tag_alerta");
	}

	/**
	 * Aplikazio honi buruzko lehioa definitzen duen klasea.
	 * @author lander
	 *
	 */
	public class HoniBuruzLehioa extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder =
					new AlertDialog.Builder(getActivity());

			builder.setMessage("Hurbilen dagoen geltokia lortu eta hurrengo autobusak zeintzuk diren erakusten du.")
			.setTitle("Honi buruz...")
			.setPositiveButton("Ados", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			return builder.create();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			//	            newGame();
			return true;
		case R.id.action_honiburuz:
			showHelp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * GPSa erabili ahal izateko klasea.
	 * @author lander
	 *
	 */
	public class NireLocationListener implements LocationListener
	{
		public void onLocationChanged(Location loc)

		{
			loc.getLatitude();
			loc.getLongitude();
		}
		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(),"GPSa ez dago erabilgarri",Toast.LENGTH_SHORT ).show();
		}
		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(),"GPSa erabilgarri dago",Toast.LENGTH_SHORT ).show();
		}
		public void onStatusChanged(String provider, int status, Bundle extras){}

	}
}
