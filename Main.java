//Play at replit.com/@SamuelEgeli/Vial-Sort - Made by Samuel Egeli
import java.util.*;
import java.io.*;
class Main {
  public static String[] colors = {
    "\u001b[0m", //Clear
    "\u001B[38;5;57m", //Purple
    "\u001B[38;5;49m", //Green
    "\u001B[38;5;39m", //Blue
    "\u001B[38;5;196m" //Red
  };
  public static Scanner sc = new Scanner(System.in);
  public static Setting[] settings = new Setting[10];
  public static Map vialMap = new Map();
  public static boolean hasMapGenerated = false;
  public static String searchFilter = "";
  public static String mapSel;
  public static void main(String[] args) throws IOException {
    System.out.println("Starting...");
    String[] options = {"V", "U", "W", "Y", "µ"};
    settings[0] = new Setting((Integer)(9), "Number of Full Vials", 3, 42);
    settings[1] = new Setting((Integer)(2), "Number of Empty Vials", 1, 742);
    settings[2] = new Setting((Integer)(5), "Vial Depth", 2, 742);
    settings[3] = new Setting((Boolean)(true), "Show Moves and Restarts", 0, 0);
    settings[4] = new Setting((Boolean)(false), "Show Time", 0, 0);
    settings[5] = new Setting((Boolean)(false), "Show Hints", 0, 0);
    settings[6] = new Setting((Boolean)(false), "Hidden Mode", 0, 0);
    settings[7] = new Setting((Boolean)(false), "Is Computer Playing", 0, 0);
    settings[8] = new Setting("V", "Vial Letter", options, true, 1);
    settings[9] = new Setting((Integer)(10), "Number of Solutions Generated", 1, 742);
    readSettings();
    updateMapList();
    updateLeaderboard();
    vialMap.setSettings((boolean)settings[6].getValue(), (String)settings[8].getValue());
    while(true) {
      mapSel = getMapSelection();
      readSettings();
      playGame();
    }
  }
  public static void playGame() throws IOException {
    boolean showHints = false;
    boolean isInputValid = true;
    int[] vialSel = new int[2];
    String input;
    vialMap.setPath(mapSel);
    vialMap.readMap();
    vialMap.reset();
    System.out.print("\033[2J\033[1;1H");
    vialMap.getTracker().startTimer();
    while(true) {
      String[] hints = vialMap.getHints();
      System.out.print("\033[2J\033[1;1H");
      System.out.println(vialMap.getTracker().getStats((boolean)settings[3].getValue(), (boolean)settings[5].getValue()));
      if((boolean)settings[5].getValue()) showHints = true;
      System.out.print(vialMap);
      if((boolean)settings[7].getValue()) {
        vialSel = computerTurn();
        vialMap.pour(vialSel[1], vialSel[0]);
        if(vialMap.checkLoss()) {
          vialMap.reset();
        } else if(vialMap.checkWin()) {
          break;
        }
      } else {
        isInputValid = inputValidation(isInputValid);
        System.out.println(colors[2]+"Enter \""+colors[3]+"r"+colors[2]+"\" to restart, \""+colors[3]+"h"+colors[2]+"\" to display possible moves, or \""+colors[3]+"e"+colors[2]+"\" to exit.");
        if(showHints) {
        for(int i=0; i<hints.length; i++) {
          if(hints[i] != null) {
            System.out.println(hints[i]);
          }
        }
        showHints = false;
      }
        System.out.println(colors[1]+"Select a vial ("+colors[3]+"1"+colors[1]+"-"+colors[3]+vialMap.getTotalVials()+colors[1]+")"+colors[3]);
        input = sc.nextLine();
        if(isNumber(input) && Integer.valueOf(input) > 0 && Integer.valueOf(input) <= vialMap.getTotalVials()) {
          vialSel[0] = Integer.valueOf(input);
          System.out.println(colors[1]+"Select another vial ("+colors[3]+"1"+colors[1]+"-"+colors[3]+vialMap.getTotalVials()+colors[1]+")"+colors[3]);
          input = sc.nextLine();
          if(isNumber(input) && Integer.valueOf(input) > 0 && Integer.valueOf(input) <= vialMap.getTotalVials()) {
            vialSel[1] = Integer.valueOf(input);
            if(!(vialMap.pour(vialSel[1], vialSel[0]))) {
              isInputValid = false;
            }
          } else {
            isInputValid = false;
          }
        } else if(input.equalsIgnoreCase("r")) {
          vialMap.reset();
        } else if(input.equalsIgnoreCase("h")) {
          showHints = true;
        } else if(input.equalsIgnoreCase("e") || input.equalsIgnoreCase("exit")) {
          break;
        } else if(input.equalsIgnoreCase("How to Play")) {
          System.out.println(colors[1]+"Vial sort is a puzzle game. There are vials separated into sections. How many sections there are depends on the level, but there will be at least 2 per vial. In each section there will be a different color. Use the numbers listed below each vial to move colors from one vial to the next. Your first selection is the vial you want to move and your second vial is your vial you want to move the color into. You can only move a color if both of the selected vials have the same color on top. The goal is to have every vial only contain one color. You can re-open this text while playing anytime by typing \""+colors[3]+"How to Play"+colors[1]+"\"\nPress "+colors[3]+"enter"+colors[1]+" to continue."+colors[3]);
          sc.nextLine();
        } else {
          isInputValid = false;
        }
        if(vialMap.checkWin()) {
          break;
        } else if(vialMap.checkLoss()) {
          System.out.print("\033[2J\033[1;1H");
          System.out.print(vialMap);
          System.out.println("You Lost!\nPress enter to retry.");
          sc.nextLine();
          vialMap.reset();
        }
      }
    }
    if(vialMap.checkWin()) {
      System.out.print("\033[2J\033[1;1H");
      System.out.print(vialMap);
      System.out.println(colors[2]+"You Win!"+colors[1]);
      System.out.println(vialMap.getTracker());
      if(mapSel.equals("Maps/Generated Map.txt")) {
        String fileName = vialMap.saveMap();
        System.out.println(colors[1]+"Map saved as "+colors[3]+fileName);
        /*fileName = "Generated Maps/"+fileName+".txt";
        Record r = getRecord(fileName);
        ArrayList<Record> records = updateLeaderboard();
        r.setCompMoves(vialMap.getTracker().getNumMoves());
        setRecord(r);*/
        hasMapGenerated = false;
      } else {
        Record r = getRecord(vialMap.getPath());
        if((boolean)settings[7].getValue() && (r.getCompMoves() > vialMap.getTracker().getNumMoves() || r.getCompMoves() == -1)) {
          r.setCompMoves(vialMap.getTracker().getNumMoves());
          setRecord(r);
        } else {
          r.setTime(vialMap.getTracker().getTimeDif());
          r.setHuMoves(vialMap.getTracker().getNumMoves());
          setRecord(r);
        }
      }
      System.out.println(colors[1]+"Press "+colors[3]+"enter"+colors[1]+" to play again or enter \""+colors[3]+"save"+colors[1]+"\" to save as a replay."+colors[2]);
      input = sc.nextLine();
      if(input.equalsIgnoreCase("save") || input.equalsIgnoreCase("s")) {
        vialMap.getTracker().save();
        Replay replay = vialMap.getTracker().getSaves()[0];
        System.out.println(colors[1]+"Replay saved as "+colors[3]+replay.saveReplay());
        vialMap.getTracker().resetSaves();
        sc.nextLine();
      }
    }
  }
  public static String getMapSelection() throws IOException {
    File folder = new File("Generated Maps");
    File[] maps = new File[742];
    File map;
    ArrayList<String> fileList = updateMapList();
    for(int i=0; i<fileList.size(); i++) {
      maps[i] = new File("Generated Maps/"+fileList.get(i)+".txt");
    }
    boolean isInputValid = true;
    boolean isChosen = false;
    String input;
    int layer = 0;
    while(true) {
      System.out.print("\033[2J\033[1;1H");
      isInputValid = inputValidation(isInputValid);
      System.out.println(colors[1]+"Welcome to "+colors[2]+"Vial Sort!"+colors[3]+"\n - 1. "+colors[1]+"Select Map"+colors[3]+"\n - 2. "+colors[1]+"Generate Map"+colors[3]+"\n - 3. "+colors[1]+"View Replays"+colors[3]+"\n - 4. "+colors[1]+"Options"+colors[3]+"\n - 5. "+colors[1]+"Leaderboard"+colors[3]+"\n - 6. "+colors[1]+"How to Play"+colors[2]);
      input = sc.nextLine();
      if(input.equals("1") || input.equalsIgnoreCase("Select Map") || input.equalsIgnoreCase("Select")) {
        boolean caseSensitive = false;
        while(true) {
          String[] fileNames = new String[7420];
          fileList = updateMapList();
          if(searchFilter.equals("")) {
            for(int i=0; i<fileList.size(); i++) {
              maps[i] = new File("Generated Maps/"+fileList.get(i)+".txt");
              fileNames[i] = fileList.get(i);
            }
          } else {
            ArrayList<String> aList = filterMapList(fileList, searchFilter, caseSensitive);
            fileNames = ArrayListtoStringArray(aList);
          }
          System.out.print("\033[2J\033[1;1H");
          int index = navigateList(fileNames, layer, isInputValid, caseSensitive);
          isInputValid = true;
          if(index == 0) break;
          if(index == -1) isInputValid = false;
          if(index == -2) layer--;
          if(index == -3) layer++;
          if(index == -5) {
            caseSensitive = !caseSensitive;
            navigateList(fileNames, layer, isInputValid, caseSensitive);
          }
          if(index > 0) {
            map = maps[index-1];   
            isChosen = mapOptions(map, maps);
            if(isChosen) return map.toString();
          }
        }
        layer = 0;
      } else if(input.equals("2") || input.equalsIgnoreCase("Generate Map") || input.equalsIgnoreCase("Generate")) {
        map = new File("Maps/Generated Map.txt");
        isChosen = mapOptions(map, maps);
        if(isChosen) return map.toString();
      } else if(input.equals("3") || input.equalsIgnoreCase("View Replays") || input.equalsIgnoreCase("Replays")) {
        String[] fileNames = new String[7420];
        File[] replays = new File("Replays").listFiles();
        for(int i=0; i<replays.length; i++) {
          fileNames[i] = replays[i].getName().substring(0, replays[i].getName().length()-4);
        }
        while(true) {
          System.out.print("\033[2J\033[1;1H");
          int index = navigateList(fileNames, layer, isInputValid, false);
          isInputValid = true;
          if(index == 0) break;
          if(index == -1) isInputValid = false;
          if(index == -2) layer--;
          if(index == -3) layer++;
          if(index > 0) {
            String replayPath = replays[index-1].toString();
            Replay replay = new Replay(replayPath);
            viewReplay(replay, true);
          }
        }
        layer = 0;
      } else if(input.equals("4") || input.equalsIgnoreCase("Options")) {
        optionMenu();
        readSettings();
      } else if(input.equals("5") || input.equalsIgnoreCase("Leaderboard") || input.equalsIgnoreCase("Records")) {
        ArrayList<Record> records = updateLeaderboard();
        String[] rNames = new String[7420];
        for(int i=0; i<records.size(); i++) {
          rNames[i] = records.get(i).getFileName();
        }
        while(true) {
          System.out.print("\033[2J\033[1;1H");
          int index = navigateList(rNames, layer, isInputValid, false);
          isInputValid = true;
          if(index == 0) break;
          if(index == -1) isInputValid = false;
          if(index == -2) layer--;
          if(index == -3) layer++;
          if(index > 0) {
            System.out.print("\033[2J\033[1;1H");
            Record r = getRecord("Generated Maps/"+rNames[index-1]+".txt");
            System.out.println(r.printValues());
            sc.nextLine();
          }
        }
        System.out.println(colors[4]+"Feature not yet implimented"+colors[1]);
        sc.nextLine();
      } else if(input.equals("6") || input.equalsIgnoreCase("How to Play")) {
        System.out.println(colors[1]+"Vial sort is a puzzle game. There are vials separated into sections. How many sections there are depends on the level, but there will be at least 2 per vial. In each section there will be a different color. Use the numbers listed below each vial to move colors from one vial to the next. Your first selection is the vial you want to move and your second vial is your vial you want to move the color into. You can only move a color if both of the selected vials have the same color on top. The goal is to have every vial only contain one color. You can re-open this text while playing anytime by typing \""+colors[3]+"How to Play"+colors[1]+"\"\nPress "+colors[3]+"enter"+colors[1]+" to continue."+colors[3]);
        sc.nextLine();
      } else {
        isInputValid = false;
      }
    }
  }
  public static boolean mapOptions(File map, File[] maps) throws IOException {
    boolean isGeneratedMap = false;
    int testResult = 0, layer = 0;
    if(map.toString().equals("Maps/Generated Map.txt") && !(hasMapGenerated)) {
      hasMapGenerated = true;
      generateMap();
    }
    vialMap.setPath(map.toString());
    vialMap.readMap();
    String fileName = map.getName().substring(0, map.getName().lastIndexOf("."));
    boolean isInputValid = true;
    String input;
    while(true) {
      System.out.print("\033[2J\033[1;1H");
      isInputValid = inputValidation(isInputValid);
      if(map.toString().equals("Maps/Generated Map.txt")) {
        if(vialMap.getPossible()) {
          System.out.println(colors[1]+"This map has been confirmed to be "+colors[3]+"possible"+colors[1]+".");
        } else {
          System.out.println(colors[1]+"There is no guarentee a generated map will be solvable. Use 2. Test to test. The larger a map is, the longer the test may take.");
        }
        isGeneratedMap = true;
      }
      System.out.println(colors[2]+fileName+colors[1]+", Vials: "+colors[3]+vialMap.getTotalVials()+colors[1]+" Depth: "+colors[3]+vialMap.getDepth());
      System.out.print(vialMap);
      System.out.print(colors[2]+"Select an option: "+colors[3]+"\n - 1. "+colors[1]+"Play");
      if(isGeneratedMap) {
        System.out.print(colors[3]+"\n - 2. "+colors[1]+"Test");
        if(testResult == 1) System.out.print(colors[3]+" - "+colors[4]+"Failure");
        if(testResult == 2) System.out.print(colors[3]+" - "+colors[2]+"Success");
        if(testResult != 0) testResult = 0;
      } else {
        System.out.print(colors[3]+"\n - 2. "+colors[1]+"Rename");
      }
      System.out.print(colors[3]+"\n - 3. "+colors[1]+"Generate Solutions");
      if(isGeneratedMap) {
        System.out.print(colors[3]+"\n - 4. "+colors[1]+"Generate New Map"+colors[3]);
      } else {
        System.out.print(colors[3]+"\n - 4. "+colors[1]+"Delete"+colors[3]);
      }
      System.out.println(colors[3]+"\n - 5. "+colors[1]+"Back"+colors[3]);
      input = sc.nextLine();
      if(input.equals("1") || input.equalsIgnoreCase("Play")) {
        return true;
      } else if((input.equals("2") || input.equalsIgnoreCase("Rename")) && !(isGeneratedMap)) {
        while(true) {
          System.out.print("\033[2J\033[1;1H");
          if(!isInputValid) {
            System.out.println(colors[4]+"Invalid Name!"+colors[0]);
            System.out.println("You cannot rename a Map to a number or a map that already exists.");
            isInputValid = true;
          }
          System.out.println(colors[1]+"What will you rename "+colors[2]+"\""+fileName+"\""+colors[1]+" to? or "+colors[2]+"cancel");
          input = sc.nextLine();
          if(input.equalsIgnoreCase("cancel")) break;
          else {
            for(int i=0; i<maps.length; i++) {
              if(maps[i].getName().equals(input+".txt")) {
                isInputValid = false;
                break;
              }
            }
            if(input.length() > 3 && input.substring(0, 3).equals("Map")) {
              if(isNumber(input.substring(4, input.length()-1))) {
                isInputValid = false;
              }
            }
            //Add an input invalidation for "Map xx.txt"
            if(isInputValid) {
              File temp = new File("Generated Maps/"+input+".txt");
              map.renameTo(temp);
              return false;
            }
          }
        }
      } else if((input.equals("2") || input.equals("Test")) && isGeneratedMap) {
        double testTime = getTestTime();
        Replay[] rp = generateSolutions(true, testTime);
        if(rp[0] != null) {
          vialMap.setPossible();
          testResult = 2;
        } else {
          testResult = 1;
        }
      } else if(input.equals("3") || input.equalsIgnoreCase("Generate Solutions")) {
        vialMap.setPath(map.toString());
        double testTime = getTestTime();
        Replay[] replays = generateSolutions(false, testTime);
        String[] replayNames = new String[7420];
        for(int i=0; i<replays.length; i++) {
          if(replays[i] != null) {
            replayNames[i] = "Replay "+(i+1);
          }
        }
        while(true) {
          System.out.print("\033[2J\033[1;1H");
          int index = navigateList(replayNames, layer, isInputValid, false);
          isInputValid = true;
          if(index == 0) break;
          if(index == -1) isInputValid = false;
          if(index == -2) layer--;
          if(index == -3) layer++;
          if(index > 0) {
            try {
              if(isGeneratedMap) viewReplay(replays[index-1], true);
              else viewReplay(replays[index-1], false);
            } catch (Exception e) {
              System.out.println(colors[4]+"Failed to display replay:\n"+e+colors[0]);
              sc.nextLine();
            }
          }
        }
        vialMap.getTracker().reset(map.toString());
      } else if(input.equals("4")) {
        if(isGeneratedMap) {
          hasMapGenerated = false;
          mapOptions(map, maps);
        } else {
          ArrayList<String> mapList = updateMapList();
          for(int i=0; i<mapList.size(); i++) {
            if((mapList.get(i)+".txt").equals(map.getName())) {
              mapList.remove(i);
              setMapList(mapList);
              map.delete();
              break;
            }
          }
          return false;
        } 
      } else if(input.equals("5") || input.equalsIgnoreCase("Back") || input.equalsIgnoreCase("Exit") || input.equalsIgnoreCase("e")) {
        vialMap.getTracker().resetSaves();
        return false;
      } else {
        isInputValid = false;
      }
    }
  }
  public static int navigateList(String[] list, int layer, boolean isInputValid, boolean caseSensitive) {
    //return 0 - Exit, -1 - Invalid, -2 - Prev, -3 - Next, > 0, -4 filter: do not change caseSensitive, -5 filter: change caseSensitive - Index Selection + 1
    String input;
    int length = 0;
    int ret = -4;
    for(int i=0; i<list.length; i++) {
      if(list[i] != null) length++;
    }
    if(length == 0) {
      length++;
      if(layer != 0) return -3;
    }
    isInputValid = inputValidation(isInputValid);
    System.out.println(colors[2]+"Select by number or name or use \""+colors[3]+"next page"+colors[2]+"\", \""+colors[3]+"prev page"+colors[2]+"\", \""+colors[3]+"search"+colors[2]+"\", or \""+colors[3]+"exit"+colors[2]+"\":"+colors[0]);
    for(int i=10*layer; i<10*(layer+1) && (10*(layer+1) < length || i<length); i++) {
      System.out.println(colors[3]+" - "+(i+1)+". "+colors[1]+list[i]);
    }
    System.out.println(colors[2]+"Current page: "+colors[3]+(layer+1)+colors[2]+" of "+colors[3]+((int)(Math.floor(length/10.1))+1));
    input = sc.nextLine();
    if((input.equalsIgnoreCase("next page") || input.equalsIgnoreCase("next") || input.equalsIgnoreCase("n")) && layer*10 < length-10) {
      return -3;
    } else if((input.equalsIgnoreCase("prev page") || input.equalsIgnoreCase("prev") || input.equalsIgnoreCase("p")) && layer != 0) {
      return -2;
    } else if(input.equalsIgnoreCase("search") || input.equalsIgnoreCase("s")) {
      while(true) {
        System.out.print("\033[2J\033[1;1H");
        System.out.println(colors[1]+"Enter a "+colors[3]+"word"+colors[1]+" to search for or enter \""+colors[3]+"-1"+colors[1]+"\" to toggle case sensistivity. \nCase Senensitive: "+colors[3]+caseSensitive+"\nPress "+colors[3]+"enter"+colors[1]+" to reset filter."+colors[3]);
        input = sc.nextLine();
        if(input.equals("-1")) {
          caseSensitive = !caseSensitive;
          if(ret == -4) ret = -5;
          if(ret == -5) ret = -4;
        } else {
          searchFilter = input;
          return ret;
        }
      }
    } else if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
      return 0;
    } else if(isNumber(input)) {
      int num = Integer.valueOf(input);
      if(num > 0 && num <= length+1) return num;
    } else {
      for(int i=0; i<length; i++) {
        if(input.equals(list[i])) {          
          return i+1;
        }
      }
    }
    return -1;
  }
  public static double getTestTime() {
    boolean isInputValid = true;
    String input;
    double testTime;
    while(true) {
      System.out.print("\033[2J\033[1;1H");
      isInputValid = inputValidation(isInputValid);
      System.out.println(colors[1]+"Choose a time in "+colors[3]+"seconds"+colors[1]+" for which to search for a solution."+colors[3]);
      input = sc.nextLine();
      if(isNumber(input)) {
        testTime = Double.valueOf(input);
        break;
      } else if(input.equals("")) {
        testTime = 3;
        break;
      } else {
        isInputValid = false;
      }
    }
    return testTime;
  }
  public static void optionMenu() throws IOException {
    boolean isInvalid = false;
    String move = "";
    String[] desc = {
      "Number of vials that are full. Also the number of colors to be sorted.",
      "Number of vials that are empty.",
      "How many units down each vial will be.",
      "Shows a display at the top indicating how many times you have restarted and how many moves you have made this restart. When the computer plays this is always true.",
      "Evertime you make a move and at the end a timer is updated. When the computer plays this is always true.",
      "Shows all your possible moves unless there is an empty vial. You do not want that filling up your screen.",
      "With hidden mode enabled only the top color of a vial is shown. When a color is moved off of the top, the one below is revealed. Colors that have been revealed will not be re-hidden.",
      "Skill issue? Can't win without hints on? Well the computer does not have such weaknesses.",
      "An aesthetic option that changes the appearance of the bottom of the vial. There is only one right answer though.",
      "When generating solutions this number determines how many solutions should be generated before stopping if the time does not expire first."
    };
    while(true) {
      System.out.print("\033[2J\033[1;1H");
      System.out.println(colors[3]+"Options:");
      for(int i=0; i<desc.length; i++) {
        System.out.println(colors[3]+" - "+(i+1)+". "+colors[1]+settings[i].getName()+colors[2]+": "+colors[3]+settings[i]);
        System.out.println(colors[2]+desc[i]+colors[0]);
      }
      System.out.println(colors[3]+" - "+(desc.length+1)+colors[1]+". Exit to main menu."+colors[0]);
      if(isInvalid) {
        System.out.print("\u001B[38;5;196mInvalid Selection."+colors[0]);
      }
      System.out.print(colors[1]+"\nChoose an option to edit. "+colors[3]);
      move = sc.nextLine();
      if(move.equalsIgnoreCase("Exit") || move.equals((desc.length+1)+"") || move.equalsIgnoreCase("e")) break;
      try {
        settings[Integer.valueOf(move)-1].setValue();
      } catch(Exception e) {
        isInvalid = true;
      }
    }
    FileWriter fw1 = new FileWriter("Options.txt");
    FileWriter fw2 = new FileWriter("Options.backup");
    for(int i=0; i<settings.length; i++) {
      if(settings[i] != null) {
        fw1.write(settings[i].getName()+": "+settings[i]+";\n");
        fw2.write(settings[i].getName()+": "+settings[i]+";\n");
      }
    }
    vialMap.setSettings((boolean)settings[6].getValue(), (String)settings[8].getValue());
    hasMapGenerated = false;
    fw1.close();
    fw2.close();
  }
  public static void readSettings() throws IOException {
    File file = new File("Options.txt");
    FileReader fr = new FileReader(file);
    char[] letters = new char[10000];
    fr.read(letters);
    int count = 0, start = 0, end = 0;
    for(int i=0; i<letters.length; i++) {
      if(letters[i] == ':') {
        count++;
        start = i+1;
      }
      if(letters[i] == ';') {
        end = i;
        String temp = "";
        for(int j=start+1; j<end; j++) {
          temp += letters[j];
        }
        settings[count-1].setValueFromString(temp);
      }
    }
    if(count == 0) {
      file = new File("Options.backup");
      fr = new FileReader(file);
      FileWriter fw = new FileWriter("Options.txt");
      fr.read(letters);
      fw.write(letters);
      fw.close();
    }
    fr.close();
  }
  public static boolean setRecord(Record r) throws IOException {
    int compMoves = r.getCompMoves(), huMoves = r.getHuMoves();
    long time = r.getTime();
    ArrayList<Record> records = updateLeaderboard();
    for(int i=0; i<records.size(); i++) {
      if(records.get(i).getFilePath().equals(r.getFilePath())) {
        records.set(i, r);
        writeRecords(records);
        return true;
      }
    }
    return false;
  }
  public static Record getRecord(String map) throws IOException {
    ArrayList<Record> records = updateLeaderboard();
    for(int i=0; i<records.size(); i++) {
      if(records.get(i).getFilePath().equals(map)) {
        return records.get(i);
      }
    }
    return null;
  }
  public static ArrayList<Record> updateLeaderboard() throws IOException {
    ArrayList<String> mapList = updateMapList();
    ArrayList<Record> records = new ArrayList<Record>();
    FileReader fr = new FileReader("Leaderboard.txt");
    char[] a = new char[10000];
    fr.read(a);
    fr.close();
    String mapName = "", temp = "";
    int compMoves = -1, huMoves = -1, phase = 0, numRecords = 0;
    long time = 0;
    for(int i=0; i<a.length; i++) {
      if(a[i] != ':' && a[i] != ';' && a[i] != '\n') {
        temp += a[i];
      } else if(a[i] == ':') {
        if(phase == 0) mapName = temp;
        if(phase == 1 && isNumber(temp)) compMoves = Integer.valueOf(temp);
        if(phase == 2 && isNumber(temp)) huMoves = Integer.valueOf(temp);
        temp = "";
        phase++;
      } else if(a[i] == ';') {
        if(phase == 3 && isNumber(temp)) time = Long.valueOf(temp);
        if(mapName.equals("") == false) {
          records.add(new Record(mapName, compMoves, huMoves, time));
          mapName = "";
          compMoves = -1;
          huMoves = -1;
          time = -1;
          numRecords++;
          temp = "";
          phase = 0;
        }
      }
    }
    for(int i=0; i<mapList.size(); i++) {
      for(int j=0; j<numRecords; j++) {
        if(mapList.get(i).equals(records.get(j).getFileName())) break;
        else if(!mapList.get(i).equals(records.get(j).getFileName()) && j == numRecords-1) {
          records.add(new Record(("Generated Maps/"+mapList.get(i)+".txt"), -1, -1, -1));
          numRecords++;
        }
      }
    }
    writeRecords(records);
    return records;
  }
  public static void writeRecords(ArrayList<Record> records) throws IOException{
    FileWriter fw = new FileWriter("Leaderboard.txt");
    for(int i=0; i<records.size(); i++) {
      fw.write(records.get(i)+"\n");
    }
    fw.close();
  }
  public static ArrayList<String> updateMapList() throws IOException {
    FileReader fr = new FileReader("FileOrder.txt");
    char[] a = new char[10000];
    fr.read(a);
    fr.close();
    ArrayList<String> storedMaps = new ArrayList<String>();
    String fileName;
    int numMaps = 0;
    String temp = "";
    for(int i=0; i<a.length; i++) {
      if(a[i] != ';' && a[i] != '\n') {
        temp += a[i];
      } else if(a[i] != '\n') {
        storedMaps.add(temp);
        numMaps++;
        temp = "";
      }
    }
    File[] folderMaps = new File("Generated Maps").listFiles();
    for(int i=0; i<folderMaps.length; i++) {
      for(int j=0; j<numMaps; j++) {
        fileName = folderMaps[i].getName().substring(0, folderMaps[i].getName().lastIndexOf("."));
        if(fileName.equals(storedMaps.get(j))) break;
        else if(!fileName.equals(storedMaps.get(j)) && j == numMaps-1) {
          storedMaps.add(fileName);
          numMaps++;
        }
      }
    }
    setMapList(storedMaps);
    return storedMaps;
  }
  public static void setMapList(ArrayList<String> storedMaps) throws IOException{
    FileWriter fw = new FileWriter("FileOrder.txt");
    for(int i=0; i<storedMaps.size(); i++) {
      fw.write(storedMaps.get(i)+";\n");
    }
    fw.close();
  }
  public static ArrayList<String> filterMapList(ArrayList<String> list, String filter, boolean ignoreCase) {
    ArrayList<String> newList = new ArrayList<String>();
    for(int i=0; i<list.size(); i++) {
      if(ignoreCase) {
        String temp = filter.toLowerCase();
        if(list.get(i).toLowerCase().contains(temp)) {
          newList.add(list.get(i));
        }
      } else {
        if(list.get(i).contains(filter)) {
          newList.add(list.get(i));
        }
      }
    }
    return newList;
  }
  public static void generateMap() throws IOException {
    boolean isValid = true;
    int fullVials = (int)settings[0].getValue();
    int totalVials = fullVials + (int)settings[1].getValue();
    int depth = (int)settings[2].getValue();
    int rand1, rand2;
    System.out.println("Generating...");
    FileWriter fw = new FileWriter("Maps/Generated Map.txt");
    int[] colors = new int[fullVials+1];
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    for(int i=1; i<43; i++) {
      numbers.add(i);
    }
    for(int i=0; i<colors.length-1; i++) {
      rand1 = (int)(Math.random()*numbers.size());
      colors[i] = numbers.get(rand1);
      numbers.remove(rand1);
    }
    int[][] values = new int[totalVials+1][depth];
    for(int i=0; i<totalVials; i++) {
      if(i < fullVials) {
        for(int j=0; j<depth; j++) {
          do {
            isValid = true;
            rand1 = (int)(Math.random()*(fullVials));
            rand2 = (int)(Math.random()*(depth));
            if(values[rand1][rand2] != 0) {
              isValid = false;              
            }
          } while(isValid == false);
          values[rand1][rand2] = colors[i];
        }
      }
    }
    fw.write("(Generated Map):\n");
    for(int i=0; i<totalVials; i++) {
      fw.write("{");
      for(int j=0; j<depth; j++) {
        if(j != depth-1) {
          fw.write(values[i][j]+", ");
        } else {
          fw.write(values[i][j]+"");
        }
      }
      fw.write("};\n");
    }
    fw.close();
  }
  public static Replay[] generateSolutions(boolean runOnce, double maxTime) throws IOException {
    long startTime = System.currentTimeMillis();
    int numWins = 0;
    int nextTime = 1;
    System.out.print("\033[2J\033[1;1H");
    System.out.println(colors[1]+"Searching... "+colors[3]+maxTime+colors[1]+"s remaining.");
    while(true) {
      int[] vialSel = computerTurn();
      vialMap.pour(vialSel[1], vialSel[0]);
      if(vialMap.checkLoss()) {
        vialMap.reset();
      } else if(vialMap.checkWin()) {
        vialMap.getTracker().save();
        vialMap.reset();
        if(runOnce) break;
        numWins++;
        if(numWins >= (Integer)settings[9].getValue()) break;
      }
      if(System.currentTimeMillis()-startTime > nextTime*1000) {
        System.out.print("\033[2J\033[1;1H");
        System.out.println(colors[1]+"Searching... "+colors[3]+(maxTime-nextTime)+colors[1]+"s remaining.");
        nextTime++;
      }
      if(System.currentTimeMillis()-startTime > maxTime*1000) {
        break;
      }
    }
    vialMap.reset();
    return vialMap.getTracker().getSaves();
  }
  public static void viewReplay(Replay replay, boolean readOnly) throws IOException {
    vialMap.setPath(replay.getPath());
    vialMap.readMap();
    String input = "";
    int numMoves = replay.getNumMoves();
    byte[][] moves = replay.getMoves();
    int move = 0;
    while(true) {
      System.out.print("\033[2J\033[1;1H");
      vialMap.reset();
      for(int i=0; i<move; i++) {
        int[] vialSel = new int[2];
        vialSel[1] = moves[1][i];
        vialSel[0] = moves[0][i];
        vialMap.pour(vialSel[0], vialSel[1]);
      }
      System.out.print(vialMap);
      System.out.print(colors[1]+"Enter \""+colors[3]+"prev"+colors[1]+"\" or \""+colors[3]+"next"+colors[1]+"\" to view the next/previous step. Use \""+colors[3]+"exit\""+colors[1]+" to exit");
      if(!readOnly) {
        System.out.println(" or \""+colors[3]+"save\""+colors[1]+" to save replay to file.");
      } else {
        System.out.println(".");
      }
      System.out.println(colors[1]+"Step "+colors[3]+(move+1)+colors[1]+" of "+colors[3]+(numMoves+1)+colors[2]);
      input = sc.nextLine();
      if((input.equalsIgnoreCase("prev") || input.equalsIgnoreCase("p")) && move != 0) {
        move--;
      } else if((input.equalsIgnoreCase("next") || input.equalsIgnoreCase("n")) && move != numMoves) {
        move++;
      } else if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
        break;
      } else if(input.equalsIgnoreCase("save") || input.equalsIgnoreCase("s")) {
        System.out.println(colors[1]+"Replay saved as "+colors[3]+replay.saveReplay());
        sc.nextLine();
      }
    }
  }
  public static int[][] getMoves(Vial[] vials) {
    int numVials = vialMap.getTotalVials();
    int[][] moves = new int[2][42];
    /*boolean isMonochrome = true;
    for(int i=0; i<numVials; i++) {
      if(vials[i].getTop() != -1 && !(vials[i].isMonochrome())) {
        isMonochrome = false;
      }
    }
    for(int i=0; i<numVials; i++) {
      for(int j=0; j<numVials; j++) {
        if(i != j && vials[i].getTop() == vials[j].getTop()) {
          if(vials[j].get)
        }
      }
    }*/
    int freeSpace = 0, count = 0, rand;
    boolean[] movable = new boolean[numVials];
    boolean isMonochrome = true;
    for(int i=0; i<numVials; i++) {
      if(vials[i].getTop() != -1 && !(vials[i].isMonochrome())) {
        isMonochrome = false;
      }
    }
    if(!isMonochrome) {
      for(int i=0; i<numVials; i++) {
        if(vials[i].getTop() == -1) {
          do {
            rand = (int)(Math.random()*numVials)+1;
            if(!(vials[rand-1].isMonochrome() && vials[rand-1].isEmpty())) break;
          } while(true);
          moves[0][count] = rand;
          moves[1][count] = i+1;
          count++;
        }
      }
    }  
    for(int i=0; i<numVials; i++) {
      freeSpace = 0;
      for(int j=0; j<numVials; j++) {
        if(j!=i) {
          if(vials[i].getTop() == vials[j].getTop()) {
            freeSpace += vials[j].numEmpty();
          }
        }
      }
      if(vials[i].getTopAmount() <= freeSpace) {
        movable[i] = true;
      } else {
        movable[i] = false;
      }
    }
    for(int i=0; i<movable.length; i++) {
      if(movable[i]) {
        for(int j=0; j<numVials; j++) {
          if(j!=i) {
            if(vials[i].getTop() == vials[j].getTop() && vials[j].isFull() == false && vials[i].isFullSameColor() == false) {
              moves[0][count] = i+1;
              moves[1][count] = j+1;
              count++;
            }
          }
        }
      }
    }
    return moves;
  } 
  public static int[] computerTurn() {
    int[] ret = new int[2];
    int[][] moves = getMoves(vialMap.getCurrentVials());
    int numPosMoves = 0, rand;
    for(int i=0; i<moves.length; i++) {
      if(moves[0][i] != 0) {
        numPosMoves++;
      } 
    }
    rand = (int)(Math.random()*numPosMoves);
    ret[0] = moves[0][rand];
    ret[1] = moves[1][rand];
    return ret;
  }
  public static String[] ArrayListtoStringArray(ArrayList<String> list) {
    String[] array = new String[list.size()];
    for(int i=0; i<list.size(); i++) {
      array[i] = list.get(i);
    }
    return array;
  }
  public static ArrayList<String> StringArraytoArrayList(String[] array) {
    ArrayList<String> list = new ArrayList<String>();
    for(int i=0; i<array.length; i++) {
      if(array[i] != null) {
        list.add(array[i]);
      }
    }
    return list;
  }
  public static boolean inputValidation(boolean isInputValid) {
    if(!isInputValid) {
      System.out.println(colors[4]+"Invalid Input"+colors[0]);
      return true;
    }
    return true;
  }
  public static void printSpace(int num) {
    for(int i=0; i<num; i++) {
      System.out.print(" ");
    }
  }
  public static boolean isNumber(char letter){ 
    if(((int)letter>47 && (int)letter<58) || letter == '.'){
      return true;
    }
    return false;
  }
  public static boolean isNumber(String word) {
    if(word.length() == 0) return false;
    for(int i=0; i<word.length(); i++) {
      if(isNumber(word.charAt(i)) == false) {
        return false;
      }
    }
    return true;
  }
}