import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Analyzer {
    Scanner inputFile = null;


    public Analyzer(Scanner inputF) {
        inputFile = inputF;
    }

    public Map<String, Integer> AnalyzeWordsFrequency() {
        String currentLine;
        Map<String, Integer> wordFreq = new HashMap<>();

        while (inputFile.hasNext()) {
            currentLine = inputFile.nextLine();
            if (currentLine.isEmpty()) {
                currentLine = " ";
            }
            currentLine = currentLine.replaceAll("[^A-Za-zА-Яа-я\\і\\І\\s\\Ї\\ї\\Є\\є\\']", " ");
            String[] words = currentLine.split("\\s+");

            for (String word : words) {
                word = word.toLowerCase();

                    if(word.length()>30){
                        word=word.substring(0,29);
                    }


                if (wordFreq.containsKey(word)) {
                    int count = wordFreq.get(word);
                    wordFreq.put(word, count + 1);
                } else {
                    wordFreq.put(word, 1);
                }
            }
        }
        return wordFreq;
    }
}



