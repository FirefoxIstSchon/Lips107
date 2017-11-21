package designersfox.k.infiradiorebuild;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
//will be used:
import android.os.AsyncTask;

//RELEASE VERSION.

public class RadioPageActivity extends Activity {

    private static final int MAX_REFRESH_CTR = 3;
    private static final int RADIO_AMOUNT = 2;
    int currentStop = -99;
    int currentStopTime;
    Random rand;
    Menu menu;
    Button turnOnButton;
    MenuItem refreshButton;
    EditText developerText;
    Boolean started = false;
    MediaPlayer mediaPlayerLips;
    MediaPlayer mediaPlayerFlash;
    MediaPlayer mediaPlayerWave;
    MediaPlayer mediaPlayerVRock;
    MediaPlayer currentMediaPlayer;
    ArrayList<MediaPlayer> mediaPlayers;
    //boolean prepared = false;
    boolean mediaPlayersInitialized = false;
    boolean doubleBackToExitPressedOnce = false;
    int alreadyRefreshed = 0;


    //MediaPlayer currentMediaPlayer;

    //String URL = "http://sendeyo.com/en/8aa6e859b1";
    //String SecondaryURL =
    // "http://cs1.mp3.pm/download/20664283/dWVvR3MzVUVodE1IQ2FRSElqT0x1WlJpS2plcVhDcW91NVIwaWhqZTUzQitacWtCVnoyQlNPQzI5TU5LcTRROTBGb3VQMCtVRlM3d3hoZkUyZ2lwcDAveDJSVXY5UWZmMjhEVzZRVlIvcDJEanNGeGFzNk9aUTVpajNCYUFzQkY/GTA_III_-_Radio_Lips_106_(mp3.pm).mp3"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE); //todo: imp screen turning cases.
        if(mediaPlayerLips == null){
            initViews();
            initMediaPlayers();
        }

        //initCurrentMediaPlayer();


        //new PlayerTask().execute();
    }

    /*private class PlayerTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            mediaPlayerLips.start();
            prepared = true;
            return prepared;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mediaPlayerLips.seekTo(rand.nextInt(60*20*1000));
        }
    }*/

    private void initViews(){
        refreshButton = findViewById(R.id.refreshButton);
        turnOnButton = findViewById(R.id.turnOnButton);
        developerText = findViewById(R.id.developerText);
    }

    private void initMediaPlayers(){
        if(rand == null) rand = new Random();
        mediaPlayers = new ArrayList<>();
        mediaPlayerLips = MediaPlayer.create(RadioPageActivity.this, R.raw.lips);
        mediaPlayerLips.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerLips.setLooping(true);
        mediaPlayers.add(mediaPlayerLips);
        mediaPlayerFlash = MediaPlayer.create(RadioPageActivity.this, R.raw.flash);
        mediaPlayerFlash.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerFlash.setLooping(true);
        mediaPlayers.add(mediaPlayerFlash);
        /*mediaPlayerWave = MediaPlayer.create(RadioPageActivity.this, R.raw.wave);
        mediaPlayerWave.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerWave.setLooping(true);
        mediaPlayers.add(mediaPlayerWave);
        mediaPlayerVRock = MediaPlayer.create(RadioPageActivity.this, R.raw.vrock);
        mediaPlayerVRock.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerVRock.setLooping(true);
        mediaPlayers.add(mediaPlayerVRock);
        mediaPlayers.add(currentMediaPlayer);
        mediaPlayersInitialized = true;*/
        fetch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radiomenu, menu);
        this.menu = menu;
        if (alreadyRefreshed == MAX_REFRESH_CTR){menu.findItem(R.id.refreshButton).setVisible(false);} //optional: refresh once
        menu.findItem(R.id.textRefreshCounter).setTitle(String.valueOf(MAX_REFRESH_CTR - alreadyRefreshed));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.turnOnButton:
                if(!started){
                    if(currentStop != -99){
                        int timeElapsedSinceStop = (int) System.currentTimeMillis() - currentStopTime;
                        mediaPlayerLips.seekTo(currentStop +
                                timeElapsedSinceStop);
                    }
                    mediaPlayerLips.start();
                    started = true;
                }else{
                    currentStop = mediaPlayerLips.getCurrentPosition();
                    currentStopTime = (int) System.currentTimeMillis();
                    mediaPlayerLips.pause();
                    started = false;
                }
                break;
            case R.id.refreshButton:
                refresh();
                alreadyRefreshed++;
                invalidateOptionsMenu();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        if(started){
            currentStop = mediaPlayerLips.getCurrentPosition();
            mediaPlayerLips.pause();
            currentStopTime = (int) System.currentTimeMillis();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(started){
            int destination = (int) System.currentTimeMillis() - currentStopTime;
            mediaPlayerLips.seekTo(currentStop + destination, MediaPlayer.SEEK_NEXT_SYNC);
            mediaPlayerLips.start();
        }
    }*/

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer e: mediaPlayers){
            if(e != null){e.stop(); e = null;}
        }
    }

    private void fetch() {
        if (mediaPlayerLips == null) {initMediaPlayers();}
        else{currentMediaPlayer = mediaPlayerLips;
            startCurrentPlayer();}
    }

    private void refresh(){
        currentMediaPlayer.pause();
        switch(rand.nextInt(RADIO_AMOUNT)){
            case 0:
                if(currentMediaPlayer == mediaPlayerLips)
                {runRefresh(); break;}
                currentMediaPlayer = mediaPlayerLips;
                break;
            case 1:
                if(currentMediaPlayer == mediaPlayerFlash)
                {runRefresh(); break;}
                currentMediaPlayer = mediaPlayerFlash;
                break;
            case 2:
                if(currentMediaPlayer == mediaPlayerWave)
                {runRefresh(); break;}
                currentMediaPlayer = mediaPlayerWave;
                break;
            case 3:
                if(currentMediaPlayer == mediaPlayerVRock)
                {runRefresh(); break;}
                currentMediaPlayer = mediaPlayerVRock;
                break;
            default:
        }
        startCurrentPlayer();
    }

    private void runRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }).start();
    }

    private void startCurrentPlayer(){
        currentMediaPlayer.start();
        started = true;
        currentMediaPlayer.seekTo(rand.nextInt(60 * 15 * 1000));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "DoubleClick 2 Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }

}