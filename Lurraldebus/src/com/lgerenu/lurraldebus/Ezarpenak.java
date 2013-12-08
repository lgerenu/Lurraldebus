package com.lgerenu.lurraldebus;

import android.content.Context;
import android.content.SharedPreferences;

public class Ezarpenak {

	private final String SHARED_PREFS_FILE = "LurraldebusEzarpenak";
	private final String KEY_HURRUNERA = "hurrunera";
	private final String KEY_HASIERA_ORDUA = "hasieraOrdua";
	private final String KEY_BUKAERA_ORDUA = "bukaeraOrdua";
	private Context mContext;

	public Ezarpenak(Context context) {
		mContext = context;
	}

	private SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	public int getHurrunera() {
		return getSettings().getInt(KEY_HURRUNERA, 10000);
	}

	public void setHurrunera(int hurrunera) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(KEY_HURRUNERA, hurrunera);
		editor.commit();
	}

	public int getHasieraOrdua() {
		return getSettings().getInt(KEY_HASIERA_ORDUA, 5);
	}

	public void setHasieraOrdua(int hasieraOrdua) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(KEY_HASIERA_ORDUA, hasieraOrdua);
		editor.commit();
	}

	public int getBukaeraOrdua() {
		return getSettings().getInt(KEY_BUKAERA_ORDUA, 60);
	}

	public void setBukaeraOrdua(int bukaeraOrdua) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(KEY_BUKAERA_ORDUA, bukaeraOrdua);
		editor.commit();
	}
}
