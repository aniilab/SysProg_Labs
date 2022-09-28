import java.io.FileNotFoundException;
import java.util.*;

public class Automaton {

    Set<Character> A;
    Set<Integer> S;
    Integer s0;
    Set<Integer> F;
    Map<Integer, Map<Character, Set<Integer>>> transitionFunction;

    Automaton() {
        A = new HashSet<>();
        S = new HashSet<>();
        s0 = 0;
        F = new HashSet<>();
        transitionFunction = new HashMap<>();
    }

    private Automaton(Scanner fileScanner) {
        //заповнюємо вхідний алфавіт
        String possibleA = "abcdefghijklmnopqrstuvwxyz";
        int aSize = fileScanner.nextInt();
        A = new HashSet<>();
        for (int i = 0; i < aSize; ++i) {
            A.add(possibleA.charAt(i));
        }

        //заповнюємо стани
        int numberOfStates = fileScanner.nextInt();
        S = new HashSet<>(numberOfStates);
        for (int i = 0; i < numberOfStates; ++i) {
            S.add(i);
        }
        s0 = fileScanner.nextInt();

        //запонвюємо заключні стани
        int numberOfFinals = fileScanner.nextInt();
        F = new HashSet<>(numberOfFinals);
        for (int i = 0; i < numberOfFinals; ++i) {
            F.add(fileScanner.nextInt());
        }

        //заповнюємо функції переходів
        transitionFunction = new HashMap<>(numberOfStates);
        for (Integer state : S) {
            transitionFunction.put(state, new HashMap<>());
        }

        while (fileScanner.hasNext()) {
            Integer from = fileScanner.nextInt();
            Character letter = fileScanner.next().charAt(0);
            Integer to = fileScanner.nextInt();
            if (!transitionFunction.get(from).keySet().contains(letter)) {
                transitionFunction.get(from).put(letter, new HashSet<>());
            }
            transitionFunction.get(from).get(letter).add(to);
        }
    }

    Automaton(String pathname) throws FileNotFoundException {
        this(Main.getScanner(pathname));
    }

    Automaton Inverse() {
        Automaton auto = new Automaton();
        auto.A = new HashSet<>(A);
        auto.S = new HashSet<>(S);
        auto.s0 = s0;
        auto.F = new HashSet<>(F);

        //робимо функцію переходів навпаки: куди-літера-звідки
        auto.transitionFunction = new HashMap<>();
        for (Integer fromState : S) {
            auto.transitionFunction.put(fromState, new HashMap<>());
        }
        for (Integer from : S) {
            for (Character letter : transitionFunction.get(from).keySet()) {
                for (Integer to : transitionFunction.get(from).get(letter)) {
                    if (!auto.transitionFunction.get(to).keySet().contains(letter)) {
                        auto.transitionFunction.get(to).put(letter, new HashSet<>());
                    }
                    auto.transitionFunction.get(to).get(letter).add(from);
                }
            }
        }
        return auto;
    }

    private Map<Integer, Boolean> analyzeStates(Set<Integer> from) {
        Map<Integer, Boolean> used = new HashMap<>();
        for (Integer state : S) {
            used.put(state, false);
        }

        Queue<Integer> states = new LinkedList<>(from);
        while (!states.isEmpty()) {
            Integer from_ = states.peek();
            used.put(from_, true);

            for (Character letter : transitionFunction.get(from_).keySet()) {
                for (Integer to : transitionFunction.get(from_).get(letter)) {
                    if (!used.get(to)) {
                        states.add(to);
                    }
                }
            }

            states.poll();
        }

        return used;
    }

    private Set<Integer> getReachableFromState(Integer fromState) {
        Set<Integer> fromStates = new HashSet<>();
        fromStates.add(fromState);
        return getReachableFromStates(fromStates);
    }

    Set<Integer> getReachableFromStates(Set<Integer> fromStates) {
        Map<Integer, Boolean> used = analyzeStates(fromStates);
        Set<Integer> reachable = new HashSet<>();
        for (Integer state : used.keySet()) {
            if (used.get(state)) {
                reachable.add(state);
            }
        }
        return reachable;
    }


    Set<Integer> getNotReachable() {
        Set<Integer> states_ = new HashSet<>(S);
        states_.removeAll(getReachableFromState(s0));
        return states_;
    }

    private Set<Integer> getNotDead(Set<Integer> forStates) {
        Automaton inverse_auto = Inverse();
        return inverse_auto.getReachableFromStates(forStates);
    }

    Set<Integer> getDead() {
        Set<Integer> states_ = new HashSet<>(S);
        states_.removeAll(getNotDead(F));
        return states_;
    }
}