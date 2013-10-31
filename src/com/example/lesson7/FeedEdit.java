package com.example.lesson7;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 10/31/13
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedEdit extends Activity {

    private EditText mTitleText;
    private EditText mLinkText;
    private Long mRowId;
    private RSSDBAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new RSSDBAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mLinkText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(RSSDBAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(RSSDBAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }


        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchChannel(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(RSSDBAdapter.KEY_TITLE)));
            mLinkText.setText(note.getString(
                    note.getColumnIndexOrThrow(RSSDBAdapter.KEY_LINK)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(RSSDBAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String link = mLinkText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.addRSS(title, link);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateChannel(mRowId, title, link);
        }
    }

}
