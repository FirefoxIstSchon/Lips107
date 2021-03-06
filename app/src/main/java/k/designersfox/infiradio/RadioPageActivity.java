package k.designersfox.infiradio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

//RELEASE VERSION.

public class RadioPageActivity extends Activity {

    private static final int MAX_REFRESH_CTR = 3;
    private static final int RADIO_AMOUNT = 2;
    int refreshCounter = 0;
    int currentStop = -999;
    long currentStopTime = -999;
    boolean started = false;
    boolean mediaPlayersInitialized = false;
    boolean doubleBackToExitPressedOnce = false;
    boolean isMenuHelpShown;

    SharedPreferences sharedPreferences;
    Random rand;
    Menu menu;
    Button turnOnButton;
    MenuItem refreshButton;
    EditText developerText;
    ImageView imageView;
    GifImageView gifView;

    MediaPlayer mediaPlayerLips;
    MediaPlayer mediaPlayerFlash;
    MediaPlayer mediaPlayerWave;
    MediaPlayer mediaPlayerVRock;
    MediaPlayer currentMediaPlayer;
    ArrayList<MediaPlayer> mediaPlayers;
    //metadata for fetching radio (initial version - not updated)
    //String URL = "http://sendeyo.com/en/8aa6e859b1";
    //String SecondaryURL =
    // "http://cs1.mp3.pm/download/20664283/dWVvR3MzVUVodE1IQ2FRSElqT0x1WlJpS2plcVhDcW91NVIwaWhqZTUzQitacWtCVnoyQlNPQzI5TU5LcTRROTBGb3VQMCtVRlM3d3hoZkUyZ2lwcDAveDJSVXY5UWZmMjhEVzZRVlIvcDJEanNGeGFzNk9aUTVpajNCYUFzQkY/GTA_III_-_Radio_Lips_106_(mp3.pm).mp3"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_page);

        if(currentMediaPlayer == null){
            initViews(savedInstanceState);
            initMediaPlayers();
        }

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

    private void initViews(Bundle bundle){
        getActionBar().hide();
        if(!bundle.getBoolean("isMenuHelpShown", false)){
            Toast.makeText(this, "Swipe Down: Menu", Toast.LENGTH_LONG).show();
            isMenuHelpShown = true;}
        refreshButton = findViewById(R.id.refreshButton);
        turnOnButton = findViewById(R.id.turnOnButton);
        developerText = findViewById(R.id.developerText);
        imageView = findViewById(R.id.imageView);
        gifView = findViewById(R.id.gifView);

        runGiffer();
    }

    private void runGiffer(){
        String src = String.valueOf(new Random().nextInt(4));
        String filename = "g"+src;
        try {
            GifDrawable gifFromPath = new GifDrawable( getAssets(),filename+".gif");
            gifView.setBackground(gifFromPath);
            imageView.setVisibility(View.GONE);
            gifView.setVisibility(View.VISIBLE);
            switch (Integer.parseInt(src)){
                case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
                case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
                case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE); break;
                case 3: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE); break;
                default: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            }
        }catch (IOException e){e.printStackTrace();}
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
        mediaPlayersInitialized = true;
        /*mediaPlayerWave = MediaPlayer.create(RadioPageActivity.this, R.raw.wave);
        mediaPlayerWave.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerWave.setLooping(true);
        mediaPlayers.add(mediaPlayerWave);
        mediaPlayerVRock = MediaPlayer.create(RadioPageActivity.this, R.raw.vrock);
        mediaPlayerVRock.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerVRock.setLooping(true);
        mediaPlayers.add(mediaPlayerVRock);
        mediaPlayers.add(currentMediaPlayer);
        mediaPlayersInitialized = true;*/ //todo: will be added after the android api 26 bug fixed.
        fetchCurrentMediaPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radiomenu, menu);
        this.menu = menu;
        if (refreshCounter == MAX_REFRESH_CTR){menu.findItem(R.id.refreshButton).setVisible(false);} //optional: refresh once
        menu.findItem(R.id.textRefreshCounter).setTitle(String.valueOf(MAX_REFRESH_CTR - refreshCounter));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.turnOnButton:
                if(!started){
                    if(currentStop != -999){
                        long timeElapsedSinceStop = System.currentTimeMillis() - currentStopTime;
                        mediaPlayerLips.seekTo(currentStop +
                                (int) timeElapsedSinceStop);
                    }
                    currentMediaPlayer.start();
                    started = true;
                }else{
                    currentStop = mediaPlayerLips.getCurrentPosition();
                    currentStopTime = (int) System.currentTimeMillis();
                    currentMediaPlayer.pause();
                    started = false;
                }
                break;
            case R.id.refreshButton:
                refresh();
                refreshCounter++;
                invalidateOptionsMenu();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSharedPref();
        for (MediaPlayer e: mediaPlayers){
            if(e != null){e.stop(); e = null;}
        }
    }

    private void fetchCurrentMediaPlayer() {
        if (mediaPlayersInitialized) {
            currentMediaPlayer = mediaPlayerLips;
            loadSharedPref();}
        else{
            initMediaPlayers();
        }
        startCurrentPlayer();
    }

    private void saveSharedPref(){
        currentStop = currentMediaPlayer.getCurrentPosition();
        currentStopTime = (int) System.currentTimeMillis();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentStop", currentStop);
        editor.putLong("currentStopTime", currentStopTime);
        editor.putBoolean("isMenuHelpShown", isMenuHelpShown);
        int currentStation = -999;
        for(int i = 0; i < RADIO_AMOUNT; i++){
            if (currentMediaPlayer == mediaPlayers.get(i)){currentStation = i;}
        }
        editor.putInt("currentStation", currentStation);
        editor.apply();
    }

    private void loadSharedPref(){
        sharedPreferences = getSharedPreferences("Lips107Prefs", Context.MODE_PRIVATE);
        currentStopTime = sharedPreferences.getLong("currentStopTime", -999);
        currentStop = sharedPreferences.getInt("currentStop", -999);
        switch (sharedPreferences.getInt("currentStation", 0)){
            case 0: currentMediaPlayer = mediaPlayerLips; break;
            case 1: currentMediaPlayer = mediaPlayerFlash; break;
            case 2: currentMediaPlayer = mediaPlayerWave; break;
            case 3: currentMediaPlayer = mediaPlayerVRock; break;
            default:
        }
        if(currentStop != -999 & currentStopTime != -999){
            if(System.currentTimeMillis() - currentStopTime < 30*1000)
                currentMediaPlayer.seekTo(currentStop + (int)(System.currentTimeMillis() - currentStopTime));
        }
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

    private void startCurrentPlayer(){
        currentMediaPlayer.start();
        started = true;
        currentMediaPlayer.seekTo(rand.nextInt(60 * 15 * 1000));
    }

    private void runRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }).start();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!getActionBar().isShowing()) {
                    getActionBar().show();
                } /*else {getActionBar().hide();}
                    return true;
                }*/}
        return false;
    }

}

/*  //PERMISSION STUFF:
    private void checkForPermissions(){ //call dis w final int CALL_REQUEST = 1; declared
        int vibratePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if(vibratePermissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
        }else{

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CALL_REQUEST: if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){}
                               else{Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show();}
                               break;
        }
    }*/

