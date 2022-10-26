import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter the filepath:");
        String filename = in.nextLine();

        Path path = Paths.get(filename);
        File file = path.toFile();

        try
        {
            LexAnalyzer lexer = new LexAnalyzer(file);
            lexer.showResult();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
        }
    }
}