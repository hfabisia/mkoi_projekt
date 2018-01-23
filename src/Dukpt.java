import java.math.BigInteger;
import java.util.List;

public class Dukpt {

    /**
     * R8 - 8-byte register.
     */
    private String R8;
    /**
     * R8A - 8-byte register.
     */
    private String R8A;
    /**
     * R8B - 8-byte register
     */
    private String R8B;
    /**
     * R3 - 21-bit register.
     */
    private String R3;
    /**
     * 21-bit shift register.
     */
    private String SR;
    /**
     * 8-byte register; right-most 8 bytes of the Key Serial Number as received from the PIN Entry
     * Device.
     */
    private String KSNR;
    /**
     * 16-byte register; PIN encryption key initially loaded into the PIN Entry Device.
     */
    private String IKEY; //= "0A71CFB531452EDB46794A3BDF307ACB";
    /**
     * 16-byte register; at completion of the algorithm, it contains the PIN encryption key used in the
     * encryption of the PIN in the current transaction.
     */
    private String CURKEY;
    /**
     * 21-bit shift register
     */
    static int intSR;

    public Dukpt() {
        List<String> KSNs = ReadFile.readFile("KSN.txt");
        String KSN = KSNs.get(0);
        List<String> IPEKs = ReadFile.readFile("IPEK.txt");
        String IPEK = IPEKs.get(0);
        KSNR = KSN.substring(KSN.length() - 16, KSN.length());
        IKEY = IPEK.substring(IPEK.length() - 32, IPEK.length());
        //1) Copy IKEY into CURKEY.
        CURKEY = IKEY;
        //2) Copy KSNR into R8. 3) Clear the 21 right-most bits of R8.
        R8 = clearBits(hexToBin(KSNR), 21);
        //4) Copy the 21 right-most bits of KSNR into R3.
        R3 = hexToBin(KSNR).substring(hexToBin(KSNR).length() - 21, hexToBin(KSNR).length());
        //5) Set the left-most bit of SR, clearing the other 20 bits.
        setSR("100000000000000000000");
        intSR = Integer.parseInt(getSR(), 2);

    }

    /**
     * Metoda TAG1 wykonuje następujący algorytm:
     * 1) Is SR AND'ed with R3 = 0? If yes, go to "TAG2".
     * 2) "OR" SR into the 21 right-most bits of R8. (This sets the R8 bit corresponding to the SR bit that is set.)
     * 3) XOR the right half of CURKEY with R8 and store the result into R8A.
     * 4) DEA-encrypt R8A using the left half of CURKEY as the key and store the result into R8A.
     * 5) XOR R8A with the right half of CURKEY and store the result into R8A.
     * 6) XOR CURKEY with hexadecimal C0C0 C0C0 0000 0000 C0C0 C0C0 0000 0000.
     * 7) XOR the right half of CURKEY with R8 and store the result into R8B.
     * 8) DEA-encrypt R8B using the left half of CURKEY as the key and store the result into R8B.
     * 9) XOR R8B with the right half of CURKEY and store the result into R8B.
     * 10) Store R8A into the right half of CURKEY.
     *
     * @param dukpt
     * @return
     */
    public String TAG1(Dukpt dukpt) {
        //1) Is SR AND'ed with R3 = 0? If yes, go to "TAG2".
        int intR3 = Integer.parseInt(dukpt.getR3(), 2);

        if ((intSR & intR3) == 0) {
            TAG2(dukpt);
        } else {
            //2) "OR" SR into the 21 right-most bits of R8. (This sets the R8 bit corresponding to the SR bit that is set.)
            String rightMost21bitsR8 = dukpt.getR8().substring(dukpt.getR8().length() - 21, dukpt.getR8().length());
            Integer intRightMost21bitsR8 = Integer.parseInt(rightMost21bitsR8, 2);
            String resultOrSrAndRightMost21bitsR8 = Integer.toBinaryString((intSR | intRightMost21bitsR8));
            if (resultOrSrAndRightMost21bitsR8.length() < rightMost21bitsR8.length()) {
                int loop = rightMost21bitsR8.length() - resultOrSrAndRightMost21bitsR8.length();
                for (int i = 0; i < loop; i++) {
                    resultOrSrAndRightMost21bitsR8 = "0" + resultOrSrAndRightMost21bitsR8;
                }
            }
            R8 = dukpt.getR8().substring(0, dukpt.getR8().length() - 21) + resultOrSrAndRightMost21bitsR8;

            //3) XOR the right half of CURKEY with R8 and store the result into R8A.
            String R8Av1 = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), binToHex(R8));
            R8A = checkLength(R8Av1, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));

            //4) DEA-encrypt R8A using the left half of CURKEY as the key and store the result into R8A.
            String desKey = getCURKEY().substring(0, getCURKEY().length() / 2);//"1113456789abcdef";
            R8A = DES.finalOutput(R8A, desKey);

            //5) XOR R8A with the right half of CURKEY and store the result into R8A.
            String R8Av2 = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), R8A);
            R8A = checkLength(R8Av2, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));

            //6) XOR CURKEY with hexadecimal C0C0 C0C0 0000 0000 C0C0 C0C0 0000 0000.
            String curkeyLast = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), "C0C0C0C000000000");
            curkeyLast = checkLength(curkeyLast, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));
            String curkeyFirst = xorLongStrings(CURKEY.substring(0, CURKEY.length() / 2), "C0C0C0C000000000");
            curkeyFirst = checkLength(curkeyFirst, CURKEY.substring(0, CURKEY.length() / 2));
            CURKEY = curkeyFirst + curkeyLast;

            //7) XOR the right half of CURKEY with R8 and store the result into R8B.
            String R8Bv1 = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), binToHex(R8));
            R8B = checkLength(R8Bv1, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));
            //8) DEA-encrypt R8B using the left half of CURKEY as the key and store the result into R8B.
            String desKey1 = CURKEY.substring(0, CURKEY.length() / 2);
            R8B = DES.finalOutput(R8B, desKey1);
            //9) XOR R8B with the right half of CURKEY and store the result into R8B.
            String R8Bv2 = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), R8B);
            R8B = checkLength(R8Bv2, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));
            //10)Store R8A into the right half of CURKEY.
            // 11) Store R8B into the left half of CURKEY.
            CURKEY = R8B + R8A;
            System.out.println("Derived PEK: " + CURKEY);
            return CURKEY;
        }
        return null;
    }

    /**
     * Metoda TAG2 wykonuje następujący algorytm:
     * 1) Shift SR right one bit.
     * 2) If SR is not equal to zero (if the "one" bit has not been shifted off), go to "TAG1".
     * 3) XOR CURKEY with hexadecimal “0000 0000 0000 00FF 0000 0000 0000 00FF” and go to “Exit”.
     * (CURKEY now holds the PIN-encryption key that the security module will use to triple-DEA decrypt the
     * received encrypted PIN block.)
     *
     * @param dukpt
     * @return
     */
    public String TAG2(Dukpt dukpt) {

        //1) Shift SR right one bit.
        intSR >>= 1;

        //2) If SR is not equal to zero (if the "one" bit has not been shifted off), go to "TAG1".
        if (intSR != 0) {
            TAG1(dukpt);
        }

        //3) XOR CURKEY with hexadecimal “0000 0000 0000 00FF 0000 0000 0000 00FF” and go to “Exit”.
        //(CURKEY now holds the PIN-encryption key that the security module will use to triple-DEA decrypt the
        //received encrypted PIN block.)
        else {

            String curkeyLast = xorLongStrings(CURKEY.substring(CURKEY.length() / 2, CURKEY.length()), "00000000000000FF");
            curkeyLast = checkLength(curkeyLast, CURKEY.substring(CURKEY.length() / 2, CURKEY.length()));
            String curkeyFirst = xorLongStrings(CURKEY.substring(0, CURKEY.length() / 2), "00000000000000FF");
            curkeyFirst = checkLength(curkeyFirst, CURKEY.substring(0, CURKEY.length() / 2));
            CURKEY = curkeyFirst + curkeyLast;
            return CURKEY;
        }
        return null;
    }

    /**
     * Metoda dodaje do s1 z przodu Stringa "0", gdy jej długość jest mniejsza od s2
     *
     * @param s1
     * @param s2
     * @return
     */
    public String checkLength(String s1, String s2) {
        if (s1.length() < s2.length()) {
            int loop = s2.length() - s1.length();
            for (int i = 0; i < loop; i++) {
                s1 = "0" + s1;
            }
        }
        return s1;
    }

    /**
     * Metoda ustawia w Stringu wartość 0 od zadanego numeru indeksu (od lewej do prawej strony)
     *
     * @param s1  String na wejście
     * @param bit indeks
     * @return s1 String ustawionymi wartościami 0
     */
    public String clearBits(String s1, int bit) {
        String x, y, z;
        x = s1.substring(0, s1.length() - bit);
        y = s1.substring(s1.length() - bit, (s1.length()));
        z = y.replace('1', '0');
        s1 = x + z;
        return s1;
    }


    /**
     * Operacja logiczna xor pomiędzy s1 a s2
     *
     * @param s1
     * @param s2
     * @return
     */
    public String xorLongStrings(String s1, String s2) {
        int split = (s1.length() > s2.length()) ? s2.length() / 2 : s1.length() / 2;

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