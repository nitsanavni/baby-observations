package com.meirco.babyobservations;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.meirco.babyobservations.db.DbHelper;
import com.meirco.babyobservations.di.Injector;
import com.meirco.babyobservations.utils.StringUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class MainActivity extends Activity {

    @Inject
    Lazy<DbHelper> mDbHelper;

    private Button mSessionButton;
    private boolean mIsSessionActive = false;
    private View mSessionLayout;
    private EditText mField;
    private long mSessionId;
    private TextView mDebugTextView;
    private CursorAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        mSessionButton = (Button) findViewById(R.id.session_button);
        mSessionLayout = findViewById(R.id.session_layout);
        mField = (EditText) findViewById(R.id.text_field);
        mField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTopUsedList();
            }
        });
        findViewById(R.id.save_entry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
                updateTopUsedList();
            }
        });
        mDebugTextView = (TextView) findViewById(R.id.debug_text);
        mDebugTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mDbHelper.get().toString();
                mDebugTextView.setText(s);
                toggleSessionState();
            }
        });
        mAdapter = new Adapter(this, mDbHelper.get().getTopUsed(null));
        ListView list = (ListView) findViewById(R.id.most_fequently_used_list);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDbHelper.get().addEntry(id, mSessionId);
                updateTopUsedList();
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.getMenu().findItem(R.id.sessions).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                closeDrawer();
                return true;
            }
        });
        updateSessionUI();
    }

    private void updateTopUsedList() {
        Editable text = mField.getText();
        String filter = null;
        if (null != text) {
            filter = text.toString();
        }
        mAdapter.changeCursor(mDbHelper.get().getTopUsed(filter));
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(mNavigationView);
    }

    private boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mNavigationView);
    }

    private void saveEntry() {
        Editable text = mField.getText();
        if (text == null || text.length() == 0) {
            return;
        }
        String string = text.toString();
        mDbHelper.get().addEntry(string, mSessionId);
        mField.setText(StringUtils.EMPTY_STRING);
    }

    private void toggleSessionState() {
        mIsSessionActive = !mIsSessionActive;
        if (mIsSessionActive) {
            startNewSession();
        } else {
            saveSession();
        }
        updateSessionUI();
    }

    private void startNewSession() {
        mSessionId = mDbHelper.get().addSession();
    }

    private void saveSession() {
        // TODO - add a dialog for editing the free text of the session
        mDbHelper.get().closeSession(mSessionId);
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
