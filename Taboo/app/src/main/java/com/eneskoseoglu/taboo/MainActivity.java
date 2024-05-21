package com.eneskoseoglu.taboo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eneskoseoglu.taboo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {

    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private int time, skipCount, winScore;
    private String teamA,teamB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = this.getSharedPreferences("com.eneskoseoglu.taboo", Context.MODE_PRIVATE);
        time = sharedPreferences.getInt("time",90);
        skipCount = sharedPreferences.getInt("skip",3);
        winScore = sharedPreferences.getInt("win",20);

        binding.newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                teamA = binding.team1EditText.getText().toString();
                teamB = binding.team2EditText.getText().toString();

                if((!(teamA.isEmpty() || teamB.isEmpty())) && !(teamA.equalsIgnoreCase(teamB))) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("time",time);
                    intent.putExtra("skip",skipCount);
                    intent.putExtra("win",winScore);
                    intent.putExtra("teamA",teamA);
                    intent.putExtra("teamB",teamB);
                    startActivity(intent);
                } else if((teamA.isEmpty() || teamB.isEmpty())) {
                    Toast.makeText(MainActivity.this, "Takım isimleri boş olamaz!", Toast.LENGTH_SHORT).show();
                } else if(teamA.equalsIgnoreCase(teamB)) {
                    Toast.makeText(MainActivity.this, "Takım isimleri aynı olamaz!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        System.out.println(time + " " + skipCount + " " + winScore);

    }

    public void openSettings(){

        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getSupportFragmentManager(),"Settings Dialog");

    }

    @Override
    public void applySettings(int time, int skipCount, int winScore) {
        this.time = time;
        this.skipCount = skipCount;
        this.winScore = winScore;
        sharedPreferences.edit().putInt("time",time).apply();
        sharedPreferences.edit().putInt("skip",skipCount).apply();
        sharedPreferences.edit().putInt("win",winScore).apply();
    }
}