package main;

import minos.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    // main.Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;  // created another mino and its x and y below
    final int NEXT_MINO_X;
    final int NEXT_MINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>(); // put the inactive mino in the static block

    // Others
    public static int dropInterval = 60; // mino drops in every 60 frames
    public boolean gameOver;

    // effects

    boolean effectsCounterOn;

    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Scores and levels
    int level = 1;
    int lines;
    int score;

    public PlayManager() {
        //main.Main Play Area Frame
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXT_MINO_X = right_x + 175;
        NEXT_MINO_Y = top_y + 500;


        // set the starting mino

        currentMino = upComingMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = upComingMino();
        nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

    }

    public void update() {
        // Check if the currentMino is active
        if (!currentMino.active) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // game over check
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
// this means the current mino immediately collided a block and couldn't move at all
                // so its x and y are same as next mino
                // no space left so
                // game is over
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2,false);
            }

            currentMino.deactivating = false; // reset

            // replace the currentMino with the nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = upComingMino();
            nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

            // check if a mino is inactive and check if line can be deleted
            checkDelete();
        } else
            currentMino.update();

    }

    public void draw(Graphics2D g2) {
        // Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        //Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("Up next 🤪", x + 33, y - 20);


        // score
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("Level : " + level, x, y);
        y += 70;
        g2.drawString("Lines : " + lines, x, y);
        y += 70;
        g2.drawString("Score : " + score, x, y);

        // Draw the currentMino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // draw pause or game over

        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(150f));
        if (gameOver) {
            x = left_x - 268;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        } else if (KeyHandler.pausePressed) {
            x = left_x - 120;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        // draw minos
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        nextMino.draw(g2);  // show next mino

        for (Block staticBlock : staticBlocks) { // scan the list and draw one by one
            staticBlock.draw(g2);
        }

        if (effectsCounterOn) {
            effectCounter++;
            g2.setColor(Color.white);

            for (Integer integer : effectY) {
                g2.fillRect(left_x, integer, WIDTH, Block.SIZE);
            }
            if (effectCounter == 10) {

                effectsCounterOn = false;
                effectCounter = 0;
                effectY.clear();

            }

        }
    }

    private Mino upComingMino() {
        Mino mino = null;
        int i = new Random().nextInt(7); // used random class to generate mino (0-6)

        mino = switch (i) {
            case 0 -> new Mino_L1();
            case 1 -> new Mino_L2();
            case 2 -> new Mino_Square();
            case 3 -> new Mino_Bar();
            case 4 -> new Mino_T();
            case 5 -> new Mino_Z1();
            case 6 -> new Mino_Z2();
            default -> mino;
        };
        return mino;
    }

    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        // so maximum number of blocks are 12 if one line got 12 blocks we can delete line
        while (x < right_x && y < bottom_y) {

            for (Block staticBlock : PlayManager.staticBlocks) { // scanning
                if (staticBlock.x == x && staticBlock.y == y) {
                    // increase the count
                    blockCount++;

                }
            }

            x += Block.SIZE;
            if (x == right_x) {

                // line filled and can be deleted
                if (blockCount == 12) {

                    effectsCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i > -1; i--) {
                        // remove all the blocks in current y line
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }

                    }
                    lineCount++;
                    lines++;
                    // to increase level + speed after every 10 line

                    if (lines % 10 == 0 && dropInterval > 1) {
                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 20;
                        } else {
                            dropInterval -= 1;
                        }
                    }

                    // a line has deleted so need to move down blocks
                    for (Block staticBlock : staticBlocks) {

                        // if a block is above the current y , move it down by the block size

                        if (staticBlock.y < y) {
                            staticBlock.y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0; // reset when x reaches the right x because it goes to the next row
                x = left_x;
                y += Block.SIZE;
            }
        }
        if (lineCount > 0) {
//            GamePanel.se.play(1,false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }
}
