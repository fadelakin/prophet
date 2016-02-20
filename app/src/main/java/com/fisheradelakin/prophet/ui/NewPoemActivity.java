package com.fisheradelakin.prophet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.fisheradelakin.prophet.R;
import com.fisheradelakin.prophet.model.Poem;
import com.fisheradelakin.prophet.util.ErrorDialog;
import com.fisheradelakin.prophet.util.StorageConfirmationDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class NewPoemActivity extends AppCompatActivity {

    private static final String TAG = "NPA";
    private static final int WRITE_STORAGE_PERMISSION = 1;
    public static final String FRAGMENT_DIALOG = "dialog";
    private Realm mRealm;

    @Bind(R.id.title_et) EditText mTitleET;
    @Bind(R.id.poem_et) EditText mPoemET;
    @Bind(R.id.author_et) EditText mAuthorET;
    @Bind(R.id.poem_view) RelativeLayout mPoemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poem);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Poem");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRealm = Realm.getDefaultInstance();

        String poemTimestamp = getIntent().getStringExtra("time");
        if (poemTimestamp != null) {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<Poem> query = realm.where(Poem.class);
            query.equalTo("timestamp", poemTimestamp);
            RealmResults<Poem> results = query.findAll();
            if (results.size() > 0) {
                Poem poem = results.get(0);
                mTitleET.setText(poem.getTitle());
                toolbar.setTitle(poem.getTitle());
                mPoemET.setText(poem.getPoem());
                if (poem.getAuthor() != null)
                    mAuthorET.setText(poem.getAuthor());
            }
        }
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
            savePoem();
            return true;
        } else if (id == R.id.action_share) {
            sharePoem();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    private void sharePoem() {

        /*View v = mPoemView.getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = v.getDrawingCache();*/

        mAuthorET.setCursorVisible(false);
        mPoemET.setCursorVisible(false);
        mTitleET.setCursorVisible(false);

        mPoemView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mPoemView.getDrawingCache();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestWriteStoragePermission();
        } else {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Prophet");

            if (!folder.exists()) {
                folder.mkdir();
                Log.i(TAG, "Folder doesn't exist but it does now.");
            } else {
                Log.i(TAG, "Folder exists");
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File file = new File(folder, "prophet" + timeStamp + ".png");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                file.setReadable(true, false);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/jpeg");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAuthorET.setCursorVisible(true);
            mPoemET.setCursorVisible(true);
            mTitleET.setCursorVisible(true);
        }
    }

    private void savePoem() {
        Poem poem = new Poem();
        poem.setTitle(mTitleET.getText().toString());
        poem.setPoem(mPoemET.getText().toString());
        poem.setAuthor(mAuthorET.getText().toString());
        poem.setTimestamp(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()));
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
    }

    private void requestWriteStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new StorageConfirmationDialog().show(getFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.write_permission))
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
