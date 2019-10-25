package au.edu.sydney.comp5216.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * The type Database helper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * The constant DATABASE_NAME.
     */
    public static final String DATABASE_NAME = "mylist.db";
    /**
     * The constant TABLE_NAME.
     */
    public static final String TABLE_NAME = "mylist_data";
    /**
     * The constant COL1 of the Database.
     * COL1 stores the ID of the item.
     */
    public static final String COL1 = "ID";
    /**
     * The constant COL2 of the Database.
     * COL1 stores the Date of the item.
     */
    public static final String COL2 = "DATE";
    /**
     * The constant COL3 of the Database.
     * COL1 stores the Distance of the item.
     */
    public static final String COL3 = "DISTANCE";
    /**
     * The constant COL4 of the Database.
     * COL1 stores the Time of the item.
     */
    public static final String COL4 = "TIME";
    /**
     * The constant COL5 of the Database.
     * COL1 stores the Pace of the item.
     */
    public static final String COL5 = "PACE";
    /**
     * The constant COL6 of the Database.
     * COL1 stores the Speed of the item.
     */
    public static final String COL6 = "SPEED";


    /**
     * Instantiates a new Database helper.
     *
     * @param context the context of the java class that call DatabaseHelper
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * This onCreate function will be implemented once DatabaseHelper is initialise in other class.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // This function will be called once when mobile app is installed.
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " DATE TEXT, "
                + " DISTANCE TEXT,"
                + " TIME TEXT,"
                + " PACE TEXT,"
                + " SPEED TEXT)";

        db.execSQL(createTable);
    }

    /**
     * This onUpgrade function will be called to delete the Database.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Add data will return boolean to check whether addData function run successfully or not.
     *
     * @param date     the date
     * @param distance the distance
     * @param time     the time
     * @param pace     the pace
     * @param speed    the speed
     * @return the boolean
     */
    public boolean addData(String date, String distance, String time, String pace, String speed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, date);
        contentValues.put(COL3, distance);
        contentValues.put(COL4, time);
        contentValues.put(COL5, pace);
        contentValues.put(COL6, speed);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            Log.i("Adding data", "Successfully.");
            return true;
        }
    }


    /**
     * Gets all contents from the Database.
     *
     * @return the list contents
     */
    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }


}