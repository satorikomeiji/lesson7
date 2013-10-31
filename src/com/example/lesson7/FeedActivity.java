package com.example.lesson7;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 10/31/13
 * Time: 11:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedActivity extends ListActivity {
    private RSSDBAdapter mDbHelper;
    private ArrayList<Article> articles;
    public static ArrayAdapter<Article> arrayAdapter;
    private long index;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new RSSDBAdapter(this);
        articles = new ArrayList<>();
        Intent intent = getIntent();
        index = intent.getLongExtra("Index", -1);
        if (index == -1) {
            Toast.makeText(this, "index is -1, error", 2000).show();
        }
        else {
            mDbHelper.open();
            Cursor entriesCursor = mDbHelper.fetchEntriesForId(index);
            if (entriesCursor == null) {
                Toast.makeText(this, "No entries", 2000).show();
            } else {
                if (entriesCursor.moveToFirst()) {
                    int _id = entriesCursor.getInt(0);
                    String title = entriesCursor.getString(1);
                    String description = entriesCursor.getString(2);
                    articles.add(new Article(_id, title, description));
                    while (entriesCursor.moveToNext()) {
                        _id = entriesCursor.getInt(0);
                        title = entriesCursor.getString(1);
                        description = entriesCursor.getString(2);
                        articles.add(new Article(_id, title, description));
                    }
                }
                String[] from = new String[]{"title"};

                int[] to = new int[]{R.id.text1};

                //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.notes_row, entriesCursor, from, to);
                arrayAdapter = new ArrayAdapter<>(this, R.layout.notes_row, articles);
                setListAdapter(arrayAdapter);
                //arrayAdapter.notifyDataSetChanged();
            }

        }
    }
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {

        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("Description", articles.get(position).description);
        startActivity(intent);
    }
}