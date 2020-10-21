import constants.ScoreConstants
import constants.ScoreConstants.validLetters
import wordfeudapi.domain.Tile
import java.util.*

//val board: Array<Array<Square>> = Array(15)

//lag egen Tile
data class Square(
    val tile: Tile? = null,
    val crossChecks: BitSet = BitSet(26),
    val crossSum: Int = 0

) {
    fun isOccupied(): Boolean {
        return tile != null
    }

    //TODO burde denne returnere stor bokstav også for blank?
    fun getLetter(): Char? {
        return tile?.character
    }
}

data class Row(
    val squares: List<Square>
) {
    fun getPrefix(startIndex: Int): String {
        val builder = StringBuilder()
        var index = startIndex
        while (--index in squares.indices && squares[index].isOccupied()) {
            builder.append(squares[index].getLetter()!!)
        }
        return builder.reverse().toString()
    }

    fun getSuffix(startIndex: Int): String {
        val builder = StringBuilder()
        var index = startIndex
        while (++index in squares.indices && squares[index].isOccupied()) {
            builder.append(squares[index].getLetter()!!)
        }
        return builder.toString()
    }
}


fun validCrossCheckLetters(prefix: String, suffix: String): BitSet {
    val bitset = BitSet(26)
    validLetters().forEachIndexed { index, char ->
        bitset.set(index,
            Dictionary.getDictionary().contains(prefix + char + suffix)
        )
    }
    return bitset
}

//KUN CROSSCHEKS
/*fun crossChecksForRow(row: Row): List<Square> {
    return row.squares.mapIndexed { index, square ->
        var bitSet = BitSet(26)
        if (!square.isOccupied()) {
            val prefix = row.getPrefix(index)
            val suffix = row.getSuffix(index)

            if (prefix.isEmpty() && suffix.isEmpty()) {
                bitSet.flip(0, 26)
            } else {
                bitSet = validCrossCheckLetters(prefix, suffix)
            }
        }
        square.copy(crossChecks = bitSet)
    }
}*/

//CROSSCHECKS + CROSSUM
fun crossChecksForRow(row: Row): List<Square> {
    return row.squares.mapIndexed { index, square ->
        var bitSet = BitSet(26)
        var crossSum = 0
        if (!square.isOccupied()) {
            val prefix = row.getPrefix(index)
            val suffix = row.getSuffix(index)

            if (prefix.isEmpty() && suffix.isEmpty()) {
                bitSet.flip(0, 26)
            } else {
                bitSet = validCrossCheckLetters(prefix, suffix)
                crossSum = (prefix + suffix).map(ScoreConstants::letterScore).sum()
            }
        }
        square.copy(crossChecks = bitSet, crossSum = crossSum)
    }
}
