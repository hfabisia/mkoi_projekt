import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        Dukpt dukpt = new Dukpt();
        PinBlocks pinBlocks = new PinBlocks();
        System.out.println("Witaj w programie DUKPT");
        System.out.println("-----------------------");
        System.out.println("Podaj nr karty: ");
        String PAN;
        Scanner odczyt = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
        PAN = odczyt.nextLine();
        System.out.println("Podany nr karty: " + PAN);
        System.out.println("-----------------------");
        System.out.println("Podaj nr PIN: ");
        String PIN;
        Scanner odczyt1 = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
        PIN = odczyt1.nextLine();
        System.out.println("Podany nr PIN: " + PIN);

        System.out.println("Clear PIN block:" + PinBlocks.PinBlockEncrypt(PIN, PAN));
        System.out.println("-----------------------");
        dukpt.TAG1(dukpt);
        dukpt.TAG2(dukpt);
    }
}
