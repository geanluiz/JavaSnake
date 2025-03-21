import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 400;
    static final int SCREEN_HEIGHT = 440;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
    static final int DELAY = 100;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.darkGray);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();

        for(int i = 0; i< bodyParts;i++) {
            x[i] = 0;
            y[i] = 40;
        }

        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void restartGame() {
        bodyParts = 6;
        direction = 'R';
        applesEaten = 0;
        startGame();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if(running) {
            // Apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Head and body parts
            for(int i = 0; i< bodyParts;i++) {
                if (i == 0 ) {
                    g.setColor(Color.lightGray);
                    g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(Color.gray);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Score text
            g.setColor(Color.red);
            g.setFont( new Font("Arial",Font.PLAIN, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

            if (!timer.isRunning()) {
                gamePaused(g);
            }
        }
        else {
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = (random.nextInt((SCREEN_WIDTH/UNIT_SIZE)-2)*UNIT_SIZE)+UNIT_SIZE;
        appleY = (random.nextInt(((SCREEN_HEIGHT-40)/UNIT_SIZE)-2)*UNIT_SIZE)+40+UNIT_SIZE;
    }

    public void move(){

        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
            break;
        }
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts;i>0;i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        if(!running) {
            timer.stop();
        }

        //check if head touches left border
        if(x[0] < 0) {
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        }

        //check if head touches right border
        if(x[0] > (SCREEN_WIDTH - UNIT_SIZE)) {
            x[0] = 0;
        }

        //check if head touches top border
        if(y[0] < 40) {
            y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        }

        //check if head touches bottom border
        if(y[0] > (SCREEN_HEIGHT - UNIT_SIZE)) {
            y[0] = 40;
        }
    }

    public void gameOver(Graphics g) {
        //Score text
        g.setColor(Color.yellow);
        g.setFont( new Font("Arial",Font.PLAIN, 30));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten,
                (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        //Game Over
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        // New game
        g.setColor(Color.yellow);
        g.setFont( new Font("Arial",Font.PLAIN, 15));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Enter to start new game.",
                (SCREEN_WIDTH - metrics3.stringWidth("Press Enter to start new game."))/2, (SCREEN_HEIGHT/2)+30);
    }

    public void gamePaused(Graphics g ) {
        // Game paused
        g.setColor(Color.yellow);
        g.setFont( new Font("Arial",Font.PLAIN, 15));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Game paused. Press enter to continue.",
                (SCREEN_WIDTH - metrics4.stringWidth("Game paused. Press enter to continue."))/2, (SCREEN_HEIGHT/2));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {direction = 'L';}
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {direction = 'R';}
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {direction = 'U';}
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {direction = 'D';}
                    break;
                case KeyEvent.VK_ENTER:
                    // New Game
                    if (!running) {
                        restartGame();
                    }
                    // Pause
                    else if (timer.isRunning()){
                        timer.stop();
                        repaint();
                    }
                    else {
                        timer.start();
                        repaint();
                    }
            }
        }
    }
}