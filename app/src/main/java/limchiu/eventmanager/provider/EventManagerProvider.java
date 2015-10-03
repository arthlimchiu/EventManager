package limchiu.eventmanager.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import limchiu.eventmanager.database.EventsDatabaseHelper;
import limchiu.eventmanager.database.EventsTable;

/**
 * Created by Clarence on 10/3/2015.
 */
public class EventManagerProvider extends ContentProvider {

    private EventsDatabaseHelper database;

    // Used for UriMatcher
    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;

    private static final String AUTHORITY = "limchiu.eventmanager.provider";
    private static final String PATH_EVENTS = "events";

    public static final Uri CONTENT_URI_EVENTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_EVENTS);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, PATH_EVENTS, EVENTS);
        sUriMatcher.addURI(AUTHORITY, PATH_EVENTS + "/#", EVENT_ID);
    }

    @Override
    public boolean onCreate() {
        database = new EventsDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                builder.setTables(EventsTable.TABLE_EVENTS);
                break;
            case EVENT_ID:
                // adding the ID to the original query
                builder.setTables(EventsTable.TABLE_EVENTS);
                builder.appendWhere(EventsTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners  are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();

        long id;
        String path;

        switch (uriType) {
            case EVENTS:
                id = db.insert(EventsTable.TABLE_EVENTS, null, values);
                path = PATH_EVENTS;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;

        String id;

        switch (uriType) {
            case EVENTS:
                rowsDeleted = db.delete(EventsTable.TABLE_EVENTS, selection, selectionArgs);
                break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(EventsTable.TABLE_EVENTS, EventsTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(EventsTable.TABLE_EVENTS, EventsTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;

        String id;

        switch (uriType) {
            case EVENTS:
                rowsUpdated = db.update(EventsTable.TABLE_EVENTS, values, selection, selectionArgs);
                break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(EventsTable.TABLE_EVENTS, values, EventsTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(EventsTable.TABLE_EVENTS, values, EventsTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
