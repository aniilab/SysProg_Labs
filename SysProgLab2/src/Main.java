import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Automaton auto = Reading();

        UnreachableStates(auto);

        DeadStates(auto);
    }

    public static Automaton Reading(){
        Scanner scanner = new Scanner(System.in);
        System.out.format("Enter the filepath:");
        String pathname = scanner.next();
        Automaton res = null;
        try {
            res = new Automaton(pathname);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void UnreachableStates(Automaton auto)
    {
        Set<Integer> notReachableStates = auto.getNotReachable();
        System.out.println("Недосяжні стани:");
        for (Integer t : notReachableStates) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

    public static void DeadStates(Automaton auto)
    {
        Set<Integer> deadStates = auto.getDead();
        System.out.println("Тупикові стани:");
        for (Integer t : deadStates) {
            System.out.print(t + " ");
        }
        System.out.println();
    }
    static Scanner getScanner(String pathname) throws FileNotFoundException {
        File file = new File(pathname);

        if (!file.exists()) {
            System.out.format("File '%s' does not exist.%n", pathname);
        }

        if (!file.canRead()) {
            System.out.format("Cannot read file '%s'.%n", pathname);
        }

        return new Scanner(file);
    }
}