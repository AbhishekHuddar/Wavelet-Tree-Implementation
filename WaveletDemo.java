/*
    Usage:
        agrs[0]: File name                              -- String
        agrs[1]: Character for Rank                     -- char
        agrs[2]: Position for Rank                      -- int
        agrs[3]: Occurence of Character for Select      -- int
        agrs[4]: Character for Select                   -- char
        agrs[5]: Position for Member                    -- int
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class WaveletDemo {
    static String fileName;
    static char rankChar;
    static int charPosition;
    static int selectOccurence;
    static char selectChar;
    static int memberPosition;

    public static void main(String[] args) {
        if (args.length == 6) {
            fileName = args[0];
            rankChar = Character.toUpperCase(args[1].charAt(0));;
            charPosition = Integer.parseInt(args[2]);
            selectOccurence = Integer.parseInt(args[3]);
            selectChar = Character.toUpperCase(args[4].charAt(0));
            memberPosition = Integer.parseInt(args[5]);
            SuffixArray suffixArray = new SuffixArray(args[0]);
        } else {
            String[] tempArgs = {"./Test.txt", "m", "40", "1", "m", "0"};
            fileName = tempArgs[0];
            rankChar = Character.toUpperCase(tempArgs[1].charAt(0));;
            charPosition = Integer.parseInt(tempArgs[2]);
            selectOccurence = Integer.parseInt(tempArgs[3]);
            selectChar = Character.toUpperCase(tempArgs[4].charAt(0));
            memberPosition = Integer.parseInt(tempArgs[5]);
            SuffixArray suffixArray = new SuffixArray(tempArgs[0]);
        }

        Runtime.getRuntime().gc();

        System.out.println("Total memory: " + WaveletTree.getMb(Runtime.getRuntime().totalMemory()) + "MB\n");


        System.out.println("\nStarting program:");
        System.out.println("-------------");

        try {
            new WaveletTree(fileName, rankChar, charPosition, selectOccurence, selectChar, memberPosition);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
