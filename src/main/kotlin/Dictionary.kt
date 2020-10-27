import constants.ScoreConstants
import mdag.MDAG
import mdag.MDAGNode

object Dictionary {

    private val instance: MDAG

    init {
        val words = Main::class.java.getResourceAsStream("nsf2020.txt")
            .bufferedReader()
            .readLines()
            .filter(ScoreConstants::wordContainsValidLetters)
        instance = MDAG(words)
    }

    fun contains(word: String): Boolean {
        return instance.contains(word)
    }

    fun getSourceNode(): MDAGNode {
        return instance.sourceNode as MDAGNode
    }
}
