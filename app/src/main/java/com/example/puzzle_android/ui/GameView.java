package com.example.puzzle_android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.example.puzzle_android.game.GameController;

public class GameView extends View {

    private GameController gameController;

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // boyutlar burada garanti edilir
        gameController = new GameController(this);
        gameController.startGame();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameController != null) {
            gameController.draw(canvas);
        }

        invalidate(); // sürekli yeniden çizim
    }
}
