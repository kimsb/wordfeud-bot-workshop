import mdag.MDAG;
import mdag.MDAGNode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

class Dictionary {

    private static MDAG dictionary;

    static void initialize() {
        Dictionary dictionary = new Dictionary();
        ClassLoader classLoader = dictionary.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("nsf2020.txt");
        List<String> words = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.toList());
        Dictionary.dictionary = (new MDAG(words));
    }

    static MDAG getDictionary() {
        return dictionary;
    }

    static MDAGNode getSourceNode() {
        return (MDAGNode) dictionary.getSourceNode();
    }
}
