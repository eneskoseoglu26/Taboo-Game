package com.eneskoseoglu.taboo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eneskoseoglu.taboo.databinding.ActivityGameBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    private int totalTime; // Toplam süre saniye cinsinden
    private int currentTime;
    private final int TIMER_INTERVAL = 1000; // Timer güncelleme aralığı, burada 1000 milisaniye (1 saniye)
    private int skipCount;
    private int scoreA,scoreB;
    private int winScore;
    private int i;
    private boolean currentTeam = true;
    private String teamA,teamB;
    private ArrayList<Word> words;


    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    private CountDownTimer timer;

    private Dialog dialog;
    private Button dialogNextBtn;
    private TextView teamAText,teamBText, teamAScoreText,teamBScoreText;
    private TextView nextTeamText,roundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        totalTime = intent.getIntExtra("time",90);
        currentTime = totalTime;
        skipCount = intent.getIntExtra("skip",3);
        teamA = intent.getStringExtra("teamA");
        teamB = intent.getStringExtra("teamB");
        winScore = intent.getIntExtra("win",20);
        scoreA = 0;
        scoreB = 0;

        binding.skipCountTextView.setText("("+skipCount+")");
        binding.scoreTextView.setText(String.valueOf(scoreA));
        binding.teamTextView.setText(teamA);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_score_info);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.layout_score_info_background));
        dialog.setCancelable(false);

        dialogNextBtn = dialog.findViewById(R.id.nextTeamButton);
        nextTeamText = dialog.findViewById(R.id.nextTeamTextView);
        teamAText = dialog.findViewById(R.id.teamATextView);
        teamBText = dialog.findViewById(R.id.teamBTextView);
        teamAScoreText = dialog.findViewById(R.id.teamAscoreTextView);
        teamBScoreText = dialog.findViewById(R.id.teamBscoreTextView);
        roundTextView = dialog.findViewById(R.id.roundTextView);

        dialogNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTeamCheck();
                totalTime = intent.getIntExtra("time",90);
                currentTime = totalTime;
                skipCount = intent.getIntExtra("skip",3);
                nextWord();
                dialog.dismiss();
                startGame();
            }
        });

        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            databaseHelper.createDatabase();
            database = databaseHelper.getReadableDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        words = new ArrayList<>();
        getWords();
        Collections.shuffle(words);
        i = 0;

        binding.wordTextView.setText(words.get(i).mainWord);
        binding.word1TextView.setText(words.get(i).forbidWord1);
        binding.word2TextView.setText(words.get(i).forbidWord2);
        binding.word3TextView.setText(words.get(i).forbidWord3);
        binding.word4TextView.setText(words.get(i).forbidWord4);

        startGame();
    }

    public void nextWord() {
        i++;
        binding.wordTextView.setText(words.get(i).mainWord);
        binding.word1TextView.setText(words.get(i).forbidWord1);
        binding.word2TextView.setText(words.get(i).forbidWord2);
        binding.word3TextView.setText(words.get(i).forbidWord3);
        binding.word4TextView.setText(words.get(i).forbidWord4);
    }

    public void addScore(View view) {

        if(currentTeam){
            scoreA++;
            binding.scoreTextView.setText(String.valueOf(scoreA));
        } else {
            scoreB++;
            binding.scoreTextView.setText(String.valueOf(scoreB));
        }

        if((scoreA == winScore)||(scoreB == winScore)) {
            timer.cancel();
            binding.correctButton.setEnabled(false);
            binding.nextLayout.setEnabled(false);
            binding.falseButton.setEnabled(false);

            if(currentTeam){
                Toast.makeText(this, "KAZANAN TAKIM : " +teamA, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "KAZANAN TAKIM : " +teamB, Toast.LENGTH_LONG).show();
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
            alert.setTitle("Yeni Oyun?");
            alert.setMessage("Yeni oyuna başlamak ister misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recreate();
                }
            });
            alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            alert.show();

        } else {
            nextWord();
        }
    }

    public void decreaseScore(View view) {
        if(currentTeam){
            scoreA--;
            binding.scoreTextView.setText(String.valueOf(scoreA));
        } else {
            scoreB--;
            binding.scoreTextView.setText(String.valueOf(scoreB));
        }
        nextWord();
    }

    public void decreaseSkip(View view) {
        if(skipCount == 0) {
            Toast.makeText(this, "Pas Hakkı Doldu!", Toast.LENGTH_SHORT).show();
        } else {
            skipCount--;
            binding.skipCountTextView.setText("("+skipCount+")");
            nextWord();
        }
    }

    @SuppressLint("Range")
    public void getWords() {

        cursor = database.rawQuery("SELECT * from kelimeler", null);
        while (cursor.moveToNext()) {
            String mainWord = cursor.getString(cursor.getColumnIndex("kelime")).toUpperCase();
            String forbidWord1 = cursor.getString(cursor.getColumnIndex("yasakli1")).toUpperCase();
            String forbidWord2 = cursor.getString(cursor.getColumnIndex("yasakli2")).toUpperCase();
            String forbidWord3 = cursor.getString(cursor.getColumnIndex("yasakli3")).toUpperCase();
            String forbidWord4 = cursor.getString(cursor.getColumnIndex("yasakli4")).toUpperCase();
            words.add(new Word(mainWord,forbidWord1,forbidWord2,forbidWord3,forbidWord4));
        }

    }

    public void startGame() {
         timer = new CountDownTimer(totalTime * 1000, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Süreyi her saniyede bir güncelle
                currentTime--;
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                // Zamanlayıcı tamamlandığında yapılacak işlemler
                Toast.makeText(GameActivity.this, "Süre Doldu!", Toast.LENGTH_SHORT).show();
                teamAText.setText(teamA);
                teamBText.setText(teamB);
                teamAScoreText.setText(String.valueOf(scoreA));
                teamBScoreText.setText(String.valueOf(scoreB));

                if(currentTeam) {
                    currentTeam = false;
                    nextTeamText.setText("Sıradaki Takım => " + teamB);
                } else {
                    currentTeam = true;
                    nextTeamText.setText("Sıradaki Takım => " + teamA);
                }

                dialog.show();

            }
        }.start();
    }

    public void updateProgressBar() {
        // ProgressBar'ı güncelle
        int progress = (int) ((float) currentTime / totalTime * 100);
        binding.progressBar.setProgress(progress);
        binding.timerTextView.setText(String.valueOf(currentTime));
    }

    public void currentTeamCheck() {

        if(currentTeam) {
            binding.scoreTextView.setText(String.valueOf(scoreA));
            binding.teamTextView.setText(teamA);
        } else {
            binding.scoreTextView.setText(String.valueOf(scoreB));
            binding.teamTextView.setText(teamB);
        }

    }

}