package com.example.puzzle_android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.example.puzzle_android.game.GameController;

public class GameView extends View {

    private GameController gameController;

    public GameView(Context context) {
        super(context);
        gameController = new GameController(this);
        gameController.startGame();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gameController.draw(canvas);
        invalidate(); // Sürekli yeniden çiz
    }

}
