package com.example.puzzle_android.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.puzzle_android.ui.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.view.MotionEvent;

public class GameController {

    private static final int GRID_SIZE = 9;
    private Tile[][] tiles = new Tile[GRID_SIZE][GRID_SIZE];

    private float tileSize;
    private float margin = 5; // tile'lar arası boşluk
    private GameView gameView;
    private List<Block> blocks = new ArrayList<>();
    private Block selectedBlock = null;
    private float offsetX, offsetY;
    private int score = 0;

    public GameController(GameView gameView) {
        this.gameView = gameView;
    }

    public void startGame() {
        setupGrid();
        createBlocks();
    }

    private void setupGrid() {
        int canvasWidth = gameView.getWidth();
        int canvasHeight = gameView.getHeight();
        float screenWidth = Math.min(canvasWidth, canvasHeight);

        tileSize = (screenWidth * 0.9f) / GRID_SIZE;

        float totalGridWidth = (tileSize + margin) * GRID_SIZE - margin;
        float totalGridHeight = totalGridWidth;

        float startX = (canvasWidth - totalGridWidth) / 2f;
        float startY = (canvasHeight - totalGridHeight) / 2f;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                float x = startX + col * (tileSize + margin);
                float y = startY + row * (tileSize + margin);
                tiles[row][col] = new Tile(x, y, tileSize);
            }
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();

        // Grid çizimi
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                tiles[row][col].draw(canvas, paint);
            }
        }

        // Bloklar çizimi
        for (Block block : blocks) {
            block.draw(canvas, paint);
        }
    }


    private void createBlocks() {
        blocks.clear();
        Random random = new Random();

        List<BlockShape> availableShapes = new ArrayList<>();

        if (score < 50) {
            availableShapes.addAll(BlockShapeFactory.easyShapes());
        } else if (score < 100) {
            availableShapes.addAll(BlockShapeFactory.easyShapes());
            availableShapes.addAll(BlockShapeFactory.mediumShapes());
        } else {
            availableShapes.addAll(BlockShapeFactory.easyShapes());
            availableShapes.addAll(BlockShapeFactory.mediumShapes());
            availableShapes.addAll(BlockShapeFactory.hardShapes());
        }

        int canvasWidth = gameView.getWidth();
        int canvasHeight = gameView.getHeight();
        float screenWidth = Math.min(canvasWidth, canvasHeight);

        float cellSize = (screenWidth * 0.1f); // Hücre boyutu

        int blockCount = 3;
        float startX = (canvasWidth - (blockCount * 3 * cellSize)) / 2f;
        float startY = canvasHeight * 0.1f;

        for (int i = 0; i < blockCount; i++) {
            BlockShape randomShape = availableShapes.get(random.nextInt(availableShapes.size()));
            Block block = new Block(randomShape, cellSize);
            block.setPosition(startX + (i * 3 * cellSize), startY);
            block.saveOriginalPosition();
            blocks.add(block);
        }
    }

    public void handleTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Block block : blocks) {
                    float left = block.getX();
                    float top = block.getY();
                    float right = left + block.getShape().getCols() * block.getCellSize();
                    float bottom = top + block.getShape().getRows() * block.getCellSize();

                    if (touchX >= left && touchX <= right && touchY >= top && touchY <= bottom) {
                        selectedBlock = block;
                        offsetX = touchX - left;
                        offsetY = touchY - top;
                        selectedBlock.saveOriginalPosition(); // 💥 BU SATIRI EKLE!
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (selectedBlock != null) {
                    selectedBlock.setPosition(touchX - offsetX, touchY - offsetY);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (selectedBlock != null) {
                    if (tryPlaceBlock(selectedBlock, event.getX(), event.getY())) {
                        blocks.remove(selectedBlock);
                    } else {
                        selectedBlock.resetPosition();
                    }

                    if (blocks.isEmpty()) {
                        createBlocks();
                    }

                    if (!canAnyBlockBePlaced()) {
                        showGameOver();
                    }

                    selectedBlock = null;
                }
                break;
        }

    }

    private boolean tryPlaceBlock(Block block, float touchX, float touchY) {
        float cellSize = block.getCellSize();
        BlockShape shape = block.getShape();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                float tileCenterX = tile.getX() + tile.getSize() / 2;
                float tileCenterY = tile.getY() + tile.getSize() / 2;

                float distance = (float) Math.sqrt(Math.pow(touchX - tileCenterX, 2) + Math.pow(touchY - tileCenterY, 2));
                if (distance < cellSize) {
                    if (canPlaceBlockAt(row, col, block)) {
                        placeBlockAt(row, col, block);

                        blocks.remove(block);

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean canPlaceBlockAt(int startRow, int startCol, Block block) {
        BlockShape shape = block.getShape();
        boolean[][] pattern = shape.getShape();
        int shapeRows = pattern.length;
        int shapeCols = pattern[0].length;

        if (startRow + shapeRows > GRID_SIZE || startCol + shapeCols > GRID_SIZE) {
            return false; // Grid dışına taşıyor
        }

        for (int row = 0; row < shapeRows; row++) {
            for (int col = 0; col < shapeCols; col++) {
                if (pattern[row][col]) { // Blokta bu hücre aktifse
                    if (tiles[startRow + row][startCol + col].isOccupied()) {
                        return false; // Grid hücresi doluysa yerleşemez
                    }
                }
            }
        }
        return true; // Her şey uygunsa yerleşebilir
    }


    private void placeBlockAt(int startRow, int startCol, Block block) {
        BlockShape shape = block.getShape();

        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getCols(); col++) {
                if (shape.getShape()[row][col]) {
                    tiles[startRow + row][startCol + col].setOccupied(true);
                }
            }
        }
        checkAndClearLines(); // Blok yerleşince satır/sütun kontrolü
    }

    private void checkAndClearLines() {
        List<Integer> fullRows = new ArrayList<>();
        List<Integer> fullCols = new ArrayList<>();

        for (int row = 0; row < GRID_SIZE; row++) {
            boolean fullRow = true;
            for (int col = 0; col < GRID_SIZE; col++) {
                if (!tiles[row][col].isOccupied()) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                fullRows.add(row);
            }
        }

        for (int col = 0; col < GRID_SIZE; col++) {
            boolean fullCol = true;
            for (int row = 0; row < GRID_SIZE; row++) {
                if (!tiles[row][col].isOccupied()) {
                    fullCol = false;
                    break;
                }
            }
            if (fullCol) {
                fullCols.add(col);
            }
        }

        // Satırları temizle
        for (int row : fullRows) {
            for (int col = 0; col < GRID_SIZE; col++) {
                tiles[row][col].setOccupied(false);
            }
        }

        // Sütunları temizle
        for (int col : fullCols) {
            for (int row = 0; row < GRID_SIZE; row++) {
                tiles[row][col].setOccupied(false);
            }
        }

        // Skoru güncelle
        int totalCleared = fullRows.size() + fullCols.size();
        if (totalCleared > 0) {
            score += totalCleared * 10; // Örnek: her satır/sütun için +10 puan
        }
    }


    private boolean canAnyBlockBePlaced() {
        for (Block block : blocks) {
            BlockShape shape = block.getShape();
            int shapeRows = shape.getRows();
            int shapeCols = shape.getCols();

            for (int startRow = 0; startRow <= GRID_SIZE - shapeRows; startRow++) {
                for (int startCol = 0; startCol <= GRID_SIZE - shapeCols; startCol++) {
                    if (canPlaceBlockAt(startRow, startCol, block)) {
                        return true; // Yerleştirilebiliyor
                    }
                }
            }
        }
        return false; // Hiçbir yere yerleşemiyor
    }


    private void showGameOver() {
        new android.app.AlertDialog.Builder(gameView.getContext())
                .setTitle("Game Over")
                .setMessage("No more moves available!")
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> {
                    restartGame();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    // Şu an için çıkmak istiyorsa sadece dialog kapansın
                    dialog.dismiss();
                })
                .show();
    }

    private void restartGame() {
        // Tüm tiles'ı boşalt
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                tiles[row][col].setOccupied(false);
            }
        }
        blocks.clear();
        createBlocks();
        score = 0;
    }
}
