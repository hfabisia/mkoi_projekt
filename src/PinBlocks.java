public class PinBlocks {

  private String PIN;
  private String PAN;

  public static String PinBlockEncrypt(String PIN, String PAN){
    String newPAN = preparePAN(PAN);
    String newPIN = preparePIN(PIN);
    String result = xorLongStrings(newPIN, newPAN);
    String checkResult = checkLength(result, (newPAN.length() > newPIN.length() ? newPAN : newPIN));
    return checkResult;
  }

  public static String preparePIN(String PIN){
    String preparePIN = "0";
    Integer length = PIN.length();
    preparePIN = preparePIN + length.toString();
    preparePIN = preparePIN + PIN;
    Integer lengthPreparePIN = preparePIN.length();

    for (int i = 0; i < 16 - lengthPreparePIN; i++){
      preparePIN = preparePIN + "F";
    }

    return preparePIN;
  }

  public static String preparePAN(String PAN){
    String preparePAN = "0000";
    preparePAN = preparePAN + PAN.substring(PAN.length() - 13, PAN.length() - 1);

    return preparePAN;
  }

  public static String xorLongStrings(String s1, String s2){
    int split = ( s1.length() > s2.length()) ?  s2.length()/2 : s1.length()/2;

    String s1First = s1.substring(0, s1.length() - split);
    String s1Last = s1.substring(s1.length() - split, s1.length());

    String s2First = s2.substring(0, s2.length() - split);
    String s2Last = s2.substring(s2.length() - split, s2.length());

    Long s1FirstLong = Long.parseLong(s1First, 16);
    Long s1LastLong = Long.parseLong(s1Last, 16);

    Long s2FirstLong = Long.parseLong(s2First, 16);
    Long s2LastLong = Long.parseLong(s2Last, 16);

    Long xorFirst = s1FirstLong ^ s2FirstLong;
    Long xorLast = s1LastLong ^ s2LastLong;

    String resultFirst = Long.toHexString(xorFirst);
//    resultFirst = checkLength(resultFirst, ( s1.length() > s2.length()) ?  s1 : s2);
    String resultLast = Long.toHexString(xorLast);
//    resultLast = checkLength(resultLast, ( s1.length() > s2.length()) ?  s1 : s2);

    String result = resultFirst + resultLast;

    return result;
  }

  public static String checkLength (String s1, String s2){
    if(s1.length() < s2.length()){
      int loop = s2.length() - s1.length();
      for(int i = 0; i < loop; i++){
        s1 = "0" + s1;
      }
    }
    return s1;
  }


  public String getPIN() {
    return PIN;
  }

  public void setPIN(String PIN) {
    this.PIN = PIN;
  }

  public String getPAN() {
    return PAN;
  }

  public void setPAN(String PAN) {
    this.PAN = PAN;
  }

}
