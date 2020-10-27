import wordfeudapi.domain.ApiBoard
import wordfeudapi.domain.ApiTile

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
        return Board(getTransposedRows().map(Row::crossChecks)).getTransposedRows()
    }

    fun getTransposedRowsWithCrossChecks(): List<Row> {
        return Board(getRows().map(Row::crossChecks)).getTransposedRows()
    }

    fun transpose(): Board {
        return Board(squares.indices.map { row ->
            squares.indices.map { column ->
                squares[column][row]
            }
        })
    }

    private fun getRows(): List<Row> {
        return squares.map {
            Row(it)
        }
    }

    private fun getTransposedRows(): List<Row> {
        return transpose().squares.map {
            Row(it)
        }
    }
}
