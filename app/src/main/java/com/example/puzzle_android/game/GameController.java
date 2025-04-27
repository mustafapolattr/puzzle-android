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

        List<BlockShape> shapes = BlockShapeFactory.basicShapes();
        Random random = new Random();

        int canvasWidth = gameView.getWidth();
        int canvasHeight = gameView.getHeight();
        float screenWidth = Math.min(canvasWidth, canvasHeight);

        float cellSize = (screenWidth * 0.1f); // Blok hücre boyutu

        int blockCount = 3;
        float startX = (canvasWidth - (blockCount * 3 * cellSize)) / 2f;
        float startY = canvasHeight * 0.1f; // Alt kısım

        for (int i = 0; i < blockCount; i++) {
            BlockShape randomShape = shapes.get(random.nextInt(shapes.size()));
            Block block = new Block(randomShape, cellSize);
            block.setPosition(startX + (i * 3 * cellSize), startY);
            block.saveOriginalPosition();
            blocks.add(block);
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
                        blocks.remove(selectedBlock); // 💥 Başarıyla yerleştiyse hemen sil
                        if (blocks.isEmpty()) {
                            createBlocks();
                        }
                    } else {
                        selectedBlock.resetPosition(); // Yerleşemediyse eski yerine dön
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

                        // 💥 Bloğu burada hemen sahneden kaldırıyoruz
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

        // Grid dışına taşma kontrolü
        if (startRow + shape.getRows() > GRID_SIZE || startCol + shape.getCols() > GRID_SIZE) {
            return false;
        }

        // Her hücre için doluluk kontrolü
        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getCols(); col++) {
                if (shape.getShape()[row][col]) {
                    if (tiles[startRow + row][startCol + col].isOccupied()) {
                        return false;
                    }
                }
            }
        }
        return true;
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

        // Satırları kontrol et
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

        // Sütunları kontrol et
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
    }

}
