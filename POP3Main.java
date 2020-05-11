package KompiuteriuTinklai_2;

import java.io.*;
import java.util.*;

public class POP3Main {

   public static void main(String[] argc) {
        POP3Client client = new POP3Client();
        GUI myGUI = new GUI(client);
        myGUI.setVisible(true);
    }
}

