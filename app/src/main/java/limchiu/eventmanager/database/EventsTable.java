package limchiu.eventmanager.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Clarence on 10/3/2015.
 */
public class EventsTable {

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME_START = "time_start";
    public static final String COLUMN_TIME_END = "time_end";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_EVENTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_TIME_START + " text, "
            + COLUMN_TIME_END + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_ADDRESS + " text, "
            + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real "
            + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        // Put upgrade statements here
    }

}
