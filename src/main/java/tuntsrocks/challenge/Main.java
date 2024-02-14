package tuntsrocks.challenge;


import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        SheetService s = new SheetService();
        s.readSheet();

    }

}