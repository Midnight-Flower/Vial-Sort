import java.util.*;
import java.io.*;
class Map {
  private static Scanner sc = new Scanner(System.in);
  private static String[] colors = {
    "\u001b[0m", //Clear
    "\u001B[38;5;57m", //Purple
    "\u001B[38;5;49m", //Green
    "\u001B[38;5;39m", //Blue
    "\u001B[38;5;196m" //Red
  };
  private GameTracker gt = new GameTracker();
  private String path = "";
  private File file;
  private Vial[] baseState = new Vial[1000];
  private Vial[] currentState = new Vial[1000];
  private boolean isHiddenModeOn;
  private boolean isPossible = false;
  private String vBottom;
  private int numFullVials = 0;
  private int numEmptyVials = 0;
  private int totalVials = 0;
  private int depth;
  public void setPath(String newPath) {
    path = newPath;
    file = new File(newPath);
  }
  public String getPath() {
    return path;
  }
  public GameTracker getTracker() {
    return gt;
  }
  public Vial[] getCurrentVials() {
    return currentState;
  }
  public int getDepth() {
    return depth;
  }
  public int getFullVials() {
    return numFullVials;
  }
  public int getEmptyVials() {
    return numEmptyVials;
  }
  public int getTotalVials() {
    return totalVials;
  }
  public void setSettings(boolean hiddenMode, String letter) {
    isHiddenModeOn = hiddenMode;
    vBottom = letter;
  }
  public void setPossible() {
    isPossible = true;
  }
  public boolean getPossible() {
    return isPossible;
  }
  public boolean getHiddenMode() {
    return isHiddenModeOn;
  }
  public void reset() throws IOException {
    gt.reset(path);
    readMap();
    for(int i=0; i<totalVials; i++) {
      //currentState[i] = baseState[i];
    }
    if(isHiddenModeOn) {
      for(int i=0; i<numFullVials; i++) {
        currentState[i].resetVisibility();
      }
    }
  }
  public boolean pour(int v1, int v2) {
    if(v1 == 0 || v2 == 0) return false;
    if(currentState[v1-1].addTop(currentState[v2-1])) {
      gt.move(v1, v2);
      return true;
    }
    return false;
  }
  public void readMap() throws IOException {
    isPossible = false;
    numFullVials = 0;
    numEmptyVials = 0;
    totalVials = 0;
    depth = 0;
    char[] chars = new char[100000];
    FileReader fr = new FileReader(file); 
    fr.read(chars);
    int[] vialVals = new int[1000];
    String temp = "";
    for(int i=0; i<chars.length; i++) {
      if(isNumber(chars[i])) {
        temp += chars[i];
      } else if(chars[i] == ';') {
        currentState[totalVials] = new Vial(vialVals, depth);
        baseState[totalVials] = new Vial(vialVals, depth);
        if(vialVals[0] == 0) numEmptyVials++;
        else numFullVials++;
        totalVials++;
      } else if(chars[i] == '{') {
        depth = 0;
      } else if(temp.length() != 0) {
        vialVals[depth] = Integer.valueOf(temp);
        temp = "";
        depth++;
      }
    }
    fr.close();
  }
  public String saveMap() throws IOException {
    File[] maps = new File("Generated Maps").listFiles();
    int mapIndex = 0;
    boolean indexFound = false;
    while(!indexFound) {
      mapIndex++;
      for(int i=0; i<maps.length; i++) {
        if(maps[i].getName().equals("Map "+mapIndex+".txt")) break;
        else if(!(maps[i].getName().equals("Map "+mapIndex)) && i == maps.length-1) {
          indexFound = true;
        }
      }
    }
    FileWriter fw = new FileWriter("Generated Maps/Map "+mapIndex+".txt");
    FileReader fr = new FileReader(path);
    char[] chars = new char[10000];
    fr.read(chars);
    for(int i=0; i<10000; i++) {
      if((int)chars[i] == 0) break;
      fw.write(chars[i]);
    }
    fw.close();
    fr.close();
    return "Map "+mapIndex+".txt";
  }
  public String toString() {
    String ret = "";
    int[] rows = findOptimalRows();
    int greatestRow = 0, smallestRow = 742;
    for(int i=0; i<rows.length; i++) {
      if(rows[i] < smallestRow) smallestRow = rows[i];
      if(rows[i] > greatestRow) greatestRow = rows[i];
    }
    int[][] offsets = new int[rows.length][greatestRow];
    for(int i=0; i<rows.length; i++) {
      if(greatestRow % 2 == 1 && rows[i] % 2 == 0) {
        offsets[i][0] = 1;
        offsets[i][smallestRow/2] = 2;
      } else if(greatestRow % 2 == 0 && rows[i] % 2 == 1) {
        offsets[i][0] = 1;
        offsets[i][greatestRow/2-1] = 1;
        offsets[i][greatestRow/2] = 1;
      }
    }
    int depth = currentState[0].getDepth();
    int vialIndex = 0;
    for(int i=0; i<rows.length; i++) {
      for(int j=0; j<depth+2; j++) {
        if(j == 0) {
          for(int k=0; k<rows[i]; k++) {
            ret += printSpace(offsets[i][k]);
            ret += colors[1]+"\\ / "+colors[0];
          }
        } else if(j == depth) {
          for(int k=0; k<rows[i]; k++) {
            ret += printSpace(offsets[i][k]);
            vialIndex = getVialIndex(rows, i, k);
            ret += colors[1]+" ";
            if(!currentState[vialIndex].isHidden(depth-1)) {
              ret += currentState[vialIndex].getColor(depth-1)+colors[1]+vBottom;
            } else {
              ret += "⍰";
            }
            ret += colors[0]+"  ";
          }
        } else if(j == depth+1) {
          for(int k=0; k<rows[i]; k++) {
            ret += printSpace(offsets[i][k]);
            ret += " "+colors[2]+(getVialIndex(rows, i, k)+1)+colors[0]+" ";
            if(i*(rows[i])+k+1 < 10) {
              ret += " ";
            }
          }
        } else {
          for(int k=0; k<rows[i]; k++) {
            ret += printSpace(offsets[i][k]);
            vialIndex = getVialIndex(rows, i, k);
            ret += colors[1]+"⎹";
            if(!currentState[vialIndex].isHidden(j-1)) {
              ret += currentState[vialIndex].getColor(j-1)+" "+colors[0];
            } else {
              ret += "?";
            }
            ret += colors[1]+"⎸ "+colors[0];
          }
        }
        ret += "\n";
      }
      ret += "\n";
    }
    return ret;
  }
  public int[] findOptimalRows() {
    int lowestDifference = 742;
    int optimal = 1;
    int num1, num2;
    for(int r=1; r<totalVials; r++) {
      num1 = (4*totalVials/r)-1;
      num2 = 2*r*(depth+3)-1;
      if(Math.abs(num1-num2) < lowestDifference) {
        lowestDifference = Math.abs(num1-num2);
        optimal = r;
      }
    }
    int[] rows = new int[optimal];
    if(totalVials/optimal == (double)totalVials/optimal) {
      for(int i=0; i<optimal; i++) {
        rows[i] = totalVials/optimal;
      }
    } else {
      for(int i=0; i<optimal; i++) {
        rows[i] = totalVials/optimal;
      }
      int rem = totalVials%optimal;
      if(rem == 1) rows[optimal-1]++;
      else if(rem == 2) {
        rows[0]++;
        rows[optimal-1]++;
      } else if(rem == 3) {
        if(optimal % 2 == 0) {
          rows[optimal-3]++;
          rows[optimal-2]++;
          rows[optimal-1]++;
        } else if(optimal % 2 == 1) {
          rows[0]++;
          rows[(optimal-1)/2]++;
          rows[optimal]++;
        }
      }
    }
    return rows;
  }
  public boolean checkWin() {
    for(int i=0; i<totalVials; i++) {
      if(currentState[i].isFullSameColor() == false && currentState[i].getTop() != -1) {
        return false;
      }
    }
    return true;
  }
  public boolean checkLoss() {
    for(int i=0; i<totalVials; i++) {
      if(currentState[i].getTop() == -1) {
        return false;
      } else {
        int color = currentState[i].getTop();
        int colorAmount = currentState[i].getTopAmount();
        for(int j=0; j<totalVials; j++) {
          if(currentState[j].getTop() == color && currentState[j].numEmpty() >= colorAmount && j != i) {
            return false;
          }
        }
      }
    }
    return true;
  }
  public String[] getHints() {
    int freeSpace = 0, numHints = 0, rand;
    String[] hints = new String[42];
    boolean[] movable = new boolean[totalVials];
    for(int i=0; i<totalVials; i++) {
      if(currentState[i].getTop() == -1) {
        do {
          rand = (int)(Math.random()*totalVials)+1;
        } while(rand == i);
        hints[numHints] = (colors[2]+"Vial "+colors[3]+rand+colors[2]+" can be moved to Vial "+colors[3]+(i+1)+colors[2]+"."+colors[0]);
        numHints++;
      }
    }
    for(int i=0; i<totalVials; i++) {
      freeSpace = 0;
      for(int j=0; j<totalVials; j++) {
        if(j!=i) {
          if(currentState[i].getTop() == currentState[j].getTop()) {
            freeSpace += currentState[j].numEmpty();
          }
        }
      }
      if(currentState[i].getTopAmount() <= freeSpace) {
        movable[i] = true;
      } else {
        movable[i] = false;
      }
    }
    for(int i=0; i<movable.length; i++) {
      if(movable[i]) {
        for(int j=0; j<totalVials; j++) {
          if(j!=i) {
            if(currentState[i].getTop() == currentState[j].getTop()) {
              hints[numHints] = (colors[2]+"Vial "+colors[3]+(i+1)+colors[2]+" can be moved to Vial "+colors[3]+(j+1)+"."+colors[2]+colors[0]);
              numHints++;
            }
          }
        }
      }
    }
    return hints;
  }
  public String printSpace(int num) {
    String ret = "";
    for(int i=0; i<num; i++) {
      ret += " ";
    }
    return ret;
  }
  public int getVialIndex(int[] array, int i, int k) {
    int ret = 0;
    for(int j=0; j<i; j++) {
      ret += array[j];
    }
    return ret+k;
  }
  public static boolean isNumber(char letter) {
    if((int)letter>47 && (int)letter<58){
      return true;
    }
    return false;
  }
}