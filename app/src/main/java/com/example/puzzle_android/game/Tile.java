package com.example.puzzle_android.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Tile {

    private float x, y;
    private float size;
    private boolean occupied = false;

    public Tile(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(occupied ? Color.DKGRAY : Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);

        RectF rect = new RectF(x, y, x + size, y + size);
        canvas.drawRoundRect(rect, 10, 10, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(rect, 10, 10, paint);
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public float getSize() { return size; }
}
