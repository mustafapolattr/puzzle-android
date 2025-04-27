package com.example.puzzle_android.game;

public class BlockShape {

    private String name;
    private int rows;
    private int cols;
    private boolean[][] shape;
    private DifficultyLevel difficulty;

    public BlockShape(String name, int rows, int cols, boolean[][] shape, DifficultyLevel difficulty) {
        this.name = name;
        this.rows = rows;
        this.cols = cols;
        this.shape = shape;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }
}
