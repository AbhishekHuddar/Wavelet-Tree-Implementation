import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaveletTree {
    private long startTime;
    private long endTime;
    private ArrayList<Character> dictionary;
    private String fullString;
    private Node rootNode;
    public WaveletTree(String fileName, char rankChar, int charPosition, int selectOccurence, char selectChar, int memberPosition) throws Exception {
            dictionary = new ArrayList<Character>();
            rootNode = new Node();
            startTime = System.nanoTime();
            readFile(fileName);
            endTime = System.nanoTime();
            System.out.println("Reading file took: " + getMiliseconds(endTime, startTime) + "ms");
            startTime = System.nanoTime();
            determineAlphabet();
            endTime = System.nanoTime();
            System.out.println("Determination of alphabet took: " + getMiliseconds(endTime, startTime) + "ms");
            startTime = System.nanoTime();
            generateWaveletTree(dictionary, fullString, rootNode);
            endTime = System.nanoTime();
            System.out.println("Generating tree took: " + getMiliseconds(endTime, startTime) + "ms");
            System.out.println("Memory used by program after generating is: " + getKb((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + "KB");
            System.out.println("\nTesting functions on tree:\n");
            System.out.println("\nRank:");
            startTime = System.nanoTime();
            System.out.println("Number of occurrences of character " + rankChar + " till position " + charPosition +
                    " is " + rank(rootNode, charPosition-1, rankChar, dictionary));
            endTime = System.nanoTime();
            System.out.println("Function RANK took: " + getMiliseconds(endTime, startTime) + "ms");
            System.out.println("\nSelect:");
            startTime = System.nanoTime();
            System.out.println("Position of " + selectOccurence + ". occurrence of character " +
                    selectChar + " is " + select(selectOccurence, selectChar));
            endTime = System.nanoTime();
            System.out.println("Function SELECT took: " + getMiliseconds(endTime, startTime) + "ms");
            System.out.println("\nMember:");
            startTime = System.nanoTime();
            System.out.println("Character at position " + memberPosition + " is " + member(memberPosition -1));
            endTime = System.nanoTime();
            System.out.println("Function MEMBER took: " + getMiliseconds(endTime, startTime) +  "ms");
            System.out.println("\n\n");
            Runtime.getRuntime().gc();
            System.out.println("Memory used by program after GC: " + getKb((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + "KB");

    }

    private void readFile(String filePath) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            StringBuilder comments = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            fullString = sb.toString();
            br.close();
        } catch (Exception e) {
            throw new Exception("Something went wrong while reading the file!");
        }
    }

    public void determineAlphabet() throws Exception {
        if (fullString.length() != 0) {
            for (char aChar : fullString.toCharArray()) {
                if (!dictionary.contains(Character.toUpperCase(aChar))) {
                    dictionary.add(Character.toUpperCase(aChar));
                }
            }
            Collections.sort(dictionary);
        } else {
            throw new Exception("Sequence's length can't be 0!");
        }
    }

    public void generateWaveletTree(List<Character> currentAlphabet, String currentLabel, Node currentNode) {
        if (currentAlphabet.size() > 2) {
            StringBuilder bitConfigurator = new StringBuilder();
            StringBuilder leftTree = new StringBuilder();
            StringBuilder rightTree = new StringBuilder();
            int middle = (currentAlphabet.size() + 1) / 2;
            for (char c : currentLabel.toCharArray()) {
                if (getIndex(Character.toUpperCase(c), currentAlphabet) < middle) {
                    bitConfigurator.append("0");
                    leftTree.append(Character.toUpperCase(c));
                } else {
                    bitConfigurator.append("1");
                    rightTree.append(Character.toUpperCase(c));
                }
            }
            currentNode.setBitmap(bitConfigurator.toString());
            currentNode.setLeftChild(new Node());
            currentNode.getLeftChild().setParent(currentNode);
            generateWaveletTree(currentAlphabet.subList(0, middle), leftTree.toString(), currentNode.getLeftChild());
            if (currentAlphabet.size() > 3) {
                currentNode.setRightChild(new Node());
                currentNode.getRightChild().setParent(currentNode);
                generateWaveletTree(currentAlphabet.subList(middle, currentAlphabet.size()),
                        rightTree.toString(), currentNode.getRightChild());
            }
        } else {
            if (currentAlphabet.size() == 2) {
                StringBuilder bitmapBuilder = new StringBuilder();
                for (char c : currentLabel.toCharArray()) {
                    if (getIndex(Character.toUpperCase(c), currentAlphabet) + 1 == 1) {
                        bitmapBuilder.append("0");
                    } else {
                        bitmapBuilder.append("1");
                    }
                }
                currentNode.setBitmap(bitmapBuilder.toString());
            }
            return;
        }
    }

    public int rank(Node currentNode, int index, char character, List<Character> currentAlphabet) {
        if (!currentAlphabet.contains(character)) {
            return 0;
        }

        int middle = (currentAlphabet.size() + 1) / 2;
        int newIndex;
        List<Character> currentAlphabetSliced = new ArrayList<Character>();
        if (getIndex(character, currentAlphabet) < middle) {
            newIndex = index - rank(currentNode.getBitmap(), index);
            currentNode = currentNode.getLeftChild();
            currentAlphabetSliced = currentAlphabet.subList(0, middle);
        } else {
            newIndex = rank(currentNode.getBitmap(), index) - 1;
            currentNode = currentNode.getRightChild();
            currentAlphabetSliced = currentAlphabet.subList(middle, currentAlphabet.size());
        }
        if (currentNode != null) {
            return rank(currentNode, newIndex, character, currentAlphabetSliced);
        } else {
            return newIndex + 1;
        }
    }

    public int select (int nthOccurrence, char character) throws Exception {
        Interval alphabeticInterval = new Interval(0, dictionary.size() - 1);
        Node currentNode = rootNode;
        int indexOfCharInAlph = getIndex(character, dictionary);
        boolean leftChild = true;
        while (alphabeticInterval.isGreaterThanTwo()) {
            if (alphabeticInterval.getSize() == 3) {
                if (alphabeticInterval.getRightIndex() == indexOfCharInAlph) {
                    leftChild = false;
                    break;
                }
            }
            if (indexOfCharInAlph <= alphabeticInterval.getMiddleIndex()) {
                currentNode = currentNode.getLeftChild();
                alphabeticInterval.setRightIndex();
            }
            else {
                currentNode = currentNode.getRightChild();
                alphabeticInterval.setLeftIndex();
            }
        }
        if (leftChild) {
            if (alphabeticInterval.getLeftIndex() == indexOfCharInAlph) {
                leftChild = true;
            }
            else {
                leftChild = false;
            }
        }
        int position = getPositionOfOccurrence(currentNode.getBitmap(), nthOccurrence, leftChild);
        if (position == 0) {
            throw new Exception("Character not found!");
        }
        Node child = currentNode;
        currentNode = currentNode.getParent();
        while(currentNode != null) {
            if (currentNode.getLeftChild().equals(child)) {
                position = getPositionOfOccurrence(currentNode.getBitmap(), position, true);
            } else {
                position = getPositionOfOccurrence(currentNode.getBitmap(), position, false);
            }
            currentNode = currentNode.getParent();
            child = child.getParent();
        }
        return position;
    }

    public char member (int index) throws Exception {
        Interval alphabeticInterval = new Interval (0, dictionary.size() - 1);
        if (index > rootNode.getBitmap().length() - 1) {
            throw new Exception("Index of range for function member");
        } else {
            Node currentNode = rootNode;
            int newIndex = index;
            while (alphabeticInterval.isGreaterThanTwo()) {
                if (currentNode != null) {
                    index = newIndex;
                    if (currentNode.getBitmap().charAt(index) == '1') {
                        newIndex = rank(currentNode.getBitmap(), index) - 1;
                        currentNode = currentNode.getRightChild();
                        alphabeticInterval.setLeftIndex();
                    } else {
                        newIndex = index - rank(currentNode.getBitmap(), index); // we're counting 0s!
                        currentNode = currentNode.getLeftChild();
                        alphabeticInterval.setRightIndex();
                    }
                } else {
                    break;
                }
            }
            if (currentNode != null)  {
                if (currentNode.getBitmap().charAt(newIndex) == '1') {
                    return dictionary.get(alphabeticInterval.getRightIndex());
                } else {
                    return dictionary.get(alphabeticInterval.getLeftIndex());
                }
            } else {
                return dictionary.get(alphabeticInterval.getLeftIndex());
            }
        }
    }

    public int getPositionOfOccurrence(String bitmap, int nthOcurrance, boolean leftChild) {
        int counter = 0;
        int position = 0;
        for (char c : bitmap.toCharArray())
        {
            if (counter < nthOcurrance)
            {
                position++;
                if (leftChild && c == '0') {
                    counter++;
                } else if (!leftChild && c=='1') {
                    counter++;
                }
            } else {
                break;
            }
        }
        if (counter == nthOcurrance) {
            return position;
        } else {
            return 0;
        }
    }

    public int getIndex(char c, List<Character> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (c == arrayList.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public int rank(String bitmap, int index) {
        int counter = 0;
        for (int i = 0; i < bitmap.length() && i <= index; i++)
        {
            if (bitmap.charAt(i) == '1')
            {
                counter++;
            }
        }
        return counter;
    }

    public double getMiliseconds (long endTime, long startTime) {
        return (double)(endTime - startTime) / 1000000;
    }

    public static long getKb(long bytes) {
        return bytes / (1024);
    }

    public static long getMb(long bytes) {
        return bytes / (1024 * 1024);
    }

    public class Interval {
        private int leftIndex;
        private int rightIndex;

        public Interval (int leftIndex, int rightIndex) {
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
        }

        public boolean isGreaterThanTwo() {
            if ((rightIndex - leftIndex) > 1) {
                return true;
            } else {
                return false;
            }
        }

        public int getMiddleIndex() {
            return (this.leftIndex + this.rightIndex) / 2;
        }

        public int getSize() {
            return rightIndex - leftIndex + 1;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        public int getRightIndex() {
            return rightIndex;
        }

        public void setLeftIndex() {
            this.leftIndex = rightIndex - (getSize() / 2 - 1);
        }

        public void setRightIndex() {
            this.rightIndex = leftIndex + ((getSize() + 1) / 2 - 1);
        }
    }

    public class Node {
        private String bitmap;

        private Node parent;
        private Node leftChild;
        private Node rightChild;

        public String getBitmap() {
            return bitmap;
        }

        public void setBitmap (String bitmap) {
            this.bitmap = bitmap;
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public void setLeftChild(Node child) {
            this.leftChild = child;
        }

        public void setRightChild(Node child) {
            this.rightChild = child;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return this.parent;
        }
    }

}
