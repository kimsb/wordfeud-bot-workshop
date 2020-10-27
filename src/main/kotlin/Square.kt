import constants.ScoreConstants
import java.util.*

data class Square(
    val tile: Tile? = null,
    val crossChecks: BitSet = BitSet(26),
    val isAnchor: Boolean = false,
    val crossSum: Int = 0,
    val letterMultiplier: Int = 1,
    val wordMultiplier: Int = 1,
    val nextSquare: Square? = null

) {
    fun isOccupied(): Boolean {
        return tile != null
    }

    //TODO burde denne returnere stor bokstav også for blank?
    fun getLetter(): Char? {
        return tile?.letter?.toUpperCase()
    }

    fun crossChecksContains(letter: Char): Boolean {
        return crossChecks[ScoreConstants.validLetters().indexOf(letter)]
    }
}
