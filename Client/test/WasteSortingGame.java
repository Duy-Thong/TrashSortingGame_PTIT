import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WasteSortingGame extends JFrame {
    private JLabel timerLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private Timer gameTimer;
    private Timer trashTimer;
    private int secondsLeft = 60;
    private int player1Score = 0;
    private int player2Score = 0;
    private ArrayList<TrashItem> trashItems = new ArrayList<>();
    private Random random = new Random();
    private JPanel gamePanel;
    private static HashMap<String, Image> trashImages = new HashMap<>();

    public WasteSortingGame() {
        setTitle("Waste Sorting Game");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadTrashImages();
        setupTopPanel();
        setupGameArea();
        setupBinPanel();
        setupTimers();
    }

    private void loadTrashImages() {
        String[] types = {"Paper", "Plastic", "Metal"};
        for (String type : types) {
            ImageIcon icon = new ImageIcon("path/to/your/" + type.toLowerCase() + "_trash.png");
            trashImages.put(type, icon.getImage());
        }
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        player1ScoreLabel = new JLabel("Player1: 0 points");
        timerLabel = new JLabel("Time: 60", SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Player2: 0 points", SwingConstants.RIGHT);
        topPanel.add(player1ScoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(player2ScoreLabel);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupGameArea() {
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (TrashItem item : trashItems) {
                    item.draw(g);
                }
            }
        };
        gamePanel.setBackground(new Color(200, 230, 255)); // Light blue background
        add(gamePanel, BorderLayout.CENTER);
    }

    private void setupBinPanel() {
        JPanel binPanel = new JPanel(new GridLayout(1, 3));
        String[] binColors = {"Red", "Yellow", "Gray"};
        for (String color : binColors) {
            JLabel binLabel = createBinLabel(color);
            binPanel.add(binLabel);
        }
        add(binPanel, BorderLayout.SOUTH);
    }

    private JLabel createBinLabel(String color) {
        JLabel label = new JLabel(color + " Bin");
        label.setOpaque(true);
        label.setBackground(Color.getColor(color.toLowerCase()));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkTrashCollection(color);
            }
        });
        return label;
    }

    private void setupTimers() {
        gameTimer = new Timer(1000, e -> {
            secondsLeft--;
            timerLabel.setText("Time: " + secondsLeft);
            if (secondsLeft <= 0) {
                gameTimer.stop();
                trashTimer.stop();
                showEndGame();
            }
        });
        gameTimer.start();

        trashTimer = new Timer(2000, e -> {
            addNewTrashItem();
            moveTrashItems();
            gamePanel.repaint();
        });
        trashTimer.start();
    }

    private void addNewTrashItem() {
        int x = random.nextInt(getWidth() - TrashItem.SIZE);
        String[] types = {"Paper", "Plastic", "Metal"};
        String type = types[random.nextInt(types.length)];
        trashItems.add(new TrashItem(x, 0, type));
    }

    private void moveTrashItems() {
        for (int i = trashItems.size() - 1; i >= 0; i--) {
            TrashItem item = trashItems.get(i);
            item.move();
            if (item.getY() > getHeight() - 100) {
                trashItems.remove(i);
            }
        }
    }

    private void checkTrashCollection(String binColor) {
        for (int i = trashItems.size() - 1; i >= 0; i--) {
            TrashItem item = trashItems.get(i);
            if (item.getY() > getHeight() - 150 && isCorrectBin(item.getType(), binColor)) {
                trashItems.remove(i);
                updateScore();
            }
        }
    }

    private boolean isCorrectBin(String trashType, String binColor) {
        return (trashType.equals("Paper") && binColor.equals("Yellow")) ||
               (trashType.equals("Plastic") && binColor.equals("Gray")) ||
               (trashType.equals("Metal") && binColor.equals("Red"));
    }

    private void updateScore() {
        if (Math.random() < 0.5) {
            player1Score += 10;
            player1ScoreLabel.setText("Player1: " + player1Score + " points");
        } else {
            player2Score += 10;
            player2ScoreLabel.setText("Player2: " + player2Score + " points");
        }
    }

    private void showEndGame() {
        EndGame endGame = new EndGame(this, determineWinner());
        endGame.setVisible(true);
        this.setVisible(false);
    }

    private String determineWinner() {
        if (player1Score > player2Score) {
            return "Player 1";
        } else if (player2Score > player1Score) {
            return "Player 2";
        } else {
            return "It's a tie!";
        }
    }

    public void restartGame() {
        secondsLeft = 60;
        player1Score = 0;
        player2Score = 0;
        trashItems.clear();
        player1ScoreLabel.setText("Player1: 0 points");
        player2ScoreLabel.setText("Player2: 0 points");
        timerLabel.setText("Time: 60");
        gameTimer.restart();
        trashTimer.restart();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WasteSortingGame game = new WasteSortingGame();
            game.setVisible(true);
        });
    }
}

class TrashItem {
    private int x, y;
    private String type;
    public static final int SIZE = 40;

    public TrashItem(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void move() {
        y += 5;
    }

    public void draw(Graphics g) {
        Image img = WasteSortingGame.trashImages.get(type);
        if (img != null) {
            g.drawImage(img, x, y, SIZE, SIZE, null);
        }
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }
}