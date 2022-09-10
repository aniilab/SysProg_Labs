import java.io.IOException;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;

class Main {

    public static void main(String... args) {
        Scanner in = new Scanner(System.in);

        String inputFilePath;
        String outputFilePath = "result.txt";

        Scanner inputFile = null;
        Formatter outputFile = null;


        Set<String> words = null;

        try {
            System.out.println("Введіть шлях до текстового файлу: ");
            inputFilePath = in.nextLine();
            in.close();

            inputFile = new Scanner(Paths.get(inputFilePath));
            outputFile = new Formatter(outputFilePath);
            Analyzer analyzer = new Analyzer(inputFile);

            Map<String, Integer> result = analyzer.AnalyzeWordsFrequency();


            words = result.keySet();

            String format = "%-" + 30 + "s\t%s%n";
            System.out.printf(format, "Word", "Frequency");
            outputFile.format(format, "Word", "Frequency");
            for(String word : words) {
                System.out.printf(format, word, result.get(word));
                outputFile.format(format, word, result.get(word));
            }
        }

        catch(IOException | NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

        finally {
            assert inputFile != null;
            assert outputFile != null;
            inputFile.close();
            outputFile.close();
        }
    }
}