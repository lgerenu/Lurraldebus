package com.lgerenu.lurraldebus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

  private TextView txtGeltokia;

  private Geltokia geltokiHurbilena;

  private ListView listvBidaiak;
  
  private ProgressDialog itxaronLehioa; /* Itxaron arazteko lehioa */

  /* Ezarpenak */
  private Ezarpenak ezarpenak; // Ezarpenak gordetzeko klasea
  private int hurrunera; // Geltoki hurbilenak bilatzeko gehiengo distantzia
  private int hasieraOrdua; // AUtobusak bilatzen hasteko oraingo ordua baino
  // zenbat minutu lehenago hasi
  private int bukaeraOrdua; // AUtobusak bilatzen bukatzeko oraingo ordua
  // baino zenbat minutu beranduago hasi

  private List<StopTimes> geldiuneak; // Geltoki hurbilenean dauden geldiuneak
  // ordu zehatz batzuen artean.

  private boolean geltokiaAurkituta = false; // Geltokirik aurkitu denentz
  // jakiteko aldagaia

  private List<Geltokia> geltokiak; // Hurbil dauden geltokien zerrenda

  private GeltokienZerrendaParcelabe geltokiZerrendaBidaltzeko = new GeltokienZerrendaParcelabe();
  private int requestCode = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // LocationManager-a erabiliko dugu GPS-a erabiltzeko
    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    List<String> zerrendaProviders = locManager.getAllProviders();
    Criteria req = new Criteria();
    req.setAccuracy(Criteria.ACCURACY_FINE);
    String providerOnena = locManager.getBestProvider(req, false);
    Log.i("consola", "Zerbitzari onena: " + providerOnena);
    locListener = new NireLocationListener();
    locManager.requestLocationUpdates(providerOnena, 0, 0, locListener);

    // Geltoki izenaren trepeta lortu
    txtGeltokia = (TextView) findViewById(R.id.txtGeltokia);

    // Bidaien zerrendaren trepeta lortu
    listvBidaiak = (ListView) findViewById(R.id.listvBidaiak);

    /*
     * Ezarpenak
     */
    ezarpenak = new Ezarpenak(this);
    // Gordetako datuak lortu, baldin badaude
    if (ezarpenak.getHurrunera() != 0)
      hurrunera = Integer.valueOf(ezarpenak.getHurrunera());
    if (ezarpenak.getHasieraOrdua() != 0)
      hasieraOrdua = Integer.valueOf(ezarpenak.getHasieraOrdua());
    if (ezarpenak.getBukaeraOrdua() != 0)
      bukaeraOrdua = Integer.valueOf(ezarpenak.getBukaeraOrdua());

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

    /* Geltoki hurbilena lortu eta bertatik pasatzen diren autobusak bilatu. */
    geltokiaLortu();

    /**
     * Geltokia lortzeko botoia
     */
    Button btnGeltokiaLortu = (Button) findViewById(R.id.btnGeltokiaLortu);
    btnGeltokiaLortu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        geltokiaLortu();
      }
    });

  }

  /**
   * Geltoki hurbilena lortu eta bertatik pasatzen diren autobusak bilatzen
   * ditu.
   */
  public void geltokiaLortu() {
    /* Botoia ezgaitu */
    Button btnGeltokiaLortu = (Button) findViewById(R.id.btnGeltokiaLortu);
    btnGeltokiaLortu.setEnabled(false);
    /* Zerrenda hustu */
    listvBidaiak.setAdapter(null);
    /* Nire kokapena zehaztu */
    double actLat = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLatitude();
    double actLon = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER).getLongitude();
    /* Datu basean hurbilen dauden geltokiak bilatu */
    geltokiHurbilenakBilatu(actLat, actLon);
    if (geltokiaAurkituta) {
      autobusakBilatu();
    } else {
      /* Gaitu berriro botoia */
      btnGeltokiaLortu.setEnabled(true);
      txtGeltokia.setText("Ez dago geltokirik inguruan...");
    }
  }

  /**
   * Activity-a berriro hastean, gordetako datuak berreskuratu.
   */
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    // Gordetako datuak lortu, baldin badaude
    if (ezarpenak.getHurrunera() != 0)
      hurrunera = Integer.valueOf(ezarpenak.getHurrunera());
    if (ezarpenak.getHasieraOrdua() != 0)
      hasieraOrdua = Integer.valueOf(ezarpenak.getHasieraOrdua());
    if (ezarpenak.getBukaeraOrdua() != 0)
      bukaeraOrdua = Integer.valueOf(ezarpenak.getBukaeraOrdua());

  }

  /**
   * Tarea asinkronoa. Geltokiak lortzeko kodea hemen dago, tarea printzipala
   * asko luzatu ez dadin.
   * 
   * @author lander
   * 
   */
  private class bkgndHurrengoGeltokiakAurkitu extends AsyncTask<Integer, Void, List<StopTimes>> {
    @Override
    protected void onPreExecute() {
      /* Itxaron lehioa sortu eta erakutsi */
      itxaronLehioa = new ProgressDialog(MainActivity.this);
      itxaronLehioa.setMessage("Geltoki eta autobusak bilatzen.\nItxaron, mesedez...");
      itxaronLehioa.setCancelable(false);
      itxaronLehioa.setIndeterminate(true);
      itxaronLehioa.show();

    }

    @Override
    protected List<StopTimes> doInBackground(Integer... geltokiaId) {
      int geltokiHurbilenareId = geltokiaId[0];
      /* Datu base ireki */
      datuBasea.openDataBase();
      /* Uneko ordua eta eguna lortu */
      Calendar dataOrdua = Calendar.getInstance();
      int orduaSegundutan = getDayTimeSeconds(dataOrdua.get(dataOrdua.HOUR_OF_DAY),
          dataOrdua.get(dataOrdua.MINUTE), dataOrdua.get(dataOrdua.SECOND));
      // orduaSegundutan = 46800; // 13:00:00ak direla simulatzeko
      // orduaSegundutan = 0; // 00:00:00ak direla simulatzeko
      /* Geltoki honetako geldiuneak atera */
      Log.i("consola", "Hasiera ordua: " + hasieraOrdua + " Bukaera ordua: " + bukaeraOrdua
          + " Ordua segundotan: " + orduaSegundutan);
      List<StopTimes> geldiuneak = datuBasea.geldialdiakIrakurri(geltokiHurbilenareId,
          orduaSegundutan + bukaeraOrdua * 60, orduaSegundutan - hasieraOrdua * 60);
      int geldiuneKopurua = geldiuneak.size();
      Log.i("consola", "Geldiune kopurua: " + geldiuneKopurua);
      /* Datu basea itxi */
      datuBasea.close();
      return geldiuneak;
    }

    @Override
    protected void onPostExecute(List<StopTimes> geldiuneak) {

      if (geldiuneak.size() > 0) {
        StopTimesAdapter geldiuneAdapter = new StopTimesAdapter(getApplicationContext(), geldiuneak);
        geldiuneAdapter.getView(0, null, null);
        listvBidaiak.setAdapter(geldiuneAdapter);
        for (int i = 0; i < geldiuneak.size(); i++) {
          String buff = hurrengoGeltokiakLortu(geldiuneak.get(i).getId(), geldiuneak.get(i)
              .getStopId());
          geldiuneak.get(i).setHurrengoGeltokiak(buff);
        }
        geldiuneAdapter.getView(0, null, null);
        listvBidaiak.setAdapter(geldiuneAdapter);
      } else {
        Toast.makeText(getApplicationContext(), "Ez da autobusik aurkitu", Toast.LENGTH_LONG)
            .show();
      }
      /* Gaitu berriro botoia */
      Button btnGeltokiaLortu = (Button) findViewById(R.id.btnGeltokiaLortu);
      btnGeltokiaLortu.setEnabled(true);
      /* Itxaron lehioa ezkutatu */
      itxaronLehioa.dismiss();
    }
  }

  /**
   * Geltoki batetik pasatzen diren autobusak bilatu.
   */
  private void autobusakBilatu() {
    if (geltokiHurbilena.getDesc() != null)
      txtGeltokia.setText(geltokiHurbilena.getDesc());
    else
      txtGeltokia.setText(geltokiHurbilena.getName());
    /* Zerrenda garbitu */
    listvBidaiak.setAdapter(null);
    /* Geltoki horri dagozkion geldiuneak aurkitu */
    bkgndHurrengoGeltokiakAurkitu task1 = new bkgndHurrengoGeltokiakAurkitu();
    int[] geltokiHurbilenaId = { 0 };
    geltokiHurbilenaId[0] = geltokiHurbilena.getId();
    task1.execute(geltokiHurbilenaId[0]);
  }

  /**
   * Kokapen bat emanda, inguruko geltokiak aurkitu.
   * 
   * @param actLat
   *          Uneko latitudea
   * @param actLon
   *          Uneko longitudea
   */
  private void geltokiHurbilenakBilatu(double actLat, double actLon) {
    datuBasea.openDataBase();
    geltokiak = datuBasea.geltokiakIrakurri();
    datuBasea.close();
    /* Aukeratutako geltoki kopurua lortu */
    int geltokiKopurua = geltokiak.size();
    /* Hurbilen dagoen geltokia aukeratu */
    int azkenDistantzia = hurrunera;
    geltokiZerrendaBidaltzeko.clear();
    for (int i = 0; i < geltokiKopurua; i++) {
      int distantzia = getDistance(actLat, actLon, geltokiak.get(i).getLat(), geltokiak.get(i)
          .getLon());
      geltokiak.get(i).setDistantzia(distantzia);
      if (distantzia < hurrunera)
        geltokiZerrendaBidaltzeko.add(geltokiak.get(i));
      if (distantzia < azkenDistantzia) {
        azkenDistantzia = distantzia;
        geltokiHurbilena = geltokiak.get(i);
        geltokiaAurkituta = true; // Geltoki bat behintzat...
      }
    }
  }

  /**
   * Ruta bat emanda, geltoki baten hurrengo geltokiak bilatu.
   * 
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
   * 
   * @param hours
   * @param minutes
   * @param seconds
   * @return Gauerditik pasatu den segundu kopurua.
   */
  public int getDayTimeSeconds(int hours, int minutes, int seconds) {
    int minutuakGuztira = hours * 60 + minutes;
    int segunduakGuztira = minutuakGuztira * 60 + seconds;
    return segunduakGuztira;
  }

  /**
   * Bi punturen arteko distantzia kalkulatzen du.
   * 
   * @param lat_a
   *          A puntuaren latitudea.
   * @param lng_a
   *          A puntuaren longitudea.
   * @param lat_b
   *          B puntuaren latitudea.
   * @param lon_b
   *          B puntuaren longitudea.
   * @return Distantzia metrotan.
   */
  public static int getDistance(double lat_a, double lng_a, double lat_b, double lon_b) {
    int Radius = 6371000; // Radio de la tierra
    double lat1 = lat_a;
    double lat2 = lat_b;
    double lon1 = lng_a;
    double lon2 = lon_b;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
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
   * Ezarpenetara joateko funtzioa.
   */
  public void gotoSettings() {
    Intent i = new Intent(this, SettingsActivity.class);
    startActivity(i);
  }

  /**
   * Geltokien activity-ra joateko funtzioa.
   */
  public void gotoGeltokiak() {
    Intent i = new Intent(this, GeltokiakActivity.class);
    Bundle container = new Bundle();
    container.putParcelable("array", geltokiZerrendaBidaltzeko);
    i.putExtras(container);
    // startActivity(i);
    startActivityForResult(i, requestCode);
  }

  /**
   * GeltokiakActivity-tik bueltatzean, hango informazioa jasotzeko.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    int i = Integer.parseInt(data.getDataString());
    Log.i("consola", "Geltokietako emaitza = " + i);
    /* Emaitzarik ez bada bueltatu, zenbaki negatibo bat itzuliko da */
    if (i >= 0) {
      geltokiHurbilena = geltokiZerrendaBidaltzeko.get(i);
      autobusakBilatu();
    }
  }

  /**
   * Aplikazio honi buruzko lehioa definitzen duen klasea.
   * 
   * @author lander
   * 
   */
  public class HoniBuruzLehioa extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

      builder
          .setMessage(
              "Hurbilen dagoen geltokia lortu eta hurrengo autobusak zeintzuk diren erakusten du.")
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
        gotoSettings();
        return true;
      case R.id.action_geltokiak:
        gotoGeltokiak();
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
   * 
   * @author lander
   * 
   */
  public class NireLocationListener implements LocationListener {
    public void onLocationChanged(Location loc)

    {
      loc.getLatitude();
      loc.getLongitude();
    }

    public void onProviderDisabled(String provider) {
      Toast.makeText(getApplicationContext(), "GPSa ez dago erabilgarri", Toast.LENGTH_SHORT)
          .show();
    }

    public void onProviderEnabled(String provider) {
      Toast.makeText(getApplicationContext(), "GPSa erabilgarri dago", Toast.LENGTH_SHORT).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

  }
}
