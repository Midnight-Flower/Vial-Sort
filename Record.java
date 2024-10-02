class Record {
  private String[] colors = {
    "\u001b[0m", //Clear
    "\u001B[38;5;57m", //Purple
    "\u001B[38;5;49m", //Green
    "\u001B[38;5;39m", //Blue
    "\u001B[38;5;196m" //Red
  };
  private String file;
  private int compMoves;
  private int huMoves;
  private long time;
  public Record(String file, int compMoves, int huMoves, long time) {
    this.file = file;
    this.compMoves = compMoves;
    this.huMoves = huMoves;
    this.time = time;
  }
  public String toString() {
    return file+":"+compMoves+":"+huMoves+":"+time+";";
  }
  public String printValues() {
    String ret = "";
    ret += colors[1]+"Map: "+colors[3]+getFileName();
    ret += colors[1]+"\nComputer Record Moves: ";
    if(compMoves != -1) ret += colors[3]+compMoves;
    else ret += colors[3]+"Not Yet Set";
    ret += colors[1]+"\nHuman Record Moves: ";
    if(huMoves != -1) ret += colors[3]+huMoves;
    else ret += colors[3]+"Not Yet Set";
    ret += colors[1]+"\nHuman Record Time: ";
    if(time != -1) ret += colors[3]+getTimeString(time);
    else ret += colors[3]+"Not Yet Set";
    return ret;
  }
  public String getFilePath() {
    return file;
  }
  public String getFileName() {
    if(file.lastIndexOf("/") == -1 || file.lastIndexOf(".") == -1) return "";
    return file.substring(file.lastIndexOf("/")+1, file.lastIndexOf("."));
  }
  public int getCompMoves() {
    return compMoves;
  }
  public int getHuMoves() {
    return huMoves;
  }
  public long getTime() {
    return time;
  }
  public void setFilePath(String file) {
    this.file = file;
  }
  public void setCompMoves(int compMoves) {
    this.compMoves = compMoves;
  }
  public void setHuMoves(int huMoves) {
    this.huMoves = huMoves;
  }
  public void setTime(long time) {
    this.time = time;
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