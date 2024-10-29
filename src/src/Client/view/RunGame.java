package Client.view;

import Client.Constants;
import Client.model.Bin;
import Client.model.TrashItem;
import Client.controller.DataController;
import Client.model.UDPClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
/**
 *
 * @author vutuyen
 */

public class RunGame extends JFrame implements UDPClient.updateUI{
    private JLabel timerLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private Timer gameTimer, trashTimer;
    private JPanel gamePanel;
    private int TIMER = 50000, TIMEPLAY = 500;
    private int secondsLeft = TIMEPLAY, frametime = TIMER;
    private int player1Score = 0;
    private int player2Score = 0;
    private String playerId;
    private String roomId;
    private int width = 810, height = 540;
    private Random random = new Random();
    private ArrayList<TrashItem> trashItems = new ArrayList<>();
    private ArrayList<TrashItem> trashItemsDefaul = new ArrayList<>();
    private ArrayList<Integer> listIndex = new ArrayList<>();
    private ArrayList<Bin> binItemsDefaul = new ArrayList<>();
    private List<String> listTypes = new ArrayList<>();
    private BufferedImage backgroundImage;
    UDPClient  udpClient;

    // Loop game
    public RunGame(String roomId, String playerId) {
        this.roomId = roomId;
        this.playerId = playerId;
        try {
            udpClient = new UDPClient(Constants.IP_SERVER,Constants.PORT);
            udpClient.setmUpdateUI(this);
            udpClient.listenForResponses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            backgroundImage = ImageIO.read(getClass().getResource("../assets/play_game3.jpg"));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        setTitle("Waste Sorting Game");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        addKeyListener(new KeyHandler());
        loadIndexs();
        loadTrashs();
        loadBins();
        setupGameArea();
        setupTopPanel();
        setupTimer();
    }

    // Load trashs, bins, indexs
    private void loadIndexs(){
        listTypes = DataController.getListTypesBin();
        int nBin = listTypes.size();
        int frameBin = width / nBin;
        int center = frameBin / 2;
        for(int i = 0;i < nBin; i++){
            listIndex.add(frameBin * i + center);
            System.out.println("x: " + (frameBin * i + center));
        }
    }

    private void loadTrashs() {
        List<String> ls = DataController.getListTrash();
        List<String> types = DataController.getListTypesTrash();
        List<String> names = DataController.getListNameTrash();
        for (int i = 0; i < ls.size(); i++) {
            trashItemsDefaul.add(new TrashItem(names.get(i), types.get(i), ls.get(i)));
        }
    }

    private void loadBins() {
        List<String> ls = DataController.getListBin();
        for (int i = 0; i < listTypes.size(); i++) {
            binItemsDefaul.add(new Bin(listIndex.get(i), getHeight(), listTypes.get(i), ls.get(i)));
            Bin tmp = binItemsDefaul.getLast();
            tmp.setY(getHeight() - 110);
        }
    }

    // set main game
    private void setupGameArea() {
        gamePanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, null);
                if(!trashItems.isEmpty()) trashItems.getFirst().draw(g);
                for(int i = 0;i  < binItemsDefaul.size(); i++) {
                    binItemsDefaul.get(i).draw(g);
                }
            }
        };
        add(gamePanel, BorderLayout.CENTER);
    }

    // set up topbar game
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        Font customFont = loadCustomFont("../assets/FVF.ttf");
        topPanel.setOpaque(false); // Đặt panel trong suốt
        player1ScoreLabel = new JLabel("Player1: 0 points");
        player1ScoreLabel.setFont(customFont.deriveFont(Font.BOLD, 13));
        timerLabel = new JLabel("Time:" + secondsLeft, SwingConstants.CENTER);
        timerLabel.setFont(customFont.deriveFont(Font.BOLD, 13));
        player2ScoreLabel = new JLabel("Player2: 0 points", SwingConstants.RIGHT);
        player2ScoreLabel.setFont(customFont.deriveFont(Font.BOLD, 13));

        topPanel.add(player1ScoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(player2ScoreLabel);
        add(topPanel, BorderLayout.NORTH);
    }

    @Override
    public void updateScorePlayer() {
        updateScorePlayer2();
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
        gameTimer = new Timer(10, (ActionEvent e) -> {
            frametime --;
            if (frametime % 100 == 0)
                secondsLeft--;
            timerLabel.setText("Time: " + secondsLeft);
            if (frametime <= 0) {
                gameTimer.stop();
                trashTimer.stop();
                showEndGame();
            }
        });
        gameTimer.start();

        trashTimer = new Timer(10, (ActionEvent e) -> {
            addNewTrashItem();
            moveTrashItems();
            gamePanel.repaint();
        });

        trashTimer.start();
    }
    public void loadItemTrash(){

    }
    // add a new item
    private void addNewTrashItem() {
        if(trashItems.isEmpty()) {
            TrashItem item = trashItemsDefaul.get(random.nextInt(0, trashItemsDefaul.size()));
            int index = random.nextInt(0, listIndex.size());
            item.setX(listIndex.get(index));
            item.setIndex(index);
            trashItems.add(item.copy());
        }

    }


    // Trash auto move down
    private void moveTrashItems() {
        TrashItem item = trashItems.getFirst();
        item.move();
        if (item.getY() > getHeight() - 110) {
            trashItems.removeFirst();
            if( isCorrectBin(item))
                updateScorePlayer1();
        }
    }

    // check answer
    private boolean isCorrectBin(TrashItem trash) {
        for(int i = 0;i < binItemsDefaul.size(); i++)
        {
            Bin bin = binItemsDefaul.get(i);
            if (trash.getType().equals(bin.getType()) && trash.getX() == bin.getX()) return true;
        }
        return false;
    }

    // update score
    private void updateScorePlayer1() {
        player1Score += 10;
        player1ScoreLabel.setText("Player1: " + player1Score + " points");
        udpClient.sendScoreUpdate(playerId, player1Score, roomId);
    }

    private void updateScorePlayer2() {
        player2Score += 10;
        player2ScoreLabel.setText("Player2: " + player2Score + " points");
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
            TrashItem x = trashItems.get(0);
            int index = x.getIndex();
            if(index == 0) {
                x.setX(listIndex.getLast());
                x.setIndex(listIndex.size() - 1);
            }
            else {
                x.setX(listIndex.get(index - 1));
                x.setIndex(index - 1);
            }
            System.out.println("left");
        } else if (keyCode == 227 || keyCode == 39){
            TrashItem x = trashItems.get(0);
            int index = x.getIndex();
            if(index == listIndex.size() - 1) {
                x.setX(listIndex.getFirst());
                x.setIndex(0);
            }
            else {
                x.setX(listIndex.get(index + 1));
                x.setIndex(index + 1);
            }
            System.out.println("Right");
        } else if (keyCode == 32 || keyCode == 225 || keyCode == 40){
            TrashItem y = trashItems.get(0);
            y.setY(getHeight() - 75);
            System.out.println("Down");
        }
    }

    // Play again
    public void restartGame() {
        secondsLeft = TIMEPLAY;
        frametime = TIMER;
        player1Score = 0;
        player2Score = 0;
        player1ScoreLabel.setText("Player1: 0 points");
        player2ScoreLabel.setText("Player2: 0 points");
        timerLabel.setText("Time: " + secondsLeft);
        trashItems.clear();
        gameTimer.restart();
        trashTimer.restart();
        this.setVisible(true);
    }
    // Load custom font method
    private Font loadCustomFont(String fontPath) {
        try (InputStream is = getClass().getResourceAsStream(fontPath)) {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, 18); // Fallback to default font if loading fails
        }
    }
}
