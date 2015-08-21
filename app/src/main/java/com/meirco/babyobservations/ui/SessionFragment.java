package com.meirco.babyobservations.ui;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meirco.babyobservations.R;
import com.meirco.babyobservations.db.DbHelper;
import com.meirco.babyobservations.di.Injector;

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
        return content;
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
