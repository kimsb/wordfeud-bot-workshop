package domain

import wordfeudapi.domain.ApiTile
import wordfeudapi.domain.TileMove

data class Move(
    val word: String,
    val score: Int,
    val row: Int,
    val horizontal: Boolean,
    val addedTiles: List<Pair<Tile, Coordinate>>
) {
    fun toTileMove(): TileMove {
        return TileMove(addedTiles.map {
            toApiTile(it)
        }.toTypedArray(),
            word,
            score,
            horizontal
        )
    }

    private fun toApiTile(pair: Pair<Tile, Coordinate>): ApiTile {
        return ApiTile(pair.second.column,
            pair.second.row,
            pair.first.letter.toUpperCase(),
            pair.first.letter.isLowerCase())
    }
}

