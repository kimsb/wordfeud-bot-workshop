import constants.ScoreConstants
import constants.ScoreConstants.validLetters
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
                    crossSum = (prefix + suffix).map(ScoreConstants::letterScore).sum()
                }
            }
            square.copy(crossChecks = bitSet,
                crossSum = crossSum)
        }
    }
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
        return tile?.letter
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
