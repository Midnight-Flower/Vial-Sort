import java.util.*;
class Setting {
  String[] options = new String[10];
  public static String[] colors = {
    "\u001b[0m", //Clear
    "\u001B[38;5;57m", //Purple
    "\u001B[38;5;49m", //Green
    "\u001B[38;5;39m", //Blue
    "\u001B[38;5;196m" //Red
  };
  private boolean anyValue;
  private String name;
  private int type;
  private int min;
  private int max;
  private int val1;
  private double val2;
  private boolean val3;
  private String val4;
  public Setting(Object val, String name, int min, int max) {
    this.name = name;
    this.min = min;
    this.max = max;
    try {
      val1 = (int)val;
      type = 1;
    } catch(Exception e) {
      try {
        val2 = (double)val;
        type = 2;
      } catch(Exception f) {
        try {
          val3 = (boolean)val;
          type = 3;
        } catch(Exception g) {}
      }
    }
  }
  public Setting(Object val, String name, String[] options, boolean anyValue, int max) {
    val4 = (String)val;
    this.name = name;
    this.options = options;
    this.anyValue = anyValue;
    this.max = max;
    type = 4;
  }
  public String toString() {
    if(type == 1) {
      return ""+val1;
    } else if(type == 2) {
      return ""+val2;
    } else if(type == 3) {
      if(val3) return "on";
      else return "off";
    } else if(type == 4) {
      return ""+val4;
    }
    return "";
  }
  public String getName() {
    return name;
  }
  public Object getValue() {
    if(type == 1) return (Integer)val1;
    else if(type == 2) return (Double)val2;
    else if(type == 3) return (Boolean)val3;
    else if(type == 4) return (String)val4;
    return null;
  }
  public void setValue() {
    Scanner sc = new Scanner(System.in);
    String newVal = "";
    System.out.print(colors[1]+"Current value is "+colors[3]);
    if(type == 1) System.out.print(val1+colors[1]+".");
    if(type == 2) System.out.print(val2+colors[1]+".");
    if(type == 3) System.out.print(val3+colors[1]+".");
    if(type == 4) System.out.print(val4+colors[1]+".");
    if(type == 1 || type == 2) {
      System.out.print(colors[1]+" Must be a value from "+colors[3]);
      if(min == -742) System.out.print("-∞"+colors[1]+"-"+colors[3]);
      else if(min == 742) System.out.print("∞"+colors[1]+"-"+colors[3]);
      else System.out.print(min+colors[1]+"-");
      if(max == -742) System.out.print(colors[3]+"-∞");
      else if(max == 742) System.out.print(colors[3]+"∞");
      else System.out.println(colors[3]+max);
    } else if(type == 3) {
      System.out.print(colors[1]+" Must be "+colors[3]+"true"+colors[1]+" or "+colors[3]+"false"+colors[1]+", "+colors[3]+"on"+colors[1]+" or"+colors[3]+" off"+colors[1]+".");
    } else if(type == 4) {
      System.out.print(colors[1]+"\nSelect a value");
      if(anyValue) {
        System.out.print(" or choose your own. Must be less than "+max+" characters");
      } 
      System.out.println(colors[3]+":");
      for(int i=0; i<options.length; i++) {
        System.out.println(colors[3]+" - "+(i+1)+". "+colors[1]+options[i]);
      }
    }
    System.out.println();
    while(true) {
      System.out.print(colors[1]+"Set a new value for "+name+colors[3]+": ");
      newVal = sc.nextLine();
      if(newVal.equalsIgnoreCase("e") || newVal.equalsIgnoreCase("exit") || newVal.equalsIgnoreCase("cancel")) {
        break;
      }
      try {
        if(type == 1) {
          int temp = Integer.valueOf(newVal);
          if(temp >= min && temp <= max) {
            val1 = Integer.valueOf(newVal);
            break;
          }
          else System.out.println("\u001B[38;5;196mInvalid\u001b[0m");
        } else if(type == 2) {
          double temp = Double.valueOf(newVal);
          if(temp >= min && temp <= max) {
            val2 = Double.valueOf(newVal);
            break;
          }
          else System.out.println("\u001B[38;5;196mInvalid\u001b[0m");
        } else if(type == 3) {
          if(newVal.equalsIgnoreCase("on")) val3 = true;
          else if(newVal.equalsIgnoreCase("off")) val3 = false;
          else {
            val3 = Boolean.valueOf(newVal);
            break;
          }
          if(newVal.equalsIgnoreCase("on") || newVal.equalsIgnoreCase("off")) break;
        } else if(type == 4) {
          try {
            val4 = options[Integer.valueOf(newVal)-1];
            break;
          } catch(Exception e) {}
          if(anyValue) {
            if(newVal.length() <= max) {
              val4 = newVal;
            }
          } else {
            for(int i=0; i<options.length; i++) {
              if(newVal.equalsIgnoreCase(options[i])) {
                val4 = newVal;
              }
            }
          }
          if(val4 == newVal) break;
        }
      } catch(Exception e) {
        System.out.println("\u001B[38;5;196mInvalid\u001b[0m");
      }
    }
  }
  public void setValueFromString(String input) {
    if(type == 4) {
      val4 = input;
    }
    try {
      val1 = Integer.valueOf(input);
    } catch(Exception e) {
      try {
        val2 = Double.valueOf(input);
      } catch(Exception f) {
        try {
          if(input.equals("on")) val3 = true;
          else if(input.equals("off")) val3 = false;
          else val3 = Boolean.valueOf(input);
        } catch(Exception g) {}
      }
    }
  }
  public void setMin(int newMin) {
    min = newMin;
  }
  public void setMax(int newMax) {
    max = newMax;
  }
}