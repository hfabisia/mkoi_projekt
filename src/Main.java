import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws Exception {

    Dukpt dukpt = new Dukpt();

//    String IKEY = "0A71CFB531452EDB46794A3BDF307ACB";
//    Integer foo = Integer.parseInt("1001",2);
//    PinBlocks pinBlocks = new PinBlocks();

//    System.out.println(Integer.toBinaryString(foo));
//    System.out.println(0b01101&0b11001);
//    System.out.println(dukpt.getR8());
//    System.out.println(dukpt.getCURKEY());
//    System.out.println(dukpt.getR3());
//    System.out.println(dukpt.getSR());
//    System.out.println("Witaj w programie DUKPT");
//    System.out.println("-----------------------");
//    System.out.println("Podaj nr karty: ");
//    String PAN;
//    Scanner odczyt = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
//    PAN = odczyt.nextLine();
//    System.out.println("Podany nr karty: "+PAN);
//    System.out.println("-----------------------");
//    System.out.println("Podaj nr PIN: ");
//    String PIN;
//    Scanner odczyt1 = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
//    PIN = odczyt1.nextLine();
//    System.out.println("Podany nr PIN: "+PIN);
//
//    System.out.println("Clear PIN block:");
//    System.out.println(PinBlocks.PinBlockEncrypt(PIN, PAN));
    System.out.println(dukpt.TAG1(dukpt));
    System.out.println(dukpt.TAG2(dukpt));
    String a = DES.finalOutput("2236ff2d27b3ef0d", "C1D0F8FB4958670D");
//    System.out.println(Arrays.toString(DES.finalOutput("2236ff2d27b3ef0d", "C1D0F8FB4958670D")  ));
    System.out.println("aaaaaaaaaa "+ a);
  }
}
