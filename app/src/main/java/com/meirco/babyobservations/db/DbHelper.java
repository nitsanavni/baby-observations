package com.meirco.babyobservations.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.meirco.babyobservations.utils.StringUtils;

import java.util.Calendar;

import javax.inject.Singleton;

/**
 * Created by nitsa_000 on 10-Aug-15.
 */
@Singleton
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "baby_obs.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "dbhelper";


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

    public String toString() {
        return printSessions() +
                "\n" + printEntries() +
                "\n" + printSeenEntries();
    }

    private String printSessions() {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(Contract.Session.TABLE,
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
            sb.append("session:");
            while (!cursor.isAfterLast()) {
                long created = cursor.getLong(cursor.getColumnIndex(Contract.Session.COL_CREATED));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(created);
                sb
                        .append("\n")
                        .append(cursor.getLong(cursor.getColumnIndex(Contract.Session._ID)))
                        .append(" ")
                        .append(calendar.getTime().toString());
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
                sb
                        .append("\nseen: ")
                        .append(cursor.getLong(cursor.getColumnIndex(Contract.SeenEntry._ID)))
                        .append(" ")
                        .append(cursor.getString(cursor.getColumnIndex(Contract.SeenEntry.COL_TEXT)));
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
            sb.append("entries:");
            while (!cursor.isAfterLast()) {
                sb.append("\n")
                        .append(cursor.getLong(cursor.getColumnIndex(Contract.Entry.COL_SESSION)))
                        .append(" ")
                        .append(cursor.getLong(cursor.getColumnIndex(Contract.Entry.COL_TEXT)));
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

    public void addEntry(long textId, long sessionId) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            upFrequency(db, textId);
            values.put(Contract.Entry.COL_CREATED, System.currentTimeMillis());
            values.put(Contract.Entry.COL_SESSION, sessionId);
            values.put(Contract.Entry.COL_TEXT, textId);
            db.insert(Contract.Entry.TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
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
            upFrequency(db, textId);
            values.put(Contract.Entry.COL_CREATED, System.currentTimeMillis());
            values.put(Contract.Entry.COL_SESSION, sessionId);
            values.put(Contract.Entry.COL_TEXT, textId);
            db.insert(Contract.Entry.TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void upFrequency(SQLiteDatabase db, long textId) {
        String table = Contract.SeenEntry.TABLE;
        String freq = Contract.SeenEntry.COL_FREQ;
        String id = Contract.SeenEntry._ID;
        db.execSQL("UPDATE " + table + " SET " + freq + " = " + freq + " + 1 WHERE " + id + " = " + String.valueOf(textId));
    }

    private long getTextId(SQLiteDatabase db, String text) {
        Cursor cursor = null;
        try {
            String[] columns = new String[]{Contract.SeenEntry._ID, Contract.SeenEntry.COL_TEXT};
            String selection = Contract.SeenEntry.COL_TEXT + " =?";
            String[] selectionArgs = new String[]{text};
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
            values.put(Contract.SeenEntry.COL_FREQ, 0);
            values.put(Contract.SeenEntry.COL_TEXT, text);
            return db.insert(Contract.SeenEntry.TABLE, null, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return 0L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Cursor getTopUsed(String filter) {
        Cursor cursor = null;
        try {
            String table = Contract.SeenEntry.TABLE;
            String[] columns = new String[]{Contract.SeenEntry._ID, Contract.SeenEntry.COL_TEXT};
            String selection = null;
            String[] selectionArgs = null;
            if (!StringUtils.isNullOrEmpty(filter)) {
                selection = Contract.SeenEntry.COL_TEXT + " LIKE ?";
                selectionArgs = new String[]{"%" + filter + "%"};
            }
            String groupBy = null;
            String having = null;
            String orderBy = Contract.SeenEntry.COL_FREQ + " DESC";
            cursor = getReadableDatabase()
                    .query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return cursor;
    }

    public static String getSeenText(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(Contract.SeenEntry.COL_TEXT));
    }

    public long addSession() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Session.COL_CREATED, System.currentTimeMillis());
        long id = db.insert(Contract.Session.TABLE, null, values);
        return id;
    }

    public void closeSession(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Session.COL_ENDED, System.currentTimeMillis());
        String whereClause = Contract.Session._ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.update(Contract.Session.TABLE, values, whereClause, whereArgs);
    }

    public Cursor getSessions() {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String table = Contract.Session.TABLE;
            String[] columns = null;
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = Contract.Session.COL_CREATED + " ASC";
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
        }
        return null;
    }

    public static long getSessionId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(Contract.Session._ID));
    }

    public static long getSessionCreated(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(Contract.Session.COL_CREATED));
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
