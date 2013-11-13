package com.lgerenu.lurraldebus;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

public class MainActivity extends Activity {
	
	private LocationManager locManager;
	private LocationListener locListener;
	private DBHelper datuBasea;

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
				Toast.makeText(getApplicationContext(), Buff, Toast.LENGTH_LONG).show();
			}
		});
		
		/**
		 * Datu base sortzeko botoia
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
