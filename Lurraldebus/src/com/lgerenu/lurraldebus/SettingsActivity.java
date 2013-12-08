package com.lgerenu.lurraldebus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	/* Ezarpenak */
	private Ezarpenak ezarpenak; // Ezarpenak gordetzeko klasea

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		/*
		 * Ezarpenak
		 */
		ezarpenak = new Ezarpenak(this);
		//Gordetako datuak lortu, baldin badaude

		/*
		 * Ezarpeneko datuak lortu eta bere lekuan jarri
		 */
		final EditText editHurrunera = (EditText) findViewById(R.id.editHurrunera);
		editHurrunera.setText(String.valueOf(ezarpenak.getHurrunera()));

		/*
		 * Ezarpenak gordetzeko botoia
		 */
		Button btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);
		btnSaveSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Ezarpenak gorde
				 */
				String hurrunera = editHurrunera.getText().toString();
				ezarpenak.setUserEmail(Integer.valueOf(hurrunera));

				/*
				 * Activity-a bukatu
				 */
				finish();
			}
		});


		/*
		 * Ezarpenak ez gordetzeko botoia
		 */
		Button btnNoSaveSettings = (Button) findViewById(R.id.btnNoSaveSettings);
		btnNoSaveSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Activity-a bukatu
				 */
				finish();
			}
		});
	}
}
