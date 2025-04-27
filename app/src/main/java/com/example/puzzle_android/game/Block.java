package com.example.puzzle_android.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Block {

    private float x, y;
    private BlockShape shape;
    private float cellSize;

    public Block(BlockShape shape, float cellSize) {
        this.shape = shape;
        this.cellSize = cellSize;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (shape == null) return;

        boolean[][] grid = shape.getShape();
        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getCols(); col++) {
                if (grid[row][col]) {
                    float left = x + col * cellSize;
                    float top = y + row * cellSize;
                    float right = left + cellSize;
                    float bottom = top + cellSize;

                    paint.setColor(Color.rgb(250, 200, 150)); // Açık tuğla rengi
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, paint);

                    paint.setColor(Color.BLACK);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, paint);
                }
            }
        }
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCellSize() {
        return cellSize;
    }

    public BlockShape getShape() {
        return shape;
    }
}
