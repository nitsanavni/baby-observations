package com.meirco.babyobservations.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.meirco.babyobservations.utils.StringUtils;

/**
 * Created by nitsa_000 on 10-Aug-15.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "baby_obs.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "dbhelper";

    private static DbHelper sInstance;

    private static final String INT_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES_TABLE =
            "CREATE TABLE " + Contract.Entry.TABLE + " (" +
                    Contract.Entry._ID + " INTEGER PRIMARY KEY," +
                    Contract.Entry.COL_CREATED + INT_TYPE + COMMA_SEP +
                    Contract.Entry.COL_SESSION + INT_TYPE + COMMA_SEP +
                    Contract.Entry.COL_TEXT + INT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES_TABLE =
            "DROP TABLE IF EXISTS " + Contract.Entry.TABLE;
    private static final String SQL_CREATE_SEEN_ENTRIES_TABLE =
            "CREATE TABLE " + Contract.SeenEntry.TABLE + " (" +
                    Contract.SeenEntry._ID + " INTEGER PRIMARY KEY," +
                    Contract.SeenEntry.COL_CREATED + INT_TYPE + COMMA_SEP +
                    Contract.SeenEntry.COL_FREQ + INT_TYPE + COMMA_SEP +
                    Contract.SeenEntry.COL_UPDATE + INT_TYPE + COMMA_SEP +
                    Contract.SeenEntry.COL_TEXT + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_SEEN_ENTRIES_TABLE =
            "DROP TABLE IF EXISTS " + Contract.SeenEntry.TABLE;

    private static final String SQL_CREATE_SESSIONS_TABLE =
            "CREATE TABLE " + Contract.Session.TABLE + " (" +
                    Contract.Session._ID + " INTEGER PRIMARY KEY," +
                    Contract.Session.COL_CREATED + INT_TYPE + COMMA_SEP +
                    Contract.Session.COL_ENDED + INT_TYPE + COMMA_SEP +
                    Contract.Session.COL_FREE_TEXT + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_SESSIONS_TABLE =
            "DROP TABLE IF EXISTS " + Contract.Session.TABLE;


    public static String getCreate() {
        return SQL_CREATE_ENTRIES_TABLE + "\n" +
                SQL_CREATE_SEEN_ENTRIES_TABLE+ "\n" +
                SQL_CREATE_SESSIONS_TABLE + "\n";
    }

    public String toString() {
        return printEntries() + "\n" + printSeenEntries();
    }

    private String printSeenEntries() {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(Contract.SeenEntry.TABLE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            if (!cursor.moveToFirst()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            while (!cursor.isAfterLast()) {
                sb.append("\nseen: " + cursor.getLong(cursor.getColumnIndex(Contract.SeenEntry._ID)) + " " +
                        cursor.getString(cursor.getColumnIndex(Contract.SeenEntry.COL_TEXT)));
                cursor.moveToNext();
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String printEntries() {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(Contract.Entry.TABLE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            if (!cursor.moveToFirst()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            while (!cursor.isAfterLast()) {
                sb.append("\nentry: " + cursor.getLong(cursor.getColumnIndex(Contract.Entry.COL_TEXT)));
                cursor.moveToNext();
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static DbHelper getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new DbHelper(context);
        }
        return sInstance;
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_TABLE);
        db.execSQL(SQL_CREATE_SEEN_ENTRIES_TABLE);
        db.execSQL(SQL_CREATE_SESSIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_TABLE);
        db.execSQL(SQL_DELETE_SEEN_ENTRIES_TABLE);
        db.execSQL(SQL_DELETE_SESSIONS_TABLE);
        onCreate(db);
    }

    public void addEntry(String string, long sessionId) {
        if (StringUtils.isNullOrEmpty(string)) {
            return;
        }
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long textId = getTextId(db, string);
            values.put(Contract.Entry.COL_CREATED, System.currentTimeMillis());
            values.put(Contract.Entry.COL_SESSION, sessionId);
            values.put(Contract.Entry.COL_TEXT, textId);
            db.insert(Contract.Entry.TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    private long getTextId(SQLiteDatabase db, String text) {
        String[] columns = new String[]{Contract.SeenEntry._ID, Contract.SeenEntry.COL_TEXT};
        String selection = Contract.SeenEntry.COL_TEXT + " =?";
        String[] selectionArgs = new String[]{text};
        Cursor cursor = null;
        try {
            cursor = db.query(Contract.SeenEntry.TABLE,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex(Contract.SeenEntry._ID));
            }
            ContentValues values = new ContentValues();
            long time = System.currentTimeMillis();
            values.put(Contract.SeenEntry.COL_CREATED, time);
            values.put(Contract.SeenEntry.COL_UPDATE, time);
            values.put(Contract.SeenEntry.COL_FREQ, 1);
            values.put(Contract.SeenEntry.COL_TEXT, text);
            return db.insert(Contract.SeenEntry.TABLE, null, values);
        } catch (Exception e) {
            return 0L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private static final class Contract {
        public Contract() {
        }

        private static final class Entry implements BaseColumns {
            private static final String TABLE = "entry_";
            private static final String COL_CREATED = "created_at";
            private static final String COL_TEXT = "text_";
            private static final String COL_SESSION = "session_";
        }

        private static final class SeenEntry implements BaseColumns {
            private static final String TABLE = "seen_entry";
            private static final String COL_CREATED = "created_at";
            private static final String COL_UPDATE = "updated_at";
            private static final String COL_FREQ = "frequency_";
            private static final String COL_TEXT = "text_";
        }

        private static final class Session implements BaseColumns {
            private static final String TABLE = "session_";
            private static final String COL_CREATED = "created_at";
            private static final String COL_ENDED = "ended_at";
            private static final String COL_FREE_TEXT = "free_text";
        }

    }

}
