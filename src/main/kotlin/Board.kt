import Dictionary.getSourceNode
import constants.ScoreConstants
import constants.ScoreConstants.validLetters
import mdag.MDAGNode
import wordfeudapi.domain.ApiBoard
import wordfeudapi.domain.ApiTile
import java.util.*

class Board(squares: List<List<Square>>) {
    val squares: List<List<Square>>

    //Setter isAnchor for hver square
    init {
        this.squares = squares.mapIndexed { i, row ->
            row.mapIndexed { j, square ->
                val isAnchor =
                    !squares[i][j].isOccupied() &&
                        ((i == 7 && j == 7) ||
                            squares.getOrNull(i - 1)?.get(j)?.isOccupied() == true ||
                            squares[i].getOrNull(j - 1)?.isOccupied() == true ||
                            squares[i].getOrNull(j + 1)?.isOccupied() == true ||
                            squares.getOrNull(i + 1)?.get(j)?.isOccupied() == true)
                square.copy(isAnchor = isAnchor)
            }
        }
    }

    constructor(apiBoard: ApiBoard, apiTiles: Array<ApiTile>) : this(
        apiBoard.board.mapIndexed { row, ints ->
            ints.mapIndexed { column, _ ->
                val tile = apiTiles.find { it.x == column && it.y == row }
                    ?.let { Tile(if (it.isWildcard) it.character.toLowerCase() else it.character) }
                Square(tile = tile,
                    letterMultiplier = apiBoard.getLetterMultiplier(column, row),
                    wordMultiplier = apiBoard.getWordMultiplier(column, row))
            }
        }
    )

    fun getRowsWithCrossChecks(): List<Row> {
        return Board(getColumnsAsRows().map(Row::crossChecks)).transpose().getRows()
    }

    fun getTransposedColumnsWithCrossChecks(): List<Row> {
        return Board(getRows().map(Row::crossChecks)).getColumnsAsRows()
    }

    fun transpose(): Board {
        return Board(squares.indices.map { row ->
            squares.indices.map { column ->
                squares[column][row]
            }
        })
    }

    private fun getRows(): List<Row> {
        return squares.map { row ->
            var previousSquare: Square? = null
            Row(
                row.reversed().map { square ->
                    val updatedSquare = square.copy(nextSquare = previousSquare)
                    previousSquare = updatedSquare
                    updatedSquare
                }.reversed()
            )
        }
    }

    private fun getColumnsAsRows(): List<Row> {
        return (squares.indices).map { column ->
            var previousSquare: Square? = null
            Row(
                (squares.indices.reversed()).map { row ->
                    val updatedSquare = squares[row][column].copy(nextSquare = previousSquare)
                    previousSquare = updatedSquare
                    updatedSquare
                }.reversed()
            )
        }
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
                    //TODO ta hensyn til blank i crossSum
                    crossSum = (prefix + suffix).map(ScoreConstants::letterScore).sum()
                }
            }
            square.copy(crossChecks = bitSet,
                crossSum = crossSum)
        }
    }

    // "-----BJØRN-BÆR-"

    var currentAnchor = 0
    fun findAcrossMoves(rack: Rack): List<String> {
        squares.forEachIndexed { index, square ->
            if (square.isAnchor) {

                println("ANCHOR-INDEX: $index")
                currentAnchor = index //TODO må jeg gjøre dette?

                val prefix = getPrefix(index)
                if (prefix.isNotEmpty()) {
                    extendRight(prefix, getSourceNode().transition(prefix), index, rack)
                } else {
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
        return lovligeOrd
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
    fun leftPart(partialWord: String, node: MDAGNode, limit: Int, anchorIndex: Int, rack: Rack) {
        extendRight(partialWord, node, anchorIndex, rack)
        if (limit > 0) {
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key)) {
                    leftPart(partialWord + it.key, it.value, limit - 1, anchorIndex, rack.without(it.key))
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
    fun extendRight(partialWord: String, node: MDAGNode, index: Int, rack: Rack) {
        val square = squares.getOrElse(index) { Square() }
        if (!square.isOccupied()) {
                if (index != currentAnchor && node.isAcceptNode) { //TODO må ha brukt brikker! //TODO kan ikke være anchor, sant?
                    //TODO legal move
                    println("$partialWord - index: $index, leftOnRack: ${rack.tiles.joinToString()}")
                    lovligeOrd.add(partialWord)
                }
                node.outgoingTransitions.entries.forEach {
                    if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                        extendRight(partialWord + it.key, it.value, index + 1, rack.without(it.key))
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

    val lovligeOrd = mutableListOf<String>()
}

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
        return crossChecks[validLetters().indexOf(letter)]
    }
}

data class Tile(
    val letter: Char
)

data class Rack(
    val tiles: List<Char>
) {
    fun contains(letter: Char): Boolean {
        return tiles.any { it == letter }
    }

    fun without(letter: Char): Rack {
        val index = tiles.indexOf(letter)
        return Rack(tiles.subList(0, index) + tiles.subList(index + 1, tiles.size))
    }
}
