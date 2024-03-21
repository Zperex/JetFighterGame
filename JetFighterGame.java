import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;

public class JetFighterGame extends JFrame implements ActionListener, KeyListener {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 700;
    private static final int PLAYER_SIZE = 60;
    private static final int ENEMY_SIZE = 40;
    private static final int BULLET_SIZE = 15;
    private static final int PLAYER_SPEED = 5;
    private static final int ENEMY_SPEED = 3;
    private static final int BULLET_SPEED = 10;
    private static final int ENEMY_FIRE_RATE = 1000; // milliseconds

    private ImageIcon playerIcon;
    private ImageIcon enemyIcon;
    private ImageIcon backgroundImage;
    private ImageIcon bulletIcon;
    private int score = 0;
    private int playerX = WIDTH / 2 - PLAYER_SIZE / 2;
    private int playerY = HEIGHT - 2 * PLAYER_SIZE;
    private int playerSpeedX = 0;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Bullet> playerBullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();

    public JetFighterGame() {
        setTitle("Jet Fighter Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);
        URL playerUrl = getClass().getResource("player.png");
        URL enemyUrl = getClass().getResource("enemy.png");
        URL backgroundUrl = getClass().getResource("background.png");
        URL bulletUrl = getClass().getResource("bullet.png");
        if (playerUrl != null && enemyUrl != null && backgroundUrl != null && bulletUrl != null) {
            playerIcon = new ImageIcon(playerUrl);
            enemyIcon = new ImageIcon(enemyUrl);
            backgroundImage = new ImageIcon(backgroundUrl);
            bulletIcon = new ImageIcon(bulletUrl);
            System.out.println("Images loaded successfully.");
        } else {
            System.err.println("Error loading images.");
            System.exit(1);
        }

        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(backgroundImage.getImage(), 0, 0, null);

                g.drawImage(playerIcon.getImage(), playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, null);

                for (Enemy enemy : enemies) {
                    g.drawImage(enemyIcon.getImage(), enemy.getX(), enemy.getY(), ENEMY_SIZE, ENEMY_SIZE, null);
                }

                for (Bullet bullet : playerBullets) {
                    g.drawImage(bulletIcon.getImage(), bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE, null);
                }

                for (Bullet bullet : enemyBullets) {
                    g.drawImage(bulletIcon.getImage(), bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE, null);
                }
            }
        };

        Timer timer = new Timer(16, this);
        timer.start();

        Timer enemyTimer = new Timer(ENEMY_FIRE_RATE, e -> generateEnemies());
        enemyTimer.start();

        Timer enemyBulletTimer = new Timer(16, e -> moveEnemyBullets());
        enemyBulletTimer.start();

        setContentPane(gamePanel);
        setVisible(true);
    }

    private void generateEnemies() {
        Random rand = new Random();
        int enemyX = rand.nextInt(WIDTH - ENEMY_SIZE);
        int enemyY = 0;
        enemies.add(new Enemy(enemyX, enemyY));
        fireEnemyBullet(enemyX + ENEMY_SIZE / 2, enemyY + ENEMY_SIZE);
    }

    private void movePlayer() {
        playerX += playerSpeedX;

        if (playerX < 0) {
            playerX = 0;
        } else if (playerX > WIDTH - PLAYER_SIZE) {
            playerX = WIDTH - PLAYER_SIZE;
        }
    }

    private void moveEnemies() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.setY(enemy.getY() + ENEMY_SPEED);

            if (enemy.getY() > HEIGHT) {
                enemyIterator.remove();
            }

            if (isCollision(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE,
                    enemy.getX(), enemy.getY(), ENEMY_SIZE, ENEMY_SIZE)) {
                gameOver();
            }
        }
    }

    private void movePlayerBullets() {
        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.setY(bullet.getY() - BULLET_SPEED);

            if (bullet.getY() < 0) {
                bulletIterator.remove();
            }
        }
    }

    private void moveEnemyBullets() {
        Iterator<Bullet> bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.setY(bullet.getY() + BULLET_SPEED);

            if (bullet.getY() > HEIGHT) {
                bulletIterator.remove();
            }

            if (isCollision(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE,
                    bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE)) {
                gameOver();
            }
        }
    }

    private void fire() {
        playerBullets.add(new Bullet(playerX + PLAYER_SIZE / 2 - BULLET_SIZE / 2, playerY - BULLET_SIZE, BULLET_SIZE, BULLET_SIZE, 0, -BULLET_SPEED));
    }

    private void fireEnemyBullet(int x, int y) {
        enemyBullets.add(new Bullet(x - BULLET_SIZE / 2, y, BULLET_SIZE, BULLET_SIZE, 0, BULLET_SPEED));
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(this, "Game Over!");
        System.exit(0);
    }

    private boolean isCollision(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2) {
        return x1 < x2 + width2 &&
                x1 + width1 > x2 &&
                y1 < y2 + height2 &&
                y1 + height1 > y2;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        moveEnemies();
        movePlayerBullets();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            fire();
           } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            playerSpeedX = -PLAYER_SPEED;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerSpeedX = PLAYER_SPEED;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerSpeedX = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        new JetFighterGame();
    }

    private class Enemy {
        private int x;
        private int y;

        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private class Bullet {
        private int x;
        private int y;
        private int width;
        private int height;
        private int speedX;
        private int speedY;

        public Bullet(int x, int y, int width, int height, int speedX, int speedY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speedX = speedX;
            this.speedY = speedY;
        }

        public void update(Point playerPosition) {
            this.x += speedX;
            this.y += speedY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
