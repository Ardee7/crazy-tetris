package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed, quickPressed;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if(code == KeyEvent.VK_SPACE) quickPressed = true;
        if (code == KeyEvent.VK_ENTER) pausePressed = !pausePressed;

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) { // Check for space bar release
            quickPressed = false; // Reset quickPressed when released
        }
    }
}
