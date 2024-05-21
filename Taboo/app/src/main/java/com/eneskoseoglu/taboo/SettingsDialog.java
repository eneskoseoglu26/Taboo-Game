package com.eneskoseoglu.taboo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.eneskoseoglu.taboo.databinding.LayoutSettingsBinding;

public class SettingsDialog extends AppCompatDialogFragment {

    private LayoutSettingsBinding binding;
    private SettingsDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = LayoutSettingsBinding.inflate(LayoutInflater.from(getActivity()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setTime();
        setSkipCount();
        setWinScore();

        builder.setView(binding.getRoot())
                .setTitle("Ayarlar")
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Uygula", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int time = Integer.parseInt(binding.timeTextView.getText().toString().substring(5));
                            int skip = Integer.parseInt(binding.skipTextView.getText().toString().substring(10));
                            int winScore = Integer.parseInt(binding.winScoreTextView.getText().toString().substring(14));
                            listener.applySettings(time,skip,winScore);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Ayarlar boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SettingsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SettingsDialogListener");
        }
    }

    public interface SettingsDialogListener {
        void applySettings(int time, int skipCount, int winScore);
    }

    public void setTime() {

        binding.timeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.timeTextView.setText("Süre:" + binding.timeButton1.getText().toString());
            }
        });

        binding.timeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.timeTextView.setText("Süre:" + binding.timeButton2.getText().toString());
            }
        });

        binding.timeButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.timeTextView.setText("Süre:" + binding.timeButton3.getText().toString());
            }
        });
    }

    public void setSkipCount() {

        binding.skipButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.skipTextView.setText("Pas Hakkı:" + binding.skipButton1.getText().toString());
            }
        });

        binding.skipButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.skipTextView.setText("Pas Hakkı:" + binding.skipButton2.getText().toString());
            }
        });

        binding.skipButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.skipTextView.setText("Pas Hakkı:" + binding.skipButton3.getText().toString());
            }
        });

    }

    public void setWinScore() {

        binding.winScoreButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.winScoreTextView.setText("Kazanma Skoru:" + binding.winScoreButton1.getText().toString());
            }
        });

        binding.winScoreButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.winScoreTextView.setText("Kazanma Skoru:" + binding.winScoreButton2.getText().toString());
            }
        });

        binding.winScoreButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.winScoreTextView.setText("Kazanma Skoru:" + binding.winScoreButton3.getText().toString());
            }
        });


    }

}
