package com.meirco.babyobservations.ui;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class SessionsFragment extends Fragment {

    @Inject
    Lazy<DbHelper> mDbHelper;

    public static SessionsFragment newInstance() {
        return new SessionsFragment();
    }

    public SessionsFragment() {
        Injector.getInstance().getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView list = (ListView) inflater.inflate(R.layout.fragment_sessions, null);
        list.setAdapter(new Adapter(getActivity(), mDbHelper.get().getSessions()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSessionScreen(id);
            }
        });
        return list;
    }

    private void openSessionScreen(long id) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame_for_fragments, SessionFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
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
            long created = DbHelper.getSessionCreated(cursor);
            mCalendar.setTimeInMillis(created);
            tv.setText(String.valueOf(DbHelper.getSessionId(cursor)) + " " +
                    String.valueOf(mCalendar.getTime().toString()));
        }
    }


}
