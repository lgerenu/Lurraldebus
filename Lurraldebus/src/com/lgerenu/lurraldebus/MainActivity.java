package com.lgerenu.lurraldebus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.database.Cursor;

public class MainActivity extends Activity {

	private LocationManager locManager;
	private LocationListener locListener;
	private DBHelper datuBasea;

	/* Latitudean dagoen diferentziarik handiena geltoki hurbilena aurkitzeko */
	private static double MAX_LAT_DIFF = 0.05;
	/* Longitudean dagoen diferentziarik handiena geltoki hurbilena aurkitzeko */
	private static double MAX_LON_DIFF = 0.05;
	
	private Geltokia geltokiHurbilena;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// LocationManager-a erabiliko dugu GPS-a erabiltzeko
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locListener = new NireLocationListener();
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);

		/**
		 * Geltokia lortzeko botoia
		 */
		Button btnGeltokiaLortu = (Button) findViewById(R.id.btnGeltokiaLortu);
		btnGeltokiaLortu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String Buff = "Nire kokapena: Latitudea "+locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLatitude() +
						" Longitudea "+ locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLongitude();
				/* Datu basean, aukeratu hurbilen dauden geltokiak */
				double actLat = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLatitude();
				double actLon = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLongitude();
				Log.i("consola", "Datubasetik irakurri behar dugu...");
				List<Geltokia> geltokiak = datuBasea.geltokiakIrakurri(actLat+MAX_LAT_DIFF, actLat-MAX_LAT_DIFF, actLon+MAX_LON_DIFF, actLon-MAX_LON_DIFF);
				int geltokiKopurua = geltokiak.size();
				Log.i("consola", "Geltoki kopurua: "+geltokiKopurua);
				/* Bilatu hurbilen dagoen geltokia */
				int azkenDistantzia = 10000; // 10 Km.
				for(int i=0; i<geltokiKopurua; i++) {
					int distantzia = getDistance(actLat, actLon, geltokiak.get(i).getLat(), geltokiak.get(i).getLon()); 
					Log.i("consola", "Distantzia: "+distantzia);
					if (distantzia < azkenDistantzia) {
						azkenDistantzia = distantzia;
						Log.i("consola", "Orain arteko distantziarik motzena: "+azkenDistantzia);
						geltokiHurbilena = geltokiak.get(i);
					}
				}
				Toast.makeText(getApplicationContext(), "Geltoki hurbilena: "+geltokiHurbilena.getName(), Toast.LENGTH_LONG).show();
			}
		});

		/**
		 * Datu basea sortzeko botoia
		 */
		Button btnDatubaseaSortu = (Button) findViewById(R.id.btnDatubaseaSortu);
		btnDatubaseaSortu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datuBasea = new DBHelper(getApplicationContext());
				try {
					datuBasea.createDataBase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/**
		 * Datu base irekitzeko botoia
		 */
		Button btnDatubaseaIreki = (Button) findViewById(R.id.btnDatubaseaIreki);
		btnDatubaseaIreki.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datuBasea.openDataBase();
			}
		});
	}

	/**
	 * Bi punturen arteko distantzia kalkulatzen du.
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lon_b
	 * @return Distantzia metrotan.
	 */
	public static int getDistance(double lat_a, double lng_a, double lat_b, double lon_b){
		int Radius = 6371000; //Radio de la tierra
//		double lat1 = lat_a / 1E6;
//		double lat2 = lat_b / 1E6;
//		double lon1 = lng_a / 1E6;
//		double lon2 = lon_b / 1E6;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



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
