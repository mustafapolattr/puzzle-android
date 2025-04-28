package com.example.puzzle_android.game;

import java.util.Arrays;
import java.util.List;

public class BlockShapeFactory {

    public static List<BlockShape> easyShapes() {
        return Arrays.asList(
                new BlockShape("two_line", 1, 2, new boolean[][]{{true, true}}, DifficultyLevel.EASY),
                new BlockShape("square", 2, 2, new boolean[][]{{true, true}, {true, true}}, DifficultyLevel.EASY),
                new BlockShape("corner", 2, 2, new boolean[][]{{true, true}, {true, false}}, DifficultyLevel.EASY)
        );
    }

    public static List<BlockShape> mediumShapes() {
        return Arrays.asList(
                new BlockShape("three_line", 1, 3, new boolean[][]{{true, true, true}}, DifficultyLevel.MEDIUM),
                new BlockShape("big_corner", 3, 3, new boolean[][]{
                        {true, true, false},
                        {true, false, false},
                        {true, false, false}
                }, DifficultyLevel.MEDIUM)
        );
    }

    public static List<BlockShape> hardShapes() {
        return Arrays.asList(
                new BlockShape("cross", 3, 3, new boolean[][]{
                        {false, true, false},
                        {true, true, true},
                        {false, true, false}
                }, DifficultyLevel.HARD),
                new BlockShape("zigzag", 3, 3, new boolean[][]{
                        {true, true, false},
                        {false, true, true},
                        {false, false, true}
                }, DifficultyLevel.HARD)
        );
    }
}

