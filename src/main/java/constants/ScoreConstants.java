package constants;

import java.util.HashMap;

public class ScoreConstants {

    private static final HashMap<Character, Integer> letterScores;
    static {
        letterScores = new HashMap<>();
        letterScores.put('A', 1);
        letterScores.put('B', 4);
        letterScores.put('C', 10);
        letterScores.put('D', 1);
        letterScores.put('E', 1);
        letterScores.put('F', 2);
        letterScores.put('G', 4);
        letterScores.put('H', 3);
        letterScores.put('I', 2);
        letterScores.put('J', 4);
        letterScores.put('K', 3);
        letterScores.put('L', 2);
        letterScores.put('M', 2);
        letterScores.put('N', 1);
        letterScores.put('O', 3);
        letterScores.put('P', 4);
        letterScores.put('R', 1);
        letterScores.put('S', 1);
        letterScores.put('T', 1);
        letterScores.put('U', 4);
        letterScores.put('V', 5);
        letterScores.put('W', 10);
        letterScores.put('Y', 8);
        letterScores.put('Æ', 8);
        letterScores.put('Ø', 4);
        letterScores.put('Å', 4);
        letterScores.put('*', 0);
    }

    public static int letterScore(char letter) {
        Integer letterScore = letterScores.get(letter);
        return letterScore == null ? 0 : letterScore;
    }

    public static String validLetters() {
        return "ABCDEFGHIJKLMNOPRSTUVWYÆØÅ";
    }

    public static boolean wordContainsValidLetters(String word) {
        String validLetters = validLetters();
        for (int i = 0; i < word.length(); i++) {
            if (validLetters.indexOf(word.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }
}
