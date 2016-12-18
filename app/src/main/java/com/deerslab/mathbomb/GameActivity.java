package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameActivity extends Activity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnClr, btnEnter;
    private TextView tvTime, tvQuest, tvComment;
    private String correctAnswer, quest;
    private StringBuffer userAnswer;
    private GameTimer gameTimer;
    private Tracker mTracker;
    private SharedPreferences preferences;

    private SoundPool soundPool;
    private final int MAX_STREAMS = 2;
    private int soundTime;
    private int soundWin;
    MediaPlayer mediaPlayerWin;

    private GameState gameState;
    private int progressCount;
    private final int COUNTforWIN = 10;
    private int number = 0;
    private boolean sound;

    private GoogleApiClient gac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);
        btnClr = (Button) findViewById(R.id.btnClr);
        btnEnter = (Button) findViewById(R.id.btnEnter);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvQuest = (TextView) findViewById(R.id.tvQuest);
        tvComment = (TextView) findViewById(R.id.tvAnswer);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnClr.setOnClickListener(this);
        btnEnter.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sound = preferences.getBoolean("sound", true);

        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);
        soundTime = soundPool.load(this, R.raw.time, 1);
        //soundWin = soundPool.load(this, R.raw.win, 1);
        mediaPlayerWin = MediaPlayer.create(this, R.raw.win);

        try {
            mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.EventBuilder().setAction("start").setCategory(Database.currentCategory.name()).setLabel("Level " + Database.currentLevel).build());

            gac = new GoogleApiClient.Builder(this).addApi(Games.API).build();
            gac.connect();

            //Database.createAds(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userAnswer = new StringBuffer();
        quest = new String();

        gameState = GameState.BEGIN;

        gameTimer = new GameTimer();
        //gameTimer.execute(gameState);
        gameTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gameState);

        refreshScreen();

        BoomActivity.adLoad(this);

    }

    @Override
    public void onClick(View v) {

        switch (gameState) {

            case MAINGAME:
                switch (v.getId()) {
                    case R.id.btn0:
                        userAnswer = userAnswer.append("0");
                        break;
                    case R.id.btn1:
                        userAnswer = userAnswer.append("1");
                        break;
                    case R.id.btn2:
                        userAnswer = userAnswer.append("2");
                        break;
                    case R.id.btn3:
                        userAnswer = userAnswer.append("3");
                        break;
                    case R.id.btn4:
                        userAnswer = userAnswer.append("4");
                        break;
                    case R.id.btn5:
                        userAnswer = userAnswer.append("5");
                        break;
                    case R.id.btn6:
                        userAnswer = userAnswer.append("6");
                        break;
                    case R.id.btn7:
                        userAnswer = userAnswer.append("7");
                        break;
                    case R.id.btn8:
                        userAnswer = userAnswer.append("8");
                        break;
                    case R.id.btn9:
                        userAnswer = userAnswer.append("9");
                        break;
                    case R.id.btnClr:
                        userAnswer = new StringBuffer();
                        break;
                    case R.id.btnEnter:
                        checkAnswer();
                        break;
                }
                break;
            case BOOM:
                switch (v.getId()){
                    default:
                        startActivity(new Intent(GameActivity.this, LevelChooserActivity.class));
                        finish();
                        break;
                }
                break;
            case WIN:
                switch (v.getId()){
                    case R.id.btnClr:
                        startActivity(new Intent(GameActivity.this, CategoryChooserActivity.class));
                        finish();
                        break;
                    case R.id.btnEnter:
                        if (Database.currentLevel < 9){
                            Database.currentLevel++;
                            startActivity(new Intent(GameActivity.this, GameActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(GameActivity.this, CategoryChooserActivity.class));
                            finish();
                        }
                        break;
                }
        }

        refreshScreen();
    }

    private void refreshScreen(){

        switch (gameState) {
            case BEGIN:
                tvComment.setText(getResources().getString(R.string.enterToGame));
                break;
            case MAINGAME:
                tvQuest.setText(quest + userAnswer);

                if (number == 1){
                    tvComment.setText(getResources().getString(R.string.startComment));}
                else {
                    tvComment.setText(number + "/" + COUNTforWIN);
                }

                break;
            case BEFORE_BOOM:
                tvQuest.setText(quest + userAnswer);
                tvComment.setText(getResources().getString(R.string.fail)+" = " + correctAnswer);
                tvTime.setTextColor(getResources().getColor(R.color.colorRed));
                tvTime.setText(getResources().getString(R.string.activateBoom));
                break;
            case WIN:
                tvQuest.setText(getResources().getString(R.string.bombDefused));
                tvComment.setText(getResources().getString(R.string.pressToNext));
                break;
        }
    }

    private void generateQuest(){

        Random rn = new Random();
        int result = 0;
        int a1 =0, a2=0, a3=0, a4=0;
        boolean oper = false;
        number++;

        switch (Database.currentCategory) {
            case PLUS:
                switch (Database.currentLevel) {
                    case 1:
                        a1 = rn.nextInt(19)+1;
                        a2 = rn.nextInt(9)+1;
                        quest = a1 + " + " + a2 + " =";
                        break;
                    case 2:
                        a1 = rn.nextInt(39)+1;
                        a2 = rn.nextInt(39)+1;
                        quest = a1 + " + " + a2 + " =";
                        break;
                    case 3:
                        a1 = rn.nextInt(50)+10;
                        a2 = rn.nextInt(50)+10;
                        quest = a1 + " + " + a2 + " =";
                        break;
                    case 4:
                        a1 = rn.nextInt(90)+10;
                        a2 = rn.nextInt(90)+10;
                        quest = a1 + " + " + a2 + " =";
                        break;
                    case 5:
                        a1 = rn.nextInt(19)+1;
                        a2 = rn.nextInt(19)+1;
                        a3 = rn.nextInt(19)+1;
                        quest = a1 + " + " + a2 + " + " +a3 + " =";
                        break;
                    case 6:
                        a1 = rn.nextInt(39)+1;
                        a2 = rn.nextInt(39)+1;
                        a3 = rn.nextInt(39)+1;
                        quest = a1 + " + " + a2 + " + " +a3 + " =";
                        break;
                    case 7:
                        a1 = rn.nextInt(50)+10;
                        a2 = rn.nextInt(50)+10;
                        a3 = rn.nextInt(50)+10;
                        quest = a1 + " + " + a2 + " + " +a3 + " =";
                        break;
                    case 8:
                        a1 = rn.nextInt(89)+10;
                        a2 = rn.nextInt(89)+10;
                        a3 = rn.nextInt(89)+10;
                        quest = a1 + " + " + a2 + " + " +a3 + " =";
                        break;
                    case 9:
                        a1 = rn.nextInt(70)+10;
                        a2 = rn.nextInt(40)+10;
                        a3 = rn.nextInt(70)+10;
                        a4 = rn.nextInt(19)+1;
                        quest = a1 + " + " + a2 + " + " +a3 + " + " + a4 + " =";
                        break;
                    default:
                        a1 = rn.nextInt(89)+10;
                        a2 = rn.nextInt(40)+10;
                        a3 = rn.nextInt(40)+50;
                        a4 = rn.nextInt(60)+20;
                        quest = a1 + " + " + a2 + " + " +a3 + " + " + a4 + " =";
                        break;
                }
                result = a1 + a2 + a3 + a4;
                break;
            case MINUS:
                switch (Database.currentLevel){
                    case 1:
                        a1 = rn.nextInt(10)+10;
                        a2 = rn.nextInt(9)+1;
                        quest = a1 + " - " + a2 + " =";
                        break;
                    case 2:
                        a1 = rn.nextInt(20)+20;
                        a2 = rn.nextInt(19)+1;
                        quest = a1 + " - " + a2 + " =";
                        break;
                    case 3:
                        a1 = rn.nextInt(30)+40;
                        a2 = rn.nextInt(29)+1;
                        quest = a1 + " - " + a2 + " =";
                        break;
                    case 4:
                        a1 = rn.nextInt(50)+40;
                        a2 = rn.nextInt(39)+1;
                        quest = a1 + " - " + a2 + " =";
                        break;
                    case 5:
                        a1 = rn.nextInt(49)+50;
                        a2 = rn.nextInt(40)+10;
                        quest = a1 + " - " + a2 + " =";
                        break;
                    case 6:
                        a1 = rn.nextInt(20)+20;
                        a2 = rn.nextInt(9)+1;
                        a3 = rn.nextInt(9)+1;
                        quest = a1 + " - " + a2 + " - " + a3 + " =";
                        break;
                    case 7:
                        a1 = rn.nextInt(20)+70;
                        a2 = rn.nextInt(34)+1;
                        a3 = rn.nextInt(34)+1;
                        quest = a1 + " - " + a2 + " - " + a3 + " =";
                        break;
                    case 8:
                        a1 = rn.nextInt(19)+80;
                        a2 = rn.nextInt(39)+1;
                        a3 = rn.nextInt(39)+1;
                        quest = a1 + " - " + a2 + " - " + a3 + " =";
                        break;
                    case 9:
                        a1 = rn.nextInt(19)+80;
                        a2 = rn.nextInt(19)+1;
                        a3 = rn.nextInt(19)+1;
                        a4 = rn.nextInt(9)+1;
                        quest = a1 + " - " + a2 + " - " + a3 +  " - " + a4 + " =";
                        break;
                    default:
                        a1 = rn.nextInt(19)+80;
                        a2 = rn.nextInt(30)+10;
                        a3 = rn.nextInt(19)+1;
                        a4 = rn.nextInt(19)+1;
                        quest = a1 + " - " + a2 + " - " + a3 +  " - " + a4 + " =";
                        break;
                }
                result = a1 - a2 - a3 - a4;
                break;
            case MISC1:
                switch (Database.currentLevel){
                    case 1:
                        a1 = rn.nextInt(10)+10;
                        a2 = rn.nextInt(9)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + (oper ? " + " : " - ") + a2 + " =";
                        result = oper ? a1 + a2 : a1 - a2;
                        break;
                    case 2:
                        a1 = rn.nextInt(20)+20;
                        a2 = rn.nextInt(19)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + (oper ? " + " : " - ") + a2 + " =";
                        result = oper ? a1 + a2 : a1 - a2;
                        break;
                    case 3:
                        a1 = rn.nextInt(40)+40;
                        a2 = rn.nextInt(30)+10;
                        oper = rn.nextBoolean();
                        quest = a1 + (oper ? " + " : " - ") + a2 + " =";
                        result = oper ? a1 + a2 : a1 - a2;
                        break;
                    case 4:
                        a1 = rn.nextInt(49)+50;
                        a2 = rn.nextInt(49)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + (oper ? " + " : " - ") + a2 + " =";
                        result = oper ? a1 + a2 : a1 - a2;
                        break;
                    case 5:
                        a1 = rn.nextInt(20)+20;
                        a2 = rn.nextInt(9)+1;
                        a3 = rn.nextInt(9)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + " - " + a2 + (oper ? " + " : " - ") + a3 + " =";
                        break;
                    case 6:
                        a1 = rn.nextInt(40)+40;
                        a2 = rn.nextInt(19)+1;
                        a3 = rn.nextInt(19)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + " - " + a2 + (oper ? " + " : " - ") + a3 + " =";
                        result = oper ? a1 - a2 + a3 : a1 - a2 - a3;
                        break;
                    case 7:
                        a1 = rn.nextInt(49)+50;
                        a2 = rn.nextInt(24)+1;
                        a3 = rn.nextInt(24)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + " - " + a2 + (oper ? " + " : " - ") + a3 + " =";
                        result = oper ? a1 - a2 + a3 : a1 - a2 - a3;
                        break;
                    case 8:
                        a1 = rn.nextInt(49)+50;
                        a2 = rn.nextInt(49)+50;
                        a3 = rn.nextInt(89)+10;
                        oper = rn.nextBoolean();
                        quest = a1 + " + " + a2 + (oper ? " + " : " - ") + a3 + " =";
                        result = oper ? a1 + a2 + a3 : a1 + a2 - a3;
                        break;
                    case 9:
                        a1 = rn.nextInt(40)+40;
                        a2 = rn.nextInt(40)+40;
                        a3 = rn.nextInt(40)+40;
                        a4 = rn.nextInt(40)+40;
                        oper = rn.nextBoolean();
                        quest = a1 + " + " + a2 + " - " + a3 + " + " + a4 + " =";
                        result = a1 + a2 - a3 + a4;
                        break;
                    default:
                        a1 = rn.nextInt(29)+70;
                        a2 = rn.nextInt(29)+70;
                        a3 = rn.nextInt(98)+1;
                        a4 = rn.nextInt(39)+1;
                        oper = rn.nextBoolean();
                        quest = a1 + " + " + a2 + " - " + a3 + (oper ? " + " : " - ") + a4 + " =";
                        result = oper ? a1 + a2 - a3 + a4 : a1 + a2 - a3 - a4;
                        break;
                }
                break;
            case MULT:
                switch (Database.currentLevel){
                    case 1:
                        a1 = rn.nextInt(4)+1;
                        a2 = rn.nextInt(4)+1;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 2:
                        a1 = rn.nextInt(8)+2;
                        a2 = rn.nextInt(4)+1;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 3:
                        a1 = rn.nextInt(8)+2;
                        a2 = rn.nextInt(8)+2;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 4:
                        a1 = rn.nextInt(10)+5;
                        a2 = rn.nextInt(8)+2;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 5:
                        a1 = rn.nextInt(10)+5;
                        a2 = rn.nextInt(10)+5;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 6:
                        a1 = rn.nextInt(9)+10;
                        a2 = rn.nextInt(10)+5;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 7:
                        a1 = rn.nextInt(9)+10;
                        a2 = rn.nextInt(9)+10;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 8:
                        a1 = rn.nextInt(15)+10;
                        a2 = rn.nextInt(9)+10;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    case 9:
                        a1 = rn.nextInt(19)+10;
                        a2 = rn.nextInt(9)+10;
                        quest = a1 + " x " + a2 + " =";
                        result = a1*a2;
                        break;
                    default:
                        a1 = rn.nextInt(19)+10;
                        a2 = rn.nextInt(9)+10;
                        a3 = rn.nextInt(4)+1;
                        quest = a1 + " x " + a2 + " x " + a3 + " =";
                        result = a1*a2*a3;
                        break;
                }
                break;

            case DIVISION:
                switch (Database.currentLevel){
                    case 1:
                        result = 1 + rn.nextInt(3);
                        a2 = 1 + rn.nextInt(3);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 2:
                        result = 1 + rn.nextInt(5);
                        a2 = 1 + rn.nextInt(5);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 3:
                        result = 1 + rn.nextInt(7);
                        a2 = 1 + rn.nextInt(7);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 4:
                        result = 1 + rn.nextInt(9);
                        a2 = 1 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 5:
                        result = 1 + rn.nextInt(9);
                        a2 = 5 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 6:
                        result = 5 + rn.nextInt(10);
                        a2 = 5 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 7:
                        result = 10 + rn.nextInt(9);
                        a2 = 10 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 8:
                        result = 15 + rn.nextInt(10);
                        a2 = 10 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 9:
                        result = 15 + rn.nextInt(10);
                        a2 = 15 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    default:
                        result = 20 + rn.nextInt(9);
                        a2 = 20 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                }
                break;
            case MISC2:
                switch (Database.currentLevel){
                    case 1:
                        result = 1 + rn.nextInt(3);
                        a2 = 1 + rn.nextInt(3);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 2:
                        result = 1 + rn.nextInt(5);
                        a2 = 1 + rn.nextInt(5);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 3:
                        result = 1 + rn.nextInt(7);
                        a2 = 1 + rn.nextInt(7);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 4:
                        result = 1 + rn.nextInt(9);
                        a2 = 1 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 5:
                        result = 1 + rn.nextInt(9);
                        a2 = 5 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 6:
                        result = 5 + rn.nextInt(10);
                        a2 = 5 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 7:
                        result = 10 + rn.nextInt(9);
                        a2 = 10 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 8:
                        result = 15 + rn.nextInt(10);
                        a2 = 10 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    case 9:
                        result = 15 + rn.nextInt(10);
                        a2 = 15 + rn.nextInt(10);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                    default:
                        result = 20 + rn.nextInt(9);
                        a2 = 20 + rn.nextInt(9);
                        a1 = result*a2;
                        quest = a1 + " / " + a2 + " =";
                        break;
                }
        }
        quest = quest + " ";
        correctAnswer = result + "";
        userAnswer = new StringBuffer();
        refreshScreen();
    }

    private void checkAnswer(){
        if (correctAnswer.equals(userAnswer.toString())){
            progressCount++;
            if (progressCount >= COUNTforWIN){
                changeStateToWin();
            } else {
                generateQuest();
            }
        } else {
            changeStateToPreBoom();
            mTracker.send(new HitBuilders.EventBuilder().setAction("wrong answer").setCategory(Database.currentCategory.name()).setLabel("Level " + Database.currentLevel).build());
        }

        refreshScreen();
    }

    protected void changeStateToMainGame(){
        Log.d(TAG, "changeStateToMainGame");

        gameTimer.cancel(true);
        gameState = GameState.MAINGAME;
        generateQuest();
        gameTimer = new GameTimer();
        //gameTimer.execute(gameState);
        gameTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gameState);
        tvComment.setText(getResources().getString(R.string.enterAnswer));
        refreshScreen();
    }

    protected void changeStateToWin(){
        Log.d(TAG, "changeStateToWin");

        gameTimer.cancel(true);
        gameState = GameState.WIN;

        try {
            //soundPool.play(soundWin, 1, 1, 0, 0, 1);
            mediaPlayerWin.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CategoriesEnum currentCategory = Database.currentCategory;
        int progress = preferences.getInt("progress"+currentCategory, 0);
        int countDefusedBomb = preferences.getInt("countDefusedBomb", 0);

        if ((progress == Database.currentLevel-1) && progress != 10)
        {editor.putInt("progress"+currentCategory, ++progress);}

        editor.putInt("countDefusedBomb", ++countDefusedBomb);

        int progressSumm = 1;
        progressSumm += preferences.getInt("progressPLUS", 0);
        progressSumm += preferences.getInt("progressMINUS", 0);
        progressSumm += preferences.getInt("progressMISC1", 0);
        progressSumm += preferences.getInt("progressMULT", 0);
        progressSumm += preferences.getInt("progressDIVISION", 0);

        if (progressSumm >= 20){
            editor.putBoolean("accessibleDIVISION", true);
        } else if (progressSumm >= 15){
            editor.putBoolean("accessibleMULT", true);
        } else if (progressSumm >= 10){
            editor.putBoolean("accessibleMISC1", true);
        } else if (progressSumm >= 5){
            editor.putBoolean("accessibleMINUS", true);
        }

        editor.apply();

        try {
            if (progress == 10){
                switch (currentCategory){
                    case PLUS:
                        Games.Achievements.unlock(gac, getString(R.string.achievement_addition));
                        break;
                    case MINUS:
                        Games.Achievements.unlock(gac, getString(R.string.achievement_subtraction));
                        break;
                    case MISC1:
                        Games.Achievements.unlock(gac, getString(R.string.achievement_ombinations_1));
                        break;
                    case MULT:
                        Games.Achievements.unlock(gac, getString(R.string.achievement_multiplication));
                        break;
                    case DIVISION:
                        Games.Achievements.unlock(gac, getString(R.string.achievement_division));
                        break;
                }
            }
            Games.Achievements.increment(gac, getString(R.string.achievement_100_bombs_defused), 1);
            Games.Leaderboards.submitScore(gac, getString(R.string.leaderboard_bombs_defused), countDefusedBomb);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mTracker.send(new HitBuilders.EventBuilder().setAction("win").setCategory(Database.currentCategory.name()).setLabel("Level " + Database.currentLevel).build());



    }

    protected void changeStateToPreBoom(){
        gameState = GameState.BEFORE_BOOM;
        gameTimer.cancel(true);

        long startTime = System.currentTimeMillis();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int countExplodedBomb = preferences.getInt("countExplodedBomb", 0);
        editor.putInt("countExplodedBomb", ++countExplodedBomb);
        editor.apply();

        Log.d("Preferences time", ((System.currentTimeMillis() - startTime)) + "");

        gameTimer = new GameTimer();
        //gameTimer.execute(gameState);
        gameTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gameState);

    }

    protected void changeStateToBoom(){

        try {
            gameTimer.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(new Intent(GameActivity.this, BoomActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {

        try {
            gameTimer.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(new Intent(GameActivity.this, LevelChooserActivity.class));
        finish();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    private class GameTimer extends AsyncTask<GameState, Integer, Void> {

        private final String TAG = this.getClass().getSimpleName();

        private final long GAME_TIME = 60000 + (Database.currentLevel - 1)*10000;
        private final long BEGIN_TIME = 4000;
        private final long BOOM_TIME = 3000;
        private long startTime, lastMarkTime;


        public GameTimer() {
            startTime = System.currentTimeMillis();
            lastMarkTime = startTime;
        }

        @Override
        protected Void doInBackground(GameState... params) {

            switch (params[0]) {

                case BEGIN:
                    while (!isCancelled()) {

                        long currentTime = System.currentTimeMillis();

                        if (currentTime - lastMarkTime > 1000) {
                            lastMarkTime = currentTime;

                            int minutes = (int) ((BEGIN_TIME + startTime - currentTime) / 1000) / 60;
                            int seconds = (int) ((BEGIN_TIME + startTime - currentTime) / 1000) % 60;

                            publishProgress(minutes, seconds);
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "cancel");
                            //e.printStackTrace();
                        }
                    }
                    break;

                case MAINGAME:
                    while (!isCancelled()) {

                        long currentTime = System.currentTimeMillis();

                        if (currentTime - lastMarkTime > 1000) {
                            lastMarkTime = currentTime;

                                int minutes = (int) ((GAME_TIME + startTime - currentTime) / 1000) / 60;
                                int seconds = (int) ((GAME_TIME + startTime - currentTime) / 1000) % 60;

                            if (seconds%2 == 0){
                                try {
                                    if (sound) {
                                        soundPool.play(soundTime, 1, 1, 0, 0, 1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            publishProgress(minutes, seconds);
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "cancel");
                            //e.printStackTrace();
                        }
                    }
                    break;

                case BEFORE_BOOM:
                    while (!isCancelled()) {

                        long currentTime = System.currentTimeMillis();

                        if (currentTime - lastMarkTime > 1000) {
                            lastMarkTime = currentTime;

                            int minutes = (int) ((BOOM_TIME + startTime - currentTime) / 1000) / 60;
                            int seconds = (int) ((BOOM_TIME + startTime - currentTime) / 1000) % 60;

                            publishProgress(minutes, seconds);
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "cancel");
                            //e.printStackTrace();
                        }
                    }
                    break;


            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);



            switch (gameState){
                case MAINGAME:
                    tvTime.setText(values[0] + ":" + String.format("%02d",values[1]));
                    break;
            }

            if (values[0]==0 && values[1] == 0){

                switch (gameState){
                    case BEGIN:
                        changeStateToMainGame();
                        break;
                    case MAINGAME:
                        changeStateToBoom();
                        try {
                            mTracker.send(new HitBuilders.EventBuilder().setAction("no time").setCategory(Database.currentCategory.name()).setLabel("Level " + Database.currentLevel).build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case BEFORE_BOOM:
                        changeStateToBoom();
                        break;
                }

                this.cancel(true);
            }
        }

    }

    private enum GameState {
        BEGIN, MAINGAME, WIN, BEFORE_BOOM, BOOM
    }
}
