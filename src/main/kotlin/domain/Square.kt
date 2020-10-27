package domain

import Constants.VALID_LETTERS
import java.util.*

data class Square(
    val tile: Tile? = null,
    val crossChecks: BitSet = BitSet(26),
    val isAnchor: Boolean = false,
    val crossSum: Int? = null,
    val letterMultiplier: Int = 1,
    val wordMultiplier: Int = 1
) {
    fun isOccupied(): Boolean {
        return tile != null
    }

    fun getLetter(): Char? {
        return tile?.letter
    }

    fun crossChecksContains(letter: Char): Boolean {
        return crossChecks[VALID_LETTERS.indexOf(letter)]
    }
}
