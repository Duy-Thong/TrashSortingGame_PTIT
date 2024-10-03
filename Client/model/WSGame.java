package model;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import model.bins.Bin;
import model.bins.FoodScrapBin;
import model.bins.GarbageBin;
import model.bins.PaperBin;
import model.bins.RecyclableContainersBin;
import model.wasteItems.BananaPeel;
import model.wasteItems.GlassBottle;
import model.wasteItems.Letter;
import model.wasteItems.Magazine;
import model.wasteItems.MilkBox;
import model.wasteItems.Newspaper;
import model.wasteItems.PlasticBag;
import model.wasteItems.WasteItem;
import ui.WasteSortingGame;
import ui.GameOverFrame;

public class WSGame extends Observable {
  public static final int WIDTH = 800;
  
  public static final int HEIGHT = 600;
  
  public static final int firstX = 100;
  
  public static final int secondX = 300;
  
  public static final int thirdX = 500;
  
  public static final int fourthX = 700;
  
  public static final String CORRECTLY_SORTED = "CORRECTLY SORTED";
  
  public static final String MISPLACED = "WASTE MISPLACED";
  
  private List<Bin> bins;
  
  private FoodScrapBin foodScrapBin;
  
  private GarbageBin garbageBin;
  
  private PaperBin paperBin;
  
  private RecyclableContainersBin recyclableBin;
  
  private int count;
  
  public static final Random RND = new Random();
  
  public static final int smallSize = 10;
  
  private boolean isGameOver;
  
  private int correctlySorted;
  
  private int incorrectlySorted;
  
  public static final String GameStarts = "GAME STARTS NOW!";
  
  private WasteItem itemOnScreen;
  
  public WSGame() {
    initializeItems();
    reset();
  }
  
  public static void setDifficultyLevel(ActionEvent e) {
    String str = e.getActionCommand();
    switch (str) {
      case "Medium":
        WasteItem.setMedSpeed();
        break;
      case "Difficult":
        WasteItem.setHighSpeed();
        break;
      case "Easy":
        WasteItem.setLowSpeed();
        break;
      default:
        throw new Error("wrong difficulty level");
    } 
  }
  
  public int getCorrectItems() {
    return this.correctlySorted;
  }
  
  public int getIncorrectItems() {
    return this.incorrectlySorted;
  }
  
  private void initializeItems() {
    this.count = 0;
    this.itemOnScreen = getNext();
    this.foodScrapBin = new FoodScrapBin();
    this.garbageBin = new GarbageBin();
    this.paperBin = new PaperBin();
    this.recyclableBin = new RecyclableContainersBin();
    this.bins = new ArrayList<>();
    this.bins.add(this.foodScrapBin);
    this.bins.add(this.garbageBin);
    this.bins.add(this.paperBin);
    this.bins.add(this.recyclableBin);
  }
  
  public WasteItem generateItem(int i) {
    Letter letter;
    BananaPeel bananaPeel;
    GlassBottle glassBottle;
    PlasticBag plasticBag;
    MilkBox milkBox;
    Magazine magazine;
    int xCoordinate, rnd = (new Random()).nextInt(4) + 1;
    switch (rnd) {
      case 1:
        xCoordinate = 100;
        break;
      case 2:
        xCoordinate = 300;
        break;
      case 3:
        xCoordinate = 500;
        break;
      case 4:
        xCoordinate = 700;
        break;
      default:
        xCoordinate = 100;
        break;
    } 
    switch (i) {
      case 1:
        return (WasteItem)new Letter(xCoordinate);
      case 2:
        return (WasteItem)new BananaPeel(xCoordinate);
      case 3:
        return (WasteItem)new GlassBottle(xCoordinate);
      case 4:
        return (WasteItem)new PlasticBag(xCoordinate);
      case 5:
        return (WasteItem)new MilkBox(xCoordinate);
      case 6:
        return (WasteItem)new Magazine(xCoordinate);
      case 7:
        return (WasteItem)new Newspaper(xCoordinate);
    } 
    throw new IllegalArgumentException("invalid item");
  }
  
  private void reset() {
    this.isGameOver = false;
    this.count = 0;
    this.correctlySorted = 0;
    this.incorrectlySorted = 0;
    setChanged();
    notifyObservers("GAME STARTS NOW!");
  }
  
  public void update(WasteSortingGame wsFrame) {
    moveItem();
    checkGameOver(wsFrame);
  }
  
  public void keyPressed(int keyCode) {
    if (keyCode == 226 || keyCode == 37) {
      this.itemOnScreen.moveLeft();
    } else if (keyCode == 227 || keyCode == 39) {
      this.itemOnScreen.moveRight();
    } else if (keyCode == 32 || keyCode == 225 || keyCode == 40) {
      speedUpDy();
    } else if (keyCode == 82 && this.isGameOver) {
      reset();
    } else if (keyCode == 88) {
      System.exit(0);
    } 
  }
  
  private void speedUpDy() {
    this.itemOnScreen.speedUp();
  }
  
  private void moveItem() {
    if (!this.isGameOver) {
      this.itemOnScreen.move();
      if (checkFalls()) {
        setChanged();
        this.count++;
        if (correctlySorted()) {
          this.correctlySorted++;
          notifyObservers("CORRECTLY SORTED");
        } else {
          this.incorrectlySorted++;
          notifyObservers("WASTE MISPLACED");
        } 
        newItemFalls();
      } 
    } 
  }
  
  public boolean checkFalls() {
    return (this.itemOnScreen.getY() >= 600 - Bin.getSizeY());
  }
  
  public boolean correctlySorted() {
    String bin = this.itemOnScreen.getBelongedBin();
    String str1 = bin;
    switch (bin) {
      case "Paper Bin":
        // if (!str1.equals("Paper Bin"))
        //   break; 
        if (this.itemOnScreen.getX() == 500)
          return true; 
        break;
      case "Food Scrap Bin":
        // if (!str1.equals("Food Scrap Bin"))
        //   break; 
        if (this.itemOnScreen.getX() == 100)
          return true; 
        break;
      case "Recyclable Containers Bin":
        // if (!str1.equals("Recyclable Containers Bin"))
        //   break; 
        if (this.itemOnScreen.getX() == 300)
          return true; 
        break;
      case "Garbage Bin":
        // if (!str1.equals("Garbage Bin"))
        //   break; 
        if (this.itemOnScreen.getX() == 700)
          return true; 
        break;
    } 
    return false;
  }
  
  public void newItemFalls() {
    if (this.count < 10) {
      itemFalls(getNext());
    } else {
      this.isGameOver = true;
    } 
  }
  
  private void itemFalls(WasteItem item) {
    this.itemOnScreen = item;
  }
  
  private WasteItem getNext() {
    return generateItem(RND.nextInt(7) + 1);
  }
  
  public void checkGameOver(WasteSortingGame wsFrame) {
    if (isOver()) {
      wsFrame.stopTimer();
      wsFrame.dispose();
      new GameOverFrame(this);
    } 
  }
  
  public boolean isOver() {
    return this.isGameOver;
  }
  
  public void render(Graphics g) {
    if (!this.isGameOver)
      this.itemOnScreen.render(g); 
    for (Bin bin : this.bins)
      bin.render(g); 
  }
}
