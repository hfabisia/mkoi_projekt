import java.math.BigInteger;
import java.util.List;

public class Dukpt {

  private String R8;
  private String R8A;
  private String R8B;
  private String R3;
  private String SR;
  private String KSNR;
  private String IKEY; //= "0A71CFB531452EDB46794A3BDF307ACB";
  private String CURKEY;

  public Dukpt(){
    List<String> KSNs = ReadFile.readFile("KSN.txt");
    String KSN = KSNs.get(0);
    List<String> IPEKs = ReadFile.readFile("IPEK.txt");
    String IPEK = IPEKs.get(0);
    KSNR = KSN.substring(KSN.length()-16,KSN.length());
    IKEY = IPEK.substring(IPEK.length()-32,IPEK.length());
    CURKEY = IKEY;
    R8 = hexToBin(KSNR).substring(0, hexToBin(KSNR).length() - 21);
    R3 = hexToBin(KSNR).substring(hexToBin(KSNR).length() - 21, hexToBin(KSNR).length());
    SR = R3.substring(0, R3.length() - 20);
  }


  public String TAG1(Dukpt dukpt){
    //1
    int intSR = Integer.parseInt(dukpt.getSR(), 2);
    int intR3 = Integer.parseInt(dukpt.getR3(), 2);
    if((intSR & intR3) == 0){
      TAG2(dukpt);
    }
    else{
    //2
      String rightMost21bitsR8 = dukpt.getR8().substring(dukpt.getR8().length() - 21, dukpt.getR8().length());
      Integer intRightMost21bitsR8 = Integer.parseInt(rightMost21bitsR8, 2);
      String resultOrSrAndRightMost21bitsR8 = Integer.toBinaryString((intSR | intRightMost21bitsR8));
      if(resultOrSrAndRightMost21bitsR8.length() < rightMost21bitsR8.length()){
        int loop = rightMost21bitsR8.length() - resultOrSrAndRightMost21bitsR8.length();
        for(int i = 0; i < loop; i++){
          resultOrSrAndRightMost21bitsR8 = "0" + resultOrSrAndRightMost21bitsR8;
        }
      }
      R8 = dukpt.getR8().substring(0, dukpt.getR8().length() - 21) + resultOrSrAndRightMost21bitsR8;

    //3
      String R8Av1 = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), binToHex(R8));
      R8A = checkLength(R8Av1, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
    //4
    //?????????????
    //5
      String R8Av2 = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), R8A);
      R8A = checkLength(R8Av2, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
    //6
      String curkeyLast = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), "C0C0C0C000000000");
      curkeyLast = checkLength(curkeyLast, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
      String curkeyFirst = xorLongStrings(CURKEY.substring(0, CURKEY.length()/2), "C0C0C0C000000000");
      curkeyFirst = checkLength(curkeyFirst, CURKEY.substring(0, CURKEY.length()/2));
      CURKEY = curkeyFirst + curkeyLast;
    //7
      String R8Bv1 = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), binToHex(R8));
      R8B = checkLength(R8Bv1, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
    //8
    //???????????
    //9
      String R8Bv2 = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), R8A);
      R8B = checkLength(R8Bv2, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
    //10, 11
      CURKEY = R8B + R8A;
      return CURKEY;
    }
    return null;
  }

  public String TAG2(Dukpt dukpt){
    //1
    int intSR = Integer.parseInt(SR, 2);
    intSR >>= 1;
    //2
    if(intSR != 0){
      TAG1(dukpt);
    }
    else {
      //3
      String curkeyLast = xorLongStrings(CURKEY.substring(CURKEY.length()/2, CURKEY.length()), "00000000000000FF");
      curkeyLast = checkLength(curkeyLast, CURKEY.substring(CURKEY.length()/2, CURKEY.length()));
      String curkeyFirst = xorLongStrings(CURKEY.substring(0, CURKEY.length()/2), "00000000000000FF");
      curkeyFirst = checkLength(curkeyFirst, CURKEY.substring(0, CURKEY.length()/2));
      CURKEY = curkeyFirst + curkeyLast;
      return CURKEY;
    }
    return null;
  }

  public String checkLength (String s1, String s2){
    if(s1.length() < s2.length()){
      int loop = s2.length() - s1.length();
      for(int i = 0; i < loop; i++){
        s1 = "0" + s1;
      }
    }
    return s1;
  }


  public String xorLongStrings(String s1, String s2){
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

  static String hexToBin(String s) {
    return new BigInteger(s, 16).toString(2);
  }

  static String binToHex(String s) {
    return new BigInteger(s, 2).toString(16);
  }



  public String getR8() {
    return R8;
  }

  public String getR8A() {
    return R8A;
  }

  public String getR8B() {
    return R8B;
  }

  public String getR3() {
    return R3;
  }

  public String getSR() {
    return SR;
  }

  public String getKSNR() {
    return KSNR;
  }

  public String getIKEY() {
    return IKEY;
  }

  public String getCURKEY() {
    return CURKEY;
  }


  public void setR8(String r8) {
    R8 = r8;
  }

  public void setR8A(String r8A) {
    R8A = r8A;
  }

  public void setR8B(String r8B) {
    R8B = r8B;
  }

  public void setR3(String r3) {
    R3 = r3;
  }

  public void setSR(String SR) {
    this.SR = SR;
  }

  public void setKSNR(String KSNR) {
    this.KSNR = KSNR;
  }

  public void setIKEY(String IKEY) {
    this.IKEY = IKEY;
  }

  public void setCURKEY(String CURKEY) {
    this.CURKEY = CURKEY;
  }

}
