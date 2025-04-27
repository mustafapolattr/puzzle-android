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
            blocks.add(block);
        }
    }

    public void handleTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Parmağın ilk dokunduğu anda hangi bloğun altında olduğunu kontrol et
                for (Block block : blocks) {
                    float left = block.getX();
                    float top = block.getY();
                    float right = left + block.getShape().getCols() * block.getCellSize();
                    float bottom = top + block.getShape().getRows() * block.getCellSize();

                    if (touchX >= left && touchX <= right && touchY >= top && touchY <= bottom) {
                        selectedBlock = block;
                        offsetX = touchX - left;
                        offsetY = touchY - top;
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
                    if (tryPlaceBlock(selectedBlock)) {
                        // Başarılı yerleştirildiyse bloğu sahneden kaldır
                        blocks.remove(selectedBlock);
                    } else {
                        // Başarısızsa geri eski pozisyona dön
                        selectedBlock.resetPosition();
                    }
                    selectedBlock = null;
                }
                break;
        }
    }

    private boolean tryPlaceBlock(Block block) {
        float blockX = block.getX();
        float blockY = block.getY();
        float cellSize = block.getCellSize();
        BlockShape shape = block.getShape();

        for (int startRow = 0; startRow <= GRID_SIZE - shape.getRows(); startRow++) {
            for (int startCol = 0; startCol <= GRID_SIZE - shape.getCols(); startCol++) {
                boolean canPlace = true;

                for (int row = 0; row < shape.getRows(); row++) {
                    for (int col = 0; col < shape.getCols(); col++) {
                        if (shape.getShape()[row][col]) {
                            Tile tile = tiles[startRow + row][startCol + col];
                            if (tile.isOccupied()) {
                                canPlace = false;
                                break;
                            }
                        }
                    }
                    if (!canPlace) break;
                }

                if (canPlace) {
                    // Yerleştir
                    for (int row = 0; row < shape.getRows(); row++) {
                        for (int col = 0; col < shape.getCols(); col++) {
                            if (shape.getShape()[row][col]) {
                                Tile tile = tiles[startRow + row][startCol + col];
                                tile.setOccupied(true);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
