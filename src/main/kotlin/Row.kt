import Dictionary.getSourceNode
import constants.ScoreConstants
import constants.ScoreConstants.letterScore
import mdag.MDAGNode
import java.util.*

data class Row(
    val squares: List<Square>
) {
    private fun getPrefix(startIndex: Int): String {
        val builder = StringBuilder()
        var index = startIndex
        while (--index in squares.indices && squares[index].isOccupied()) {
            builder.append(squares[index].getLetter()!!)
        }
        return builder.reverse().toString()
    }

    private fun getSuffix(startIndex: Int): String {
        val builder = StringBuilder()
        var index = startIndex
        while (++index in squares.indices && squares[index].isOccupied()) {
            builder.append(squares[index].getLetter()!!)
        }
        return builder.toString()
    }

    //CROSSCHECKS + CROSSUM
    fun crossChecks(): List<Square> {
        return squares.mapIndexed { index, square ->
            var bitSet = BitSet(26)
            var crossSum = 0
            if (!square.isOccupied()) {
                val prefix = getPrefix(index)
                val suffix = getSuffix(index)

                if (prefix.isEmpty() && suffix.isEmpty()) {
                    bitSet.flip(0, 26)
                } else {
                    bitSet = validCrossCheckLetters(prefix, suffix)
                    crossSum = (prefix + suffix).map(ScoreConstants::letterScore).sum()
                }
            }
            square.copy(crossChecks = bitSet,
                crossSum = crossSum)
        }
    }

    private var currentAnchor = 0
    fun findAcrossMoves(rack: Rack): List<RowMove> {
        squares.forEachIndexed { index, square ->
            if (square.isAnchor) {

                println("ANCHOR-INDEX: $index")
                currentAnchor = index //TODO må jeg gjøre dette?

                val prefix = getPrefix(index)
                if (prefix.isNotEmpty()) {
                    extendRight(prefix, getSourceNode().transition(prefix), index, rack)
                } else {
                    //TODO kan dette gjøres penere?
                    var limit = 0
                    for (i in index - 1 downTo 0) {
                        if (squares[i].isAnchor) {
                            break
                        }
                        limit++
                    }
                    leftPart(prefix, getSourceNode(), limit, index, rack)
                }
            }
        }
        return rowMoves
    }

    private fun calculateScore(word: String, startIndex: Int): Int {
        var wordMultiplier = 1
        var crossSums = 0
        var addedLetters = 0
        return squares.subList(startIndex, startIndex + word.length).mapIndexed { index, square ->
            if (square.isOccupied()) {
                letterScore(square.getLetter()!!)
            } else {
                addedLetters++
                wordMultiplier *= square.wordMultiplier
                val squareScore = letterScore(word[index]) * square.letterMultiplier
                if (square.crossSum > 0) {
                    crossSums += (squareScore + square.crossSum) * square.wordMultiplier
                }
                squareScore
            }
        }.sum() * wordMultiplier + crossSums + (if (addedLetters == 7) 40 else 0)
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
    private fun leftPart(
        partialWord: String,
        node: MDAGNode,
        limit: Int,
        anchorIndex: Int,
        rack: Rack
    ) {
        extendRight(partialWord, node, anchorIndex, rack)
        if (limit > 0) {
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key)) {
                    leftPart(partialWord + it.key, it.value, limit - 1, anchorIndex, rack.without(it.key))
                }
                if (rack.contains('*')) {
                    leftPart(partialWord + it.key.toLowerCase(), it.value, limit - 1, anchorIndex, rack.without('*'))
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
    private fun extendRight(
        partialWord: String,
        node: MDAGNode,
        index: Int,
        rack: Rack
    ) {
        val square = squares.getOrElse(index) { Square() }
        if (!square.isOccupied()) {
            if (index != currentAnchor && node.isAcceptNode) { //TODO må ha brukt brikker! //TODO kan ikke være anchor, sant?
                //TODO legal move
                println("$partialWord - index: $index, leftOnRack: ${rack.tiles.joinToString()}")
                rowMoves.add(RowMove(partialWord,
                    index - partialWord.length,
                    calculateScore(partialWord, index - partialWord.length)))
            }
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key, it.value, index + 1, rack.without(it.key))
                }
                if (rack.contains('*') && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key.toLowerCase(), it.value, index + 1, rack.without('*'))
                }
            }
        } else {
            //TODO dette var annerledes, kan jeg gjøre det tilbake?
            square.getLetter()?.let {
                if (node.hasOutgoingTransition(it)) {
                    extendRight(partialWord + it, node.transition(it), index + 1, rack)
                }
            }
        }

    }

    // TODO kan kanskje la extendRight returnere en liste med wordsStartingAt..?
    private val rowMoves = mutableListOf<RowMove>()

}

data class RowMove(
    val word: String,
    val startIndex: Int,
    val score: Int
)
