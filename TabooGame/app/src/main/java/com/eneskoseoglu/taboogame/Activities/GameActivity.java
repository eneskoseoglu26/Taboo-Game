package com.eneskoseoglu.taboogame.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.eneskoseoglu.taboogame.Helper.DatabaseHelper;
import com.eneskoseoglu.taboogame.MainActivity;
import com.eneskoseoglu.taboogame.Models.Word;
import com.eneskoseoglu.taboogame.R;
import com.eneskoseoglu.taboogame.databinding.ActivityGameBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    int score,skip,time;
    ActivityGameBinding binding;
    ArrayList<Word> wordArrayList;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor c;
    int i;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        i = 0;
        score = 0;
        skip = 10;
        time = 90000;
        binding.timeTextView.setText(String.valueOf(time/1000));
        binding.scoreCountView.setText(String.valueOf(score));
        binding.skipCountView.setText(String.valueOf(skip));

        try {
            dbHelper = new DatabaseHelper(getApplicationContext());
            dbHelper.createDatabase();
            db = dbHelper.getReadableDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        wordArrayList = new ArrayList<>();
        addWords(wordArrayList);
        Collections.shuffle(wordArrayList);

        binding.wordTextView.setText(wordArrayList.get(i).mainWord);
        binding.word1TextView.setText(wordArrayList.get(i).word1);
        binding.word2TextView.setText(wordArrayList.get(i).word2);
        binding.word3TextView.setText(wordArrayList.get(i).word3);
        binding.word4TextView.setText(wordArrayList.get(i).word4);

        startGame();

    }

    public void addScore(View view) {

        score++;
        binding.scoreCountView.setText(String.valueOf(score));
        nextWord();

    }

    public void decreaseScore(View view) {

        score--;
        binding.scoreCountView.setText(String.valueOf(score));
        nextWord();

    }

    public void decreaseSkip(View view) {

        if(skip == 0) {

            Toast.makeText(this, "Pas Hakkı Doldu!", Toast.LENGTH_SHORT).show();

        } else {

            skip--;
            binding.skipCountView.setText(String.valueOf(skip));
            nextWord();
        }

    }

    public void nextWord() {

        i++;
        binding.wordTextView.setText(wordArrayList.get(i).mainWord);
        binding.word1TextView.setText(wordArrayList.get(i).word1);
        binding.word2TextView.setText(wordArrayList.get(i).word2);
        binding.word3TextView.setText(wordArrayList.get(i).word3);
        binding.word4TextView.setText(wordArrayList.get(i).word4);


    }

    @SuppressLint("Range")
    public void addWords(ArrayList<Word> wordArrayList) {

        c = db.rawQuery("SELECT * from words", null);
        while (c.moveToNext()) {
            String main_word = c.getString(c.getColumnIndex("main_word")).toUpperCase();
            String word1 = c.getString(c.getColumnIndex("word1")).toUpperCase();
            String word2 = c.getString(c.getColumnIndex("word2")).toUpperCase();
            String word3 = c.getString(c.getColumnIndex("word3")).toUpperCase();
            String word4 = c.getString(c.getColumnIndex("word4")).toUpperCase();
            wordArrayList.add(new Word(main_word,word1,word2,word3,word4));
        }

    }


    public void startGame() {

        countDownTimer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                binding.timeTextView.setText(String.valueOf(millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {

                Toast.makeText(GameActivity.this, "Süre Doldu!", Toast.LENGTH_SHORT).show();
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

            }
        }.start();

    }


}