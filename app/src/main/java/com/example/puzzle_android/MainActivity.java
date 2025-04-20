package com.example.puzzle_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.puzzle_android.ui.GameView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this)); // XML yerine direkt View kullanıyoruz
    }
}
