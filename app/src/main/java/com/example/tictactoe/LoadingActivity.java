package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Znajdź ProgressBar za pomocą jego identyfikatora
        progressBar = findViewById(R.id.progressBar);

        // Ustalenie maksymalnej wartości paska postępu
        progressBar.setMax(100);

        // Tworzenie wątku do symulacji napełniania się paska postępu
        Thread loadingThread = new Thread(() -> {
            try {
                for (int progress = 0; progress <= 100; progress += 10) {
                    // Aktualizuj postęp paska
                    progressBar.setProgress(progress);
                    Thread.sleep(100); // Symulacja pracy przez 0.1 sekundy
                }
            } catch (InterruptedException e) {
                Log.e("LoadingActivity", "Wystąpił błąd: " + e.getMessage());
            } finally {
                // Po zakończeniu pracy paska postępu, przejdź do SettingsActivity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Rozpocznij wątek symulujący pasek postępu
        loadingThread.start();
    }
}
