package com.lgerenu.lurraldebus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
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

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
