package com.lgerenu.lurraldebus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "pesa.db";
	public final String databasePath = new String();

	private SQLiteDatabase datuBasea;

	private Context mContext;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	/**
	 * Datu basea sortu.
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		File pathFile = mContext.getDatabasePath(DATABASE_NAME);
		databasePath.valueOf(pathFile.getPath());
		Log.i("consola", "PATH: "+pathFile.getPath());
		boolean dbExist = checkDataBase(pathFile.getAbsolutePath());
		if(!dbExist) {
			Log.i("consola", "Datubasea ez da existitzen.");
			this.getReadableDatabase();
			try {
				copyDataBase(pathFile);
				Toast.makeText(mContext, "Datubasea sortu da", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// Error copying database
			}
		}
		else {
			Log.i("consola", "Datubasea existitzen da.");
		}
	}

	/**
	 * Datubasea existitzen den ikusi
	 * @param path Datu basearen kokaleku eta izena.
	 * @return Datua basea existitzen den edo ez.
	 */
	private boolean checkDataBase(String path) {
		SQLiteDatabase checkDB = null;
		try {           
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		} catch(Exception e){
			// Database doesn't exist
		}
		if(checkDB != null) {
			checkDB.close();
		}
		return checkDB != null;
	}

	/**
	 * Datubasea kopiatu.
	 * @param pathFile
	 * @throws IOException
	 */
	private void copyDataBase(File pathFile) throws IOException {
		InputStream myInput = mContext.getAssets().open("pesa.db");
		OutputStream myOutput = new FileOutputStream(pathFile);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Datu basea ireki
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLiteException{

		//Open the database
		String myPath = databasePath.toString();
		datuBasea = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		String buff = new String().valueOf(datuBasea.isOpen());
		Log.i("consola", "Datubasea irekita dago: "+buff);

	}

	/**
	 * Datu basea itxi
	 * @throws SQLException
	 */
	public void closeDataBase() throws SQLiteException{
		//Close the database
		datuBasea.close();
		Log.i("consola", "Datubasea itxita dago.");

	}

	/**
	 * Geltokien zerrenda bat lortzen da, latitude eta longitude tarte batean.
	 * @param maxLat
	 * @param minLat
	 * @param maxLon
	 * @param minLon
	 * @return
	 */
	public List<Geltokia> geltokiakIrakurri(double maxLat, double minLat, double maxLon, double minLon) {
		SQLiteDatabase db = getReadableDatabase();
		List<Geltokia> zerrendaGeltokiak = new ArrayList<Geltokia>();
		String query = "SELECT * FROM gtfs_stops WHERE stop_lat>"+minLat+" AND stop_lat<"+maxLat+" AND stop_lon>"+minLon+" AND stop_lon<"+maxLon;
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		do {
			Geltokia geltokia = new Geltokia(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4));
			zerrendaGeltokiak.add(geltokia);
		} while (c.moveToNext());
		db.close();
		c.close();
		return zerrendaGeltokiak;
	}

	/**
	 * @param geltokiaId
	 * @param maxOrduaSeg
	 * @param minOrduaSeg
	 * @return
	 */
	public List<StopTimes> geldialdiakIrakurri(int geltokiaId, int maxOrduaSeg, int minOrduaSeg) {
		//Gaurko data lortu
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String data = df.format(cal.getTime());
		Log.i("consola", "Gaurko data: "+data);

		//Geldialdiak lortu
		SQLiteDatabase db = getReadableDatabase();
		List<StopTimes> zerrendaGeldiuneak = new ArrayList<StopTimes>();
		String[] irakurtzekoDatuak = {"trip_id", "arrival_time", "departure_time", "stop_id", "stop_sequence", "arrival_time_seconds", "departure_time_seconds"};
		String baldintzak = "stop_id="+geltokiaId+" AND arrival_time_seconds>"+minOrduaSeg+" AND arrival_time_seconds<"+maxOrduaSeg;
		Cursor c = db.query("gtfs_stop_times", irakurtzekoDatuak, baldintzak, null, null, null, "arrival_time_seconds", null);
		c.moveToFirst();
		if(c.getCount() > 0) {
			do {
				int route_id = routeLortu(c.getInt(0));
				int service_id = serviceLortu(c.getInt(0)); 
				StopTimes geldiunea = new StopTimes(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6), route_id, routeIzenaLortu(route_id), service_id, "");
				// Begiratu ia gaurko bidaiak diren
				if(gaurDaBidaia(data, service_id)) {
					Log.i("consola", "Gaurko bidaia da.");
					zerrendaGeldiuneak.add(geldiunea);
				}
				else {
					Log.i("consola", "Ez da gaurko bidaia");
				}
			} while (c.moveToNext());
		}
		db.close();
		c.close();
		return zerrendaGeldiuneak;
	}

	public int routeLortu(int trip_id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"route_id"};
		String baldintzak = "trip_id="+trip_id;
		Cursor c = db.query("gtfs_trips", irakurtzekoDatuak, baldintzak, null, null, null, null, null);
		c.moveToFirst();
		int route_id = c.getInt(0);
		db.close();
		c.close();
		return route_id;
	}

	public int serviceLortu(int trip_id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"service_id"};
		String baldintzak = "trip_id="+trip_id;
		Cursor c = db.query("gtfs_trips", irakurtzekoDatuak, baldintzak, null, null, null, null, null);
		c.moveToFirst();
		int service_id = c.getInt(0);
		db.close();
		c.close();
		return service_id;
	}

	public String routeIzenaLortu(int route_id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"route_long_name"};
		String baldintzak = "route_id="+route_id;
		Cursor c = db.query("gtfs_routes", irakurtzekoDatuak, baldintzak, null, null, null, null, null);
		c.moveToFirst();
		String route_izena = c.getString(0);
		db.close();
		c.close();
		return route_izena;
	}

	public String stopIzenaLortu(int stop_id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"stop_name"};
		String baldintzak = "stop_id="+stop_id;
		Cursor c = db.query("gtfs_stops", irakurtzekoDatuak, baldintzak, null, null, null, null, null);
		c.moveToFirst();
		String stop_izena = c.getString(0);
		db.close();
		c.close();
		return stop_izena;
	}

	public boolean gaurDaBidaia(String gaurkoData, int service_id) {
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"service_id", "date"};
		String baldintzak = "service_id="+service_id+" AND date="+gaurkoData;
		Cursor c = db.query("gtfs_calendar_dates", irakurtzekoDatuak, baldintzak, null, null, null, null, null);
		c.moveToFirst();
		Log.i("consola", "Emaitza kopurua: "+c.getCount());
		if(c.getCount() > 0)
			return true;
		else
			return false;
	}

	public String hurrengoGeltokiakLortu(int trip_id, int stop_id) {
		String hurrengoGeltokiak = "";
		SQLiteDatabase db = getReadableDatabase();
		String[] irakurtzekoDatuak = {"stop_id", "stop_sequence"};
		String baldintzak = "trip_id="+trip_id;
		Cursor c = db.query("gtfs_stop_times", irakurtzekoDatuak, baldintzak, null, null, null, "stop_sequence", null);
		c.moveToFirst();
		boolean hurrengoaBai = false;
		do {
			if(c.getInt(0) == stop_id)
				hurrengoaBai = true;
			else if(hurrengoaBai) {
				String geltokiIzena = stopIzenaLortu(c.getInt(0));
				if(!hurrengoGeltokiak.isEmpty())
					hurrengoGeltokiak += "->"; 
				hurrengoGeltokiak += geltokiIzena;
			}
		} while(c.moveToNext());
		return hurrengoGeltokiak;	
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
