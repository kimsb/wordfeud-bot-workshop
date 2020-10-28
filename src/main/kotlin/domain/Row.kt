package domain

import Constants
import Constants.VALID_LETTERS
import Constants.letterScore
import Dictionary.contains
import Dictionary.getSourceNode
import mdag.MDAGNode
import java.util.*

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
        return squares.mapIndexed { squareIndex, square ->
            val bitSet = BitSet(26)
            var crossSum: Int? = null
            if (!square.isOccupied()) {
                val prefix = getPrefix(squareIndex)
                val suffix = getSuffix(squareIndex)
                if (prefix.isEmpty() && suffix.isEmpty()) {
                    bitSet.flip(0, 26)
                } else {
                    VALID_LETTERS.forEachIndexed { bitSetIndex, letter ->
                        bitSet.set(bitSetIndex, contains(prefix + letter + suffix))
                    }
                    crossSum = if ((prefix + suffix).isEmpty()) null else (prefix + suffix).map(Constants::letterScore).sum()
                }
            }
            square.copy(crossChecks = bitSet, crossSum = crossSum)
        }
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
                if (square.crossSum != null) {
                    crossSums += (squareScore + square.crossSum) * square.wordMultiplier
                }
                squareScore
            }
        }.sum() * wordMultiplier + crossSums + (if (addedLetters == 7) 40 else 0)
    }

    private fun leftPart(
        partialWord: String,
        node: MDAGNode,
        limit: Int,
        anchorIndex: Int,
        rack: Rack
    ) {
        extendRight(partialWord, node, anchorIndex, anchorIndex, rack)
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

    private fun extendRight(
        partialWord: String,
        node: MDAGNode,
        anchorIndex: Int,
        index: Int,
        rack: Rack
    ) {
        val square = squares.getOrElse(index) { Square() }
        if (!square.isOccupied()) {
            if (index != anchorIndex && node.isAcceptNode) {
                rowMoves.add(RowMove(partialWord,
                    index - partialWord.length,
                    calculateScore(partialWord, index - partialWord.length)))
            }
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key, it.value, anchorIndex, index + 1, rack.without(it.key))
                }
                if (rack.contains('*') && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key.toLowerCase(), it.value, anchorIndex, index + 1, rack.without('*'))
                }
            }
        } else {
            square.getLetter()?.let {
                if (node.hasOutgoingTransition(it)) {
                    extendRight(partialWord + it, node.transition(it), anchorIndex, index + 1, rack)
                }
            }
        }
    }

}
