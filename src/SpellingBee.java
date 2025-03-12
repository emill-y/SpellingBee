import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, Eisha Yadav
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 * Edited on March 12, 2025 by Eisha Yadav @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<String>();
    }

    public void generate() {
        generate("", letters,new boolean[letters.length()]);
    }

    public void generate(String substring, String leftover, boolean[] used){
        if (!substring.isEmpty() && !words.contains(substring)) {
            words.add(substring);
        }
        for (int i = 0; i < leftover.length(); i++) {
            // Skip already used characters
            if (used[i]) continue;
            used[i] = true;
            generate(substring + leftover.charAt(i), leftover, used);
            used[i] = false;
        }
    }

    public void sort() {
        words = mergeSort(words, 0, words.size() - 1);
    }

    private ArrayList<String> mergeSort(ArrayList<String> arr, int low, int high) {

        if (high <= low) {
            ArrayList<String> newArr = new ArrayList<>();
            newArr.add(words.get(low));
            return newArr;
        }

        int med = (high + low) / 2;
        ArrayList<String> arr1 = mergeSort(arr, low, med);
        ArrayList<String> arr2 = mergeSort(arr, med + 1, high);
        return merge(arr1, arr2);

    }

    public ArrayList<String> merge(ArrayList<String> arr1, ArrayList<String> arr2) {
        ArrayList<String> merged = new ArrayList<>();
        int a = 0, b = 0;
        while (a < arr1.size() && b < arr2.size()) {
            if (arr1.get(a).compareTo(arr2.get(b)) > 0) {
                merged.add(arr1.get(a));
                a++;
            }
            else {
                merged.add(arr2.get(b++));

            }
        }
        while (a < arr1.size()) {
            merged.add(arr1.get(a++));
        }
        while (b < arr2.size()) {
            merged.add(arr2.get(b++));
        }

        return merged;
    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }

    public void checkWords() {
        // Remove word under the condition that it is not within the dictionary.
        // Method Learnt from Oracle Library for Java
        words.removeIf(word -> !binarySearch(word));
    }

    // Iterative Implementation of Binary Search (Not Recursive)
    public boolean binarySearch(String target){
        int left = 0;
        int right = DICTIONARY_SIZE - 1;
        while (left <= right){
            int mid = left + (right - left) / 2;
            int comparison = DICTIONARY[mid].compareTo(target);
            if (comparison == 0) return true;
            if (comparison < 0) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }

    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {
        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
