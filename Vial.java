class Vial {
  //colors based off of https://www.google.com/search?q=water+color+sort&rlz=1CABQYG_enUS971US971&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiph5Si0677AhWPFVkFHS0lA0AQ_AUoAnoECAIQBA&biw=1300&bih=580&dpr=1.05&safe=active&ssui=on#imgrc=zes-NLIQR4QHSM
  //Color codes: https://replit.com/@SamuelEgeli/Java-Color-Codes
  final String[] colorVals = {"\u001b[0m", "\u001B[48;5;196m", "\u001B[48;5;208m", "\u001B[48;5;190m", "\u001B[48;5;40m", "\u001B[48;5;32m", "\u001B[48;5;7m", "\u001B[48;5;1m", "\u001B[48;5;126m", "\u001B[48;5;3m", "\u001B[48;5;4m", "\u001B[48;5;5m", "\u001B[48;5;6m", "\u001B[48;5;8m", "\u001B[48;5;9m", "\u001B[48;5;10m", "\u001B[48;5;11m", "\u001B[48;5;12m", "\u001B[48;5;17m", "\u001B[48;5;22m", "\u001B[48;5;23m", "\u001B[48;5;30m", "\u001B[48;5;34m", "\u001B[48;5;35m", "\u001B[48;5;36m", "\u001B[48;5;47m", "\u001B[48;5;52m", "\u001B[48;5;54m", "\u001B[48;5;57m", "\u001B[48;5;60m", "\u001B[48;5;63m", "\u001B[48;5;64m", "\u001B[48;5;65m", "\u001B[48;5;66m", "\u001B[48;5;84m", "\u001B[48;5;88m", "\u001B[48;5;89m", "\u001B[48;5;93m", "\u001B[48;5;94m", "\u001B[48;5;95m", "\u001B[48;5;98m", "\u001B[48;5;166m", "\u001B[48;5;197m"};
  private boolean isHidden[] = new boolean[50];
  private int[] colors;
  private int depth;
  public Vial(int[] colors) {
    this.colors = colors;
    this.depth = colors.length;
    for(int i=0; i<depth; i++) {
      isHidden[i] = false;
    }
  }
  public Vial(int[] colors, int depth) {
    int[] temp = new int[depth];
    for(int i=0; i<depth; i++) {
      temp[i] = colors[i];
      isHidden[i] = false;
    }
    this.colors = temp;
    this.depth = depth;
  }
  public Vial(int depth) {
    int[] colors = new int[depth];
    this.depth = depth;
    for(int i=0; i<depth; i++) {
      colors[i] = 0;
      isHidden[i] = false;
    }
    this.colors = colors;
  }
  public String getColor(int index) {
    return colorVals[colors[index]];
  }
  public int getColorVal(int index) {
    return colors[index];
  }
  public int getDepth() {
    return depth;
  }
  public boolean isMonochrome() {
    if(getTop() == -1) return true;
    int base = getTop();
    for(int i=0; i<numFull(); i++) {
      if(colors[i] != base && colors[i] != 0) {
        return false;
      }
    }
    return true;
  }
  public boolean isFullSameColor() {
    try {
      int base = colors[0];
      for(int i=0; i<colors.length; i++) {
        if(base != colors[i]) {
          return false;
        }
      }
    } catch(NullPointerException e) {
      return false;
    }
    return true;
  }
  public boolean isFull() {
    if(getDepth() == getDepth()-getTopIndex()) {
      return true;
    }
    return false;
  }
  public boolean isEmpty() {
    if(colors[depth-1] == 0) {
      return true;
    } 
    return false;
  }
  public int numEmpty() {
    return getTopIndex();
  }
  public int numFull() {
    return depth-getTopIndex();
  }
  public int getTop() {
    for(int i=0; i<depth; i++) {
      if(colors[i] != 0) {
        return colors[i];
      }
    }
    return -1;
  }
  public int getTopIndex() {
    if(isEmpty()) {
      return depth-1;
    }
    for(int i=0; i<depth; i++) {
      if(colors[i] != 0) {
        return i;
      }
    }
    return -1;
  }
  public void removeTop() {
    colors[getTopIndex()] = 0;
  }
  public boolean addTop(Vial v) {
    if(isFull() || v.isEmpty() || (getTop() != v.getTop() && isEmpty() == false)) {
      return false;
    }
    if(isEmpty()) {
      colors[getTopIndex()] = v.getTop();
      v.removeTop(); 
    }
    for(int i=v.getTopIndex(); i<v.getDepth()+1; i++) {
      if(getTop() == v.getTop() && v.getTop() != 0 && isFull() == false) {
        colors[getTopIndex()-1] = v.getTop();
        v.removeTop();
      } else {
        break;
      }
    }
    int base = v.getTop();
    for(int i=v.getTopIndex(); i<v.getDepth(); i++) {
      if(v.getColorVal(i) == base) {
        v.setVisibility(i, false);
      } else {
        break;
      }
    }
    for(int i=0; i<depth; i++) {
      if(v.getColorVal(i) == 0) {
        v.setVisibility(i, false);
      } 
    }
    return true;
  }
  public void addTop(int color) {
    colors[getTopIndex()-1] = color;
  }
  public int getTopAmount() {
    int base = getTop();
    int num = 0;
    if(isEmpty()) return 0;
    for(int i=getTopIndex(); i<depth; i++) {
      if(colors[i] == base) {
        num++;
      } else {
        return num;
      }
    }
    return num;
  }
  public void toggleVisibility(int pos) {
    isHidden[pos] = !isHidden[pos];
  }
  public void setVisibility(int pos, boolean value) {
    isHidden[pos] = value;
  }
  public void setTopVisibility(boolean value) {
    isHidden[getTopIndex()] = value;
  }
  public boolean isHidden(int pos) {
    return isHidden[pos];
  }
  public void resetVisibility() {
    for(int i=1; i<depth; i++) {
      isHidden[i] = true;
    }
  }
}