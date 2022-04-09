import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context myContext;
    private static String DB_NAME = "dbname.db";
    private static String DB_PATH = "";
    private static int DATABASE_VERSION = 1;
    public SQLiteDatabase myDatabase;

    public Cursor getRows(String query) {
        myDatabase = this.getReadableDatabase();
        String Query = query;
        Cursor CR = myDatabase.rawQuery(Query, null);
        return CR;
    }


    private int getCount(String query) {
        myDatabase = this.getReadableDatabase();
        Cursor CR = myDatabase.rawQuery(query, null);
        return CR.getCount();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i > i1) {
            Log.v("Database Upgrade", "Database version higher than old.");
            deleteDatabase();
        }
    }

    public DatabaseHelper(Context context) throws IOException {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        boolean dbExists = checkDatabase();
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        if (dbExists) {
            Log.d("DB_LOG", "Database bulundu !");
        } else {
            try {
                if (createDatabase() == true) {
                    Log.d("DB_LOG", "Database oluşturuldu !");
                } else {
                    Log.d("DB_LOG", "Database oluşturulamadı !");
                }
            } catch (Exception e) {
                Log.d("DB_LOG", "Database oluşturulamadı !");
            }
        }

    }

    public boolean createDatabase() throws IOException {
        boolean dbExists = checkDatabase();
        // checkDatabase metodu ile database varmı/yokmu kontrolü yap
        if (dbExists) { //database varsa
            return true;
        } else { // database yoksa
            this.getReadableDatabase();
            try {
                this.close();
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Database kopyalanma hatası");
            }
            return false;
        }
    }

    public boolean checkDatabase() {
        boolean checkdb = false;

        try {
            String dosyaKonumu = DB_PATH + DB_NAME;
            File dbFile = new File(dosyaKonumu);
            checkdb = dbFile.exists();
        } catch (SQLiteException e) {
            Log.d("DB_LOG", "Database bulunamadı");
        }

        return checkdb;
    }

    private void copyDatabase() throws IOException {
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void openDatabase() {
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void deleteDatabase() {
        File file = new File(DB_PATH + DB_NAME);
        if (file.exists()) {
            file.delete();
            if (file.delete() == true) {
                Log.d("DB_LOG", "Database file deleted on apk in database file");
            } else {
                Log.d("DB_LOG", "Database file do not deleted !");
            }
        }
    }

    public synchronized void close() {
        if (myDatabase != null) {
            myDatabase.close();
        }
        super.close();
    }

}
