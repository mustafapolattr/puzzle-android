package com.example.puzzle_android.game;

import com.example.puzzle_android.ui.GameView;

public class GameController {

    private GameView gameView;

    public GameController(GameView view) {
        this.gameView = view;
    }

    public void startGame() {
        // Buraya oyun başlangıcındaki grid ve blok kurulumları gelecek
    }

    public void draw(android.graphics.Canvas canvas) {
        // Burada grid ve blokları çizeceğiz (ileride yapılacak)
    }
}
