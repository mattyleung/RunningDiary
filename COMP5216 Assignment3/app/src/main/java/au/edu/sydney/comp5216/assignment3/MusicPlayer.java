package au.edu.sydney.comp5216.assignment3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Music player.
 */
public class MusicPlayer extends AppCompatActivity {

    /**
     * The constant RUNTIME_PERMISSION_CODE.
     */
    public static final int RUNTIME_PERMISSION_CODE = 7;
    /**
     * The Context.
     */
    Context context;
    /**
     * The List elements.
     */
    String[] ListElements = new String[]{};
    /**
     * The List elements uri.
     */
    Uri[] ListElementsUri = new Uri[]{};
    /**
     * The List view.
     */
    ListView listView;
    /**
     * The List elements array list.
     */
    List<String> ListElementsArrayList;
    /**
     * The List elements array list uri.
     */
    List<Uri> ListElementsArrayListUri;
    /**
     * The Adapter.
     */
    ArrayAdapter<String> adapter;
    /**
     * The Content resolver.
     */
    ContentResolver contentResolver;
    /**
     * The Cursor.
     */
    Cursor cursor;
    /**
     * The Uri.
     */
    Uri uri;
    /**
     * The Pause button.
     */
    Button pauseButton, /**
     * The Resume button.
     */
    resumeButton, /**
     * The Home button.
     */
    homeButton;
    /**
     * The Pause position.
     */
    int pausePosition, /**
     * The Number of play times.
     */
    numberOfPlayTimes = 1;
    /**
     * The M player.
     */
    MediaPlayer mPlayer;

    /**
     * This function will be called once the emulator access MusicPlayer class.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);
        listView = (ListView) findViewById(R.id.listView1);
        resumeButton = (Button) findViewById(R.id.resumeButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        homeButton = (Button) findViewById(R.id.homeButtonMusic);
        context = getApplicationContext();
        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));
        ListElementsArrayListUri = new ArrayList<>(Arrays.asList(ListElementsUri));
        adapter = new ArrayAdapter<String>
                (MusicPlayer.this, android.R.layout.simple_list_item_1, ListElementsArrayList);

        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();
        GetAllMediaMp3Files();
        listView.setAdapter(adapter);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pausePosition = mPlayer.getCurrentPosition();
                mPlayer.pause();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.seekTo(pausePosition);
                mPlayer.start();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicPlayer.this, MainActivity.class);
                startActivity(intent);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MusicPlayer.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG)
                        .show();
                if (numberOfPlayTimes == 1) {
                    mPlayer = MediaPlayer.create(MusicPlayer.this, ListElementsUri[position]);
                    mPlayer.start();
                    numberOfPlayTimes++;
                } else {
                    mPlayer.release();
                    mPlayer = MediaPlayer.create(MusicPlayer.this, ListElementsUri[position]);
                    mPlayer.start();
                    numberOfPlayTimes++;
                }
            }
        });
    }

    /**
     * Functions that get all meida files from the device.
     * Get all media mp 3 files.
     */
    public void GetAllMediaMp3Files() {
        contentResolver = context.getContentResolver();
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MusicPlayer.this, "Something Went Wrong.", Toast.LENGTH_LONG);
            return;
        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MusicPlayer.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);
            return;
        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            Log.i("Testing", "This is the uri of: " + uri);

            do {
                String SongTitle = cursor.getString(Title);
                String path = cursor.getString(Data);
                // Adding Media File Names to ListElementsArrayList.
                ListElementsArrayList.add(SongTitle);
                ListElementsArrayListUri.add(Uri.parse(path));
            }
            while (cursor.moveToNext());
        }

        Log.i("Length of ArrayList", "is: " + ListElementsArrayList.size());
        Log.i("Length of ArrayListURI", "is: " + ListElementsArrayListUri.size());
        ListElementsUri = ListElementsArrayListUri.toArray(ListElementsUri);
        Log.i("Length of URI array[]", "is: " + ListElementsUri.length);
        Log.i("URI array at 5", "This is: " + ListElementsUri[5]);
    }

    /**
     * Require Android runtime permission.
     */
// Creating Runtime permission function.
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MusicPlayer.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MusicPlayer.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            MusicPlayer.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {

            }
        }
    }

    /**
     * Require Android runtime permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case RUNTIME_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }
}