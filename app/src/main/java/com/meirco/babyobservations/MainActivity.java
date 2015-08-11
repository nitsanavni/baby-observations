package com.meirco.babyobservations;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

}
