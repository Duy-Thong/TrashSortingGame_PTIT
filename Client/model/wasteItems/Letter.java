package model.wasteItems;

public class Letter extends WasteItem {
  public static final String belongsTo = "Paper Bin";
  
  private static final int code = 1;
  
  private static final String IMAGE_PATH = "src/data/images/waste_items/letter.png";
  
  public Letter(int x) {
    super(x);
    this.belongedBin = "Paper Bin";
    this.codeNum = 1;
    this.name = "Letter";
    this.imagePath = "src/data/images/waste_items/letter.png";
  }
  
  protected String getPath() {
    return "src/data/images/waste_items/letter.png";
  }
}
