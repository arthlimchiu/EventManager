package limchiu.eventmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clarence on 10/3/2015.
 */
public class EventsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eventsdb.db";
    private static final int DATABASE_VERSION = 1;

    public EventsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        EventsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
