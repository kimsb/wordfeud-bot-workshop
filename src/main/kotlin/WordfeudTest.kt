import Dictionary.getSourceNode
import constants.ScoreConstants
import constants.ScoreConstants.validLetters
import mdag.MDAGNode
import java.util.*

//val board: Array<Array<Square>> = Array(15)



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

    //TODO flytte denne til square (må initialiseres for hvert board)
    /*fun isAnchor(index: Int): Boolean {
        return squares[index].crossAnchor ||
            !squares[index].isOccupied() && isAdjacentToOccupiedSquare(index)
    }

    private fun isAdjacentToOccupiedSquare(index: Int): Boolean {
        return index - 1 in squares.indices && squares[index - 1].isOccupied() ||
            return index + 1 in squares.indices && squares[index + 1].isOccupied()
    }*/
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

//CROSSCHECKS + CROSSANCHOR
/*fun crossChecksForRow(row: Row): List<Square> {
    return row.squares.mapIndexed { index, square ->
        var bitSet = BitSet(26)
        var crossAnchor = false
        if (!square.isOccupied()) {
            val prefix = row.getPrefix(index)
            val suffix = row.getSuffix(index)

            if (prefix.isEmpty() && suffix.isEmpty()) {
                bitSet.flip(0, 26)
            } else {
                bitSet = validCrossCheckLetters(prefix, suffix)
                crossAnchor = true
            }
        }
        square.copy(crossChecks = bitSet,
            crossAnchor = crossAnchor)
    }
}*/

//CROSSCHECKS + CROSSANCHOR + CROSSUM
fun crossChecksForRow(row: Row): List<Square> {
    return row.squares.mapIndexed { index, square ->
        var bitSet = BitSet(26)
        var crossAnchor = false
        var crossSum = 0
        if (!square.isOccupied()) {
            val prefix = row.getPrefix(index)
            val suffix = row.getSuffix(index)

            if (prefix.isEmpty() && suffix.isEmpty()) {
                bitSet.flip(0, 26)
            } else {
                bitSet = validCrossCheckLetters(prefix, suffix)
                crossAnchor = true
                crossSum = (prefix + suffix).map(ScoreConstants::letterScore).sum()
            }
        }
        square.copy(crossChecks = bitSet,
            crossAnchor = crossAnchor,
            crossSum = crossSum)
    }
}

// "-----BJØRN-BÆR-"
fun findAcrossMoves(row: Row) {
    var limit = 0
    row.squares.forEachIndexed { index, square ->
        if (row.isAnchor(index)) {
            val prefix = row.getPrefix(index)
            if (prefix.isNotEmpty()) {
                extendRight(prefix, getSourceNode().transition(prefix), square)
                limit = 0
            } else {
                leftPart(prefix, getSourceNode(), limit++)
            }
        }
    }
}

fun leftPart(partialWord: String, mdagNode: MDAGNode, limit: Int) {

}

fun extendRight(partialWord: String, mdagNode: MDAGNode, square: Square) {

}
