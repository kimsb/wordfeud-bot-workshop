import Dictionary.getSourceNode
import constants.ScoreConstants.validLetters
import mdag.MDAGNode
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



// "-----BJØRN-BÆR-"
fun findAcrossMoves(row: Row, rack: Rack) {
    var limit = 0
    row.squares.forEachIndexed { index, square ->
        if (square.isAnchor) {
            val prefix = row.getPrefix(index)
            if (prefix.isNotEmpty()) {
                extendRight(prefix, getSourceNode().transition(prefix), square, rack)
                limit = 0
            } else {
                leftPart(prefix, getSourceNode(), limit++, square, rack)
            }
        }
    }
}


/*
ExtendRight (PartialWord, N, Anchorsquare)
if limit > 0 then
    for each edge E out of N
        if the letter 1 labeling edge E is in our rack then
        remove a tile labeled 1 from the rack
        let N' be the node reached by following egde E
        Leftpart (PartialWord . 1, N', limit - 1)
        put the tile 1 back into the rack
*/
fun leftPart(partialWord: String, node: MDAGNode, limit: Int, anchorSquare: Square, rack: Rack) {
    extendRight(partialWord, node, anchorSquare, rack)
    if (limit > 0) {
         node.outgoingTransitions.entries.forEach {
             if (rack.contains(it.key)) {
                 leftPart(partialWord + it.key, it.value, limit - 1, anchorSquare, rack.without(it.key))
             }
         }
    }
}


/*
if square is vacant then
    if N is a terminal node then
        LegalMove (PartialWord)
    for each edge E out of N
        if the letter 1 labeling edge E is in our rack AND 1 is in the cross-check set of square then
            remove a tile 1 from the rack
            let N' be the node reached by following edge E
            let next-square be the square to the right of square
            ExtendRight (PartialWord - 1, N', next-square )
            put the tile 1 back into the rack
else
    let 1 be the letter occupying square
    if N has an edge labeled by 1 that leads to some node N' then
        let next-square be the square to the right of square
        ExtendRight (PartialWord . 1, N', next-square )
*/
fun extendRight(partialWord: String, node: MDAGNode, square: Square?, rack: Rack) {
    if (square == null) {
        return
    }
    if (!square.isOccupied()) {
        if (node.isAcceptNode) {
            //TODO legal move
            println(partialWord)
            Dictionary.ordFunnet++
        }
        node.outgoingTransitions.entries.forEach {
            if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                extendRight(partialWord + it.key, it.value, square.nextSquare, rack.without(it.key))
            }
        }
    } else {
        val letter = square.getLetter()!!
        if (node.hasOutgoingTransition(letter)) {
            extendRight(partialWord + letter, node.transition(letter), square.nextSquare, rack)
        }
    }
}

fun findAllMoves(board: Board, rackString: String) {

    println("STARTER ROWS:")
    board.getRowsWithCrossChecks().forEach {
        findAcrossMoves(it, Rack(rackString.toList()))
    }
    println("STARTER COLUMNS:")
    board.getTransposedColumnsWithCrossChecks().forEach {
        findAcrossMoves(it, Rack(rackString.toList()))
    }

}
