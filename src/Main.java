import java.util.List;

public class Main {

  public static void main(String[] args) {

    Dukpt dukpt = new Dukpt();

//    String IKEY = "0A71CFB531452EDB46794A3BDF307ACB";
//    Integer foo = Integer.parseInt("1001",2);


//    System.out.println(Integer.toBinaryString(foo));
//    System.out.println(0b01101&0b11001);
//    System.out.println(dukpt.getR8());
//    System.out.println(dukpt.getCURKEY());
//    System.out.println(dukpt.getR3());
//    System.out.println(dukpt.getSR());
    System.out.println(dukpt.TAG1(dukpt));
    System.out.println(dukpt.TAG2(dukpt));
  }
}
