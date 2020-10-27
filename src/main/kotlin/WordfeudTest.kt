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

fun findAllMoves(board: Board, rackString: String): List<String> {

    val dictionary = Dictionary.getDictionary();

    println("STARTER ROWS:")
    val lovligeOrdRows = board.getRowsWithCrossChecks().flatMapIndexed {index, it ->
        println("ROW-INDEX: $index")
        it.findAcrossMoves(Rack(rackString.toList()))
    }

    println("STARTER COLUMNS:")
    val lovligeOrdColumns = board.getTransposedRowsWithCrossChecks().flatMapIndexed { index, it ->
        println("COLUMN-INDEX: $index")
        it.findAcrossMoves(Rack(rackString.toList()))
    }

    return lovligeOrdRows + lovligeOrdColumns
}
