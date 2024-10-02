import java.io.*;
class Replay {
  private byte[][] moves = new byte[2][742];
  private int numMoves;
  private String file;
  public Replay(byte[][] moves, int numMoves, String file) {
    this.moves = moves;
    this.numMoves = numMoves;
    this.file = file;
  }
  public Replay(String replayPath) throws IOException {
    readReplay(replayPath);
  }
  public byte[][] getMoves() {
    return moves;
  }
  public int getNumMoves() {
    return numMoves;
  }
  public String getPath() {
    return file;
  }
  public void readReplay(String replayPath) throws IOException {
    String mapPath = "Generated Maps/"+replayPath.substring(replayPath.lastIndexOf("/")+1, replayPath.lastIndexOf("Replay")-1)+".txt";
    file = mapPath;
    char[] chars = new char[100000];
    FileReader fr = new FileReader(replayPath); 
    fr.read(chars);
    String temp = "";
    numMoves = 0;
    int startIndex = 0;
    for(int i=0; i<chars.length; i++) {
      if(chars[i] == ';') {
        startIndex = i+1;
        break;
      }
    }
    for(int i=startIndex; i<chars.length; i++) {
      if(isNumber(chars[i])) {
        temp += chars[i];
      } else if(chars[i] == ',') {
        moves[0][numMoves] = (byte)(int)Integer.valueOf(temp);
        temp = "";
      } else if(chars[i] == '}') {
        moves[1][numMoves] = (byte)(int)Integer.valueOf(temp);
        temp = "";
      } else if(chars[i] == ';') {
        numMoves++;
      }
    }
    numMoves--;
    fr.close();
  }
  public String saveReplay() throws IOException {
    String fileName = file.substring(file.lastIndexOf("/")+1, file.lastIndexOf("."));
    File folder = new File("Replays");
    int numReplays = 1;
    File[] replays = folder.listFiles();
    for(int i=0; i<replays.length; i++) {
      if(replays[i].getName().contains(fileName+" ")) {
        numReplays++;
      }
    }
    FileWriter fw = new FileWriter("Replays/"+fileName+" Replay "+numReplays+".txt");
    fw.write("(Saved Replay):\nMap: "+file+";");
    for(int i=0; i<numMoves+1; i++) {
      fw.write("\n{"+moves[0][i]+", "+moves[1][i]+"};");
    }
    fw.close();
    return fileName+" Replay "+numReplays+".txt";
  }
  public static boolean isNumber(char letter) {
    if((int)letter>47 && (int)letter<58){
      return true;
    }
    return false;
  }
}