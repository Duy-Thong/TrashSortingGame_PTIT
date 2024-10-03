import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WasteSortingGame extends JFrame {
    private JLabel timerLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private Timer gameTimer;
    private int secondsLeft = 30;
    private int player1Score = 0;
    private int player2Score = 0;

    public WasteSortingGame() {
        setTitle("Waste Sorting Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupTopPanel();
        setupGameArea();
        setupBinPanel();
        setupTimer();
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        player1ScoreLabel = new JLabel("Player1: 0 points");
        timerLabel = new JLabel("Time: 30", SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Player2: 0 points", SwingConstants.RIGHT);
        topPanel.add(player1ScoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(player2ScoreLabel);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupGameArea() {
        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(new Color(100, 200, 150)); // Teal-ish background
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
        // Replace "path/to/your/image.png" with the actual path to your bin images
        ImageIcon icon = new ImageIcon("database/images/bins/container_bin_grey.png");
        JLabel label = new JLabel(icon);
        label.setToolTipText(color + " Bin");
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Simulating scoring (replace with actual game logic)
                if (Math.random() < 0.5) {
                    player1Score += 10;
                    player1ScoreLabel.setText("Player1: " + player1Score + " points");
                } else {
                    player2Score += 10;
                    player2ScoreLabel.setText("Player2: " + player2Score + " points");
                }
            }
        });
        return label;
    }

    private void setupTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                timerLabel.setText("Time: " + secondsLeft);
                if (secondsLeft <= 0) {
                    gameTimer.stop();
                    showEndGame();
                }
            }
        });
        gameTimer.start();
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
        secondsLeft = 30;
        player1Score = 0;
        player2Score = 0;
        player1ScoreLabel.setText("Player1: 0 points");
        player2ScoreLabel.setText("Player2: 0 points");
        timerLabel.setText("Time: 30");
        gameTimer.restart();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WasteSortingGame game = new WasteSortingGame();
            game.setVisible(true);
        });
    }
}