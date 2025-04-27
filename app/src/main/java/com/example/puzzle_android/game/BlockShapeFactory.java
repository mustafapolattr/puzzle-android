package com.example.puzzle_android.game;

import java.util.Arrays;
import java.util.List;

public class BlockShapeFactory {

    public static List<BlockShape> basicShapes() {
        return Arrays.asList(
                new BlockShape(
                        "two_line",
                        1, 2,
                        new boolean[][] {
                                { true, true }
                        },
                        DifficultyLevel.EASY
                ),
                new BlockShape(
                        "square",
                        2, 2,
                        new boolean[][] {
                                { true, true },
                                { true, true }
                        },
                        DifficultyLevel.EASY
                ),
                new BlockShape(
                        "corner",
                        2, 2,
                        new boolean[][] {
                                { true, true },
                                { true, false }
                        },
                        DifficultyLevel.EASY
                )
        );
    }
}
