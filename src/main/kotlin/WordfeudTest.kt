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











fun findAllMoves(board: Board, rackString: String): List<String> {

    val dictionary = Dictionary.getDictionary();

    println("STARTER ROWS:")
    val lovligeOrdRows = board.getRowsWithCrossChecks().flatMapIndexed {index, it ->
        println("ROW-INDEX: $index")
        it.findAcrossMoves(Rack(rackString.toList()))
    }

    println("STARTER COLUMNS:")
    val lovligeOrdColumns = board.getTransposedColumnsWithCrossChecks().flatMapIndexed { index, it ->
        println("COLUMN-INDEX: $index")
        it.findAcrossMoves(Rack(rackString.toList()))
    }

    return lovligeOrdRows + lovligeOrdColumns
}
