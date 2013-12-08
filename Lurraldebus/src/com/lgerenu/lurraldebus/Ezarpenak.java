package com.lgerenu.lurraldebus;

import android.content.Context;
import android.content.SharedPreferences;

public class Ezarpenak {

	private final String SHARED_PREFS_FILE = "LurraldebusEzarpenak";
	private final String KEY_HURRUNERA = "hurrunera";
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

	public void setUserEmail(int hurrunera) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(KEY_HURRUNERA, hurrunera);
		editor.commit();
	}
}
