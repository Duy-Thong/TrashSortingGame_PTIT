package model.wasteItems;

public class BananaPeel extends WasteItem {
  public static final String belongsTo = "Food Scrap Bin";
  
  private static final int code = 2;
  
  private static final String IMAGE_PATH = "src/data/images/waste_items/banana_peel.png";
  
  public BananaPeel(int x) {
    super(x);
    this.belongedBin = "Food Scrap Bin";
    this.codeNum = 2;
    this.name = "Banana peel";
    this.imagePath = "src/data/images/waste_items/banana_peel.png";
  }
  
  protected String getPath() {
    return "src/data/images/waste_items/banana_peel.png";
  }
}
