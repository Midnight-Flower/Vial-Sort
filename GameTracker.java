class GameTracker {
  private String[] colors = {
    "\u001b[0m", //Clear
    "\u001B[38;5;57m", //Purple
    "\u001B[38;5;49m", //Green
    "\u001B[38;5;39m", //Blue
    "\u001B[38;5;196m" //Red
  };
  private byte[][] moves = new byte[2][742];
  private Replay[] saves = new Replay[742];
  private String fileName;
  private int numReplays = 0;
  private int numRestarts = -1;
  private int numTotalMoves = 0;
  private int numMoves = 0;
  private long totStartTime;
  private long gameStartTime;
  public void move(int v1, int v2) {
    moves[0][numMoves] = (byte)v1;
    moves[1][numMoves] = (byte)v2;
    numTotalMoves++;
    numMoves++;
  }
  public int getNumMoves() {
    return numMoves;
  }
  public long getTimeDif() {
    return System.currentTimeMillis() - gameStartTime;
  }
  public void reset(String fileName) {
    moves = new byte[2][742];
    this.fileName = fileName;
    gameStartTime = System.currentTimeMillis();
    numMoves = 0;
    numRestarts++;
  }
  public void resetSaves() {
    saves = new Replay[742];
    numReplays = 0;
  }
  public void save() {
    saves[numReplays] = new Replay(moves, numMoves, fileName);
    numReplays++;
  }
  public void startTimer() {
    totStartTime = System.currentTimeMillis();
    gameStartTime = System.currentTimeMillis();
  }
  public String getStats(boolean showMoves, boolean showTime) {
    String ret = "";
    if(showMoves) ret += colors[1]+"Move: "+colors[3]+numMoves+colors[1]+" Restarts: "+colors[3]+numRestarts+colors[0]+"\n";
    if(showTime) ret += colors[1]+"Time: "+colors[3]+getTimeString(System.currentTimeMillis()-totStartTime)+"ms"+colors[0]+"\n";
    return ret;
  }
  public Replay[] getSaves() {
    return saves;
  }
  public String toString() {
    String ret = "";
    long totTimeDif = System.currentTimeMillis()-totStartTime;
    long gameTimeDif = System.currentTimeMillis()-gameStartTime;
    String totTimeString = getTimeString(totTimeDif);
    String gameTimeString = getTimeString(gameTimeDif);
    ret += "Total Time: "+totTimeString;
    if(totTimeString.equals(gameTimeString) == false) {
      ret += "\nGame Time: "+gameTimeString;
    }
    ret += "\nRestarts: "+numRestarts;
    if(numTotalMoves != numMoves) {
      ret += "\nTotal Moves: "+numTotalMoves;
    }
    return ret+"\nMoves: "+numMoves;
  }
  private String getTimeString(long timeDif) {
    String ret = "";
    String[] units = {"ms", "s,", "m,", "hrs,"};
    long[] showTime = {0, 0, 0, 0};
    showTime[3] = timeDif/3600000;
    timeDif %= 3600000;
    showTime[2] = timeDif/60000;
    timeDif %= 60000;
    showTime[1] = timeDif/1000;
    timeDif %= 1000;
    showTime[0] = timeDif;
    for(int i=showTime.length-1; i>=0; i--) {
      if(showTime[i] != 0) {
        ret += showTime[i]+units[i]+" ";
      }
    }
    return ret;
  }
}