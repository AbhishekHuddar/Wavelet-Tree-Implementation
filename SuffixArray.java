import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SuffixArray {
    static int counter = 0;
    String suffixArrayString;
    int suffixArrayOrigin;
    public SuffixArray(String fileName) {
        //String fileName = "./src/main/resources/Test.txt";

        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> lines = new ArrayList<>();
            ArrayList<SuffixString> suffixLines = new ArrayList<>();

            String line = br.readLine();
            while (line != null) {
                line = line.replaceAll("\\s+", " ").trim();
                if (line.length() > 0) {
                    lines.add(line);
                }
                line = br.readLine();
            }
            line = "";
            for (String s : lines) {
                line += s + " ";
            }
            line.trim();
            lines.clear();
            line += "$";
            int[] suffixes = new int[line.length()];
            for (int a = 0, b = 0; a < line.length(); a++) {
                suffixLines.add(new SuffixString(line.substring(a) + "" + line.substring(0, b++)));
            }

            Collections.sort(suffixLines);
            int orig = -1, roller = -1;
            String stringVal = "";
            for (SuffixString s : suffixLines) {
                stringVal += s.str.charAt(s.str.length() - 1);
                roller++;
                if (s.index == 0) {
                    orig = roller;
                }
            }
            this.suffixArrayOrigin = orig;
            this.suffixArrayString = stringVal;
            System.out.println("Position: " + orig + "\nOutput: " + stringVal);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static class SuffixString implements Comparable<SuffixString> {
        String str = "";
        int index;

        public SuffixString(String str) {
            this.str = str;
            index = counter++;
        }
        @Override
        public int compareTo(SuffixString o) {
            return this.str.compareTo(((SuffixString)o).str);
        }
    }
}
