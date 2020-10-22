import wordfeudapi.domain.ApiBoard
import wordfeudapi.domain.ApiTile
import java.util.*

data class Board(
    val squares: List<List<Square>>,
) {
    constructor(apiBoard: ApiBoard, apiTiles: Array<ApiTile>) : this(
        apiBoard.board.mapIndexed { x, ints ->
            ints.mapIndexed { y, _ ->
                val tile = apiTiles.find { it.x == x && it.y == y }
                    ?.let { Tile(if (it.isWildcard) it.character.toLowerCase() else it.character) }
                Square(tile = tile,
                    letterMultiplier = apiBoard.getLetterMultiplier(x, y),
                    wordMultiplier = apiBoard.getWordMultiplier(x, y))
            }
        })

    //TODO set nextSquare
    //TODO //if board is empty - make middle square anchor

}

//lag egen Tile
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

    fun nextSquare(): Square? {
        println("not implemented")
        return null
    }
}

data class Tile(
    val letter: Char
)
