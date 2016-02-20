package com.fisheradelakin.prophet.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.fisheradelakin.prophet.R;
import com.fisheradelakin.prophet.model.Poem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class NewPoemActivity extends AppCompatActivity {

    private static final String TAG = "NPA";
    private Realm mRealm;

    @Bind(R.id.title_et) EditText mTitleET;
    @Bind(R.id.poem_et) EditText mPoemET;
    @Bind(R.id.author_et) EditText mAuthorET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poem);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Poem poem = new Poem();
            poem.setTitle(mTitleET.getText().toString());
            poem.setPoem(mPoemET.getText().toString());
            poem.setAuthor(mAuthorET.getText().toString());
            mRealm.beginTransaction();
            mRealm.copyToRealm(poem);
            mRealm.commitTransaction();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                }
            }, new Realm.Transaction.Callback() {
                @Override
                public void onSuccess() {
                    Log.v(TAG, "Poem added to database");
                    super.onSuccess();
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    super.onError(e);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
