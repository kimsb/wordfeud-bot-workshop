import constants.ScoreConstants.validLetters
import java.util.*

fun validCrossCheckLetters(prefix: String, suffix: String): BitSet {
    val bitset = BitSet(26)
    validLetters().forEachIndexed { index, char ->
        bitset.set(index,
            Dictionary.getDictionary().contains(prefix + char + suffix)
        )
    }
    return bitset
}


