package com.meirco.babyobservations.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meirco.babyobservations.R;
import com.meirco.babyobservations.db.DbHelper;
import com.meirco.babyobservations.di.Injector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by nitsa_000 on 21-Aug-15.
 */
public class SessionFragment extends Fragment {

    private static final String KEY_SESSION_ID = "session_id";
    private static final String TAG = "SessionF";
    @Inject
    Lazy<DbHelper> mDbHelper;
    private long mSessionId;

    public static Fragment newInstance(long sessionId) {
        Fragment f = new SessionFragment();
        Bundle args = new Bundle(1);
        args.putLong(KEY_SESSION_ID, sessionId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (null == args || args.isEmpty()) {
            Log.e(TAG, "something went wrong - got null/empty args");
            return;
        }
        mSessionId = args.getLong(KEY_SESSION_ID);
    }

    public SessionFragment() {
        Injector.getInstance().getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_session, null);
        ListView list = (ListView) content.findViewById(R.id.list);
        list.setAdapter(new Adapter(getActivity(), mDbHelper.get().getSessionEntries(mSessionId)));
        TextView title = (TextView) content.findViewById(R.id.title);
        title.setText(getString(R.string.session_title, mSessionId));
        content.findViewById(R.id.export_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportSessionText();
            }
        });
        return content;
    }

    private void exportSessionText() {
        Cursor cursor = mDbHelper.get().getSessionEntries(mSessionId);
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(getActivity(), R.string.export_session_no_entries_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = SimpleDateFormat.getTimeInstance();
        while (!cursor.isAfterLast()) {
            long entryCreated = DbHelper.getEntryCreated(cursor);
            calendar.setTimeInMillis(entryCreated);
            dateFormat.setCalendar(calendar);
            String format = dateFormat.format(calendar.getTime());
            String entryText = mDbHelper.get().getSeenText(DbHelper.getEntryText(cursor));
            sb.append(format)
                    .append("\t")
                    .append(entryText)
                    .append("\n");
            cursor.moveToNext();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private static class Adapter extends CursorAdapter {
        private final Calendar mCalendar;

        public Adapter(Context context, Cursor c) {
            super(context, c, true);
            mCalendar = Calendar.getInstance();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.list_item, null);
            edit(tv, cursor);
            return tv;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            edit((TextView) view, cursor);
        }

        private void edit(TextView tv, Cursor cursor) {
            long created = DbHelper.getEntryCreated(cursor);
            mCalendar.setTimeInMillis(created);
            String time = String.valueOf(mCalendar.getTime().toString());
            String textId = String.valueOf(DbHelper.getEntryText(cursor));
            tv.setText(time + " " + textId);
        }
    }


}
