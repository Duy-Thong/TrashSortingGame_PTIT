package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class RunGame extends JFrame {
    private JLabel timerLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private Timer gameTimer, trashTimer;
    private JPanel gamePanel;
    private int secondsLeft = 120;
    private int player1Score = 0;
    private int player2Score = 0;
    private int player = 1;
    private Random random = new Random();
    private ArrayList<TrashItem> trashItems = new ArrayList<>();
    public static HashMap<String, Image> trashImages = new HashMap<>();
    

    // Loop game
    public RunGame() {             
        setTitle("Waste Sorting Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addKeyListener(new KeyHandler());
        loadTrashImages();
        setupGameArea();
        setupTopPanel();
        setupBinPanel();
        setupTimer();     
    }
    
    // Load image trash
    private void loadTrashImages() {
        String[] types = {"Paper", "Plastic", "Metal"};
        for (String type : types) {
            ImageIcon icon = new ImageIcon("/home/vutuyen/NetBeansProjects/JavaApplication8/src/images/trash2.png");
            trashImages.put(type, icon.getImage());
        }
    }
    
    // set main game 
    private void setupGameArea() {
        gamePanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(trashItems.size() >= 1){
                    trashItems.get(0).draw(g, trashImages);
                }
            }
        };       
        add(gamePanel, BorderLayout.CENTER);
    }
    
    // set up topbar game
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        player1ScoreLabel = new JLabel("Player1: 0 points");
        timerLabel = new JLabel("Time:" + secondsLeft, SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Player2: 0 points", SwingConstants.RIGHT);
        topPanel.add(player1ScoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(player2ScoreLabel);
        add(topPanel, BorderLayout.NORTH);
    }
    
    
    
    // Set up location Bin
    private void setupBinPanel() {
        JPanel binPanel = new JPanel(new GridLayout(1, 3));
        String[] binColors = {"Red", "Yellow", "Gray"};
        for (String color : binColors) {
            JLabel binLabel = Bin.createBinLabel(color);
            binPanel.add(binLabel);
        }
        add(binPanel, BorderLayout.SOUTH);
    }
    
    // Handler KeyBoards
     private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            userKeyPressed(e.getKeyCode());
        }
    }
    
     // set Timer game and Trash
    private void setupTimer() {
        gameTimer = new Timer(100, (ActionEvent e) -> {
            secondsLeft--;
            timerLabel.setText("Time: " + secondsLeft);
            if (secondsLeft <= 0) {
                gameTimer.stop();
                trashTimer.stop();
                showEndGame();
            }
        });
        gameTimer.start();
        
        trashTimer = new Timer(100, (ActionEvent e) -> {
            if(trashItems.isEmpty())
                addNewTrashItem();
            moveTrashItems();
            gamePanel.repaint();
        });
        
        trashTimer.start();
    }
    
    // add new a item
    private void addNewTrashItem() {
        ArrayList<Integer> locx =new ArrayList<>(Arrays.asList(80, 280, 480));
        int x = locx.get(random.nextInt(0, locx.size()));
        String[] types = {"Paper", "Plastic", "Metal"};
        String type = types[random.nextInt(types.length)];
        trashItems.add(new TrashItem(x, 0, type));
    }
    
    
    // Trash auto move down
    private void moveTrashItems() {
        TrashItem item = trashItems.get(0);
        item.move();
        if (item.getY() > getHeight() - 10) {
            trashItems.remove(0);
            HashMap<Integer, String> x_bin = new HashMap<Integer, String>();
            x_bin.put(80, "Red");
            x_bin.put(280, "Yellow");
            x_bin.put(480, "Gray");
            if( isCorrectBin(item.getType(), x_bin.get(item.getX())))
                updateScore();
        }
    }
    
    // check answer
    private boolean isCorrectBin(String trashType, String binColor) {
        return (trashType.equals("Paper") && binColor.equals("Yellow")) ||
               (trashType.equals("Plastic") && binColor.equals("Gray")) ||
               (trashType.equals("Metal") && binColor.equals("Red"));
    }
    
    // update score
    private void updateScore() {
        if (player == 1) {
            player1Score += 10;
            player1ScoreLabel.setText("Player1: " + player1Score + " points");
        } else {
            player2Score += 10;
            player2ScoreLabel.setText("Player2: " + player2Score + " points");
        }
    }
    
    // show EndGame
    private void showEndGame() {
        EndGame endGame = new EndGame(this, determineWinner());
        endGame.setVisible(true);
        this.setVisible(false);
    }
    

    // Who win?
    private String determineWinner() {
        if (player1Score > player2Score) {
            return "Player 1";
        } else if (player2Score > player1Score) {
            return "Player 2";
        } else {
            return "It's a tie!";
        }
    }
    
    
    // Get event game
     public void userKeyPressed(int keyCode) {
            if (keyCode == 226 || keyCode == 37){
//              this.itemOnScreen.moveLeft();
                System.out.println("left");
            } else if (keyCode == 227 || keyCode == 39){
//              this.itemOnScreen.moveRight();
                System.out.println("Right");
            } else if (keyCode == 32 || keyCode == 225 || keyCode == 40){
//              speedUpDy();
                System.out.println("Down");
//            } else if (keyCode == 82 && this.isGameOver) {
////              reset();
//                System.out.println("Over");
            } else if (keyCode == 88) {
              System.exit(0);
            } 
    }
    
     // Play again
    public void restartGame() {
        secondsLeft = 120;
        player1Score = 0;        
        player2Score = 0;
        player = 1;
        player1ScoreLabel.setText("Player1: 0 points");
        player2ScoreLabel.setText("Player2: 0 points");
        timerLabel.setText("Time: 120");
        trashItems.clear();
        gameTimer.restart();
        trashTimer.restart();
        this.setVisible(true);
    }
    
}