import Constants.VALID_LETTERS
import mdag.MDAG
import mdag.MDAGNode

object Dictionary {

    private val instance: MDAG

    init {
        val words = Main::class.java.getResourceAsStream("nsf2020.txt")
            .bufferedReader()
            .readLines()
            .filter(this::wordContainsValidLetters)
        instance = MDAG(words)
    }

    fun contains(word: String): Boolean {
        return instance.contains(word)
    }

    fun getSourceNode(): MDAGNode {
        return instance.sourceNode as MDAGNode
    }

    private fun wordContainsValidLetters(word: String): Boolean {
        word.forEach {
            if (!VALID_LETTERS.contains(it)) {
                return false
            }
        }
        return true
    }
}
