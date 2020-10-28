package domain

import Dictionary.getSourceNode
import mdag.MDAGNode

data class Row(
    val squares: List<Square>
) {

    private val rowMoves = mutableListOf<RowMove>()

    fun findAcrossMoves(rack: Rack): List<RowMove> {
        squares.forEachIndexed { index, square ->
            if (square.isAnchor) {
                val prefix = getPrefix(index)
                if (prefix.isNotEmpty()) {
                    extendRight(prefix, getSourceNode().transition(prefix), index, index, rack)
                } else {
                    val limit = (0 until index).find {
                        squares[index - 1 - it].isAnchor
                    } ?: index
                    leftPart("", getSourceNode(), limit, index, rack)
                }
            }
        }
        return rowMoves
    }

    fun crossChecks(): List<Square> {
        //TODO Cross-checks
        return squares
    }


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

    private fun calculateScore(word: String, startIndex: Int): Int {
        //TODO calculateScore
        return 0
    }

    private fun leftPart(
        partialWord: String,
        node: MDAGNode,
        limit: Int,
        anchorIndex: Int,
        rack: Rack
    ) {
        //TODO leftPart
    }

    private fun extendRight(
        partialWord: String,
        node: MDAGNode,
        anchorIndex: Int,
        index: Int,
        rack: Rack
    ) {
        //TODO extendRight
    }

}
