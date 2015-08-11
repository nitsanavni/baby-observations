package com.meirco.babyobservations;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meirco.babyobservations.db.DbHelper;

public class MainActivity extends Activity {

    private Button mSessionButton;
    private boolean mIsSessionActive = false;
    private View mSessionLayout;
    private EditText mField;
    private Button mSaveButton;
    private long mSessionId;
    private TextView mDebugTextView;
    private ListView mMostUsedEntriesListView;
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSessionButton = (Button) findViewById(R.id.session_button);
        mSessionLayout = findViewById(R.id.session_layout);
        mField = (EditText) findViewById(R.id.text_field);
        mSaveButton = (Button) findViewById(R.id.save_entry_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
                updateTopUsedList();
            }
        });
        mDebugTextView = (TextView) findViewById(R.id.debug_text);
        mDebugTextView.setText(DbHelper.getCreate());
        mSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = DbHelper.getInstance(MainActivity.this).toString();
                mDebugTextView.setText(s);
                toggleSessionState();
            }
        });
        mMostUsedEntriesListView = (ListView) findViewById(R.id.most_fequently_used_list);
        mAdapter = new Adapter(this,DbHelper.getInstance(this).getTopUsed());
        mMostUsedEntriesListView.setAdapter(mAdapter);
    }

    private void updateTopUsedList() {
        mAdapter.changeCursor(DbHelper.getInstance(this).getTopUsed());
    }

    private void saveEntry() {
        Editable text = mField.getText();
        if (text == null || text.length() == 0) {
            return;
        }
        String string = text.toString();
        DbHelper dbHelper = DbHelper.getInstance(this);
        dbHelper.addEntry(string, mSessionId);
    }

    private void toggleSessionState() {
        mIsSessionActive = !mIsSessionActive;
        saveSession();
        updateSessionUI();
    }

    private void saveSession() {
        if (mIsSessionActive) {

        }
    }

    private void updateSessionUI() {
        if (mIsSessionActive) {
            mSessionButton.setText(R.string.end_session);
            mSessionLayout.setVisibility(View.VISIBLE);
        } else {
            mSessionButton.setText(R.string.begin_session);
            mSessionLayout.setVisibility(View.GONE);
        }
    }

    private static class Adapter extends CursorAdapter {

        public Adapter(Context context, Cursor c) {
            super(context, c, true);
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
            tv.setText(DbHelper.getSeenText(cursor));
        }
    }
}
