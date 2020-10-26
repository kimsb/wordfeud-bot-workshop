import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import wordfeudapi.domain.ApiBoard
import wordfeudapi.domain.ApiTile

class BoardTest {

    @Nested
    @DisplayName("Anchors")
    inner class AnchorTest {

        @Test
        fun emptyBoard() {
            val emptyApiBoard = ApiBoard(Array(15) { IntArray(15) })
            val emptyBoard = Board(emptyApiBoard, emptyArray())

            assertThat(emptyBoard.squares[7][7].isAnchor).`as`("Midterste square skal være anchor på tomt brett").isTrue
            assertThat(emptyBoard.squares.flatten().count { it.isAnchor }).`as`("Tomt brett skal bare ha én anchor").isEqualTo(1)
        }

        @Test
        fun boardWithTiles() {
            val emptyApiBoard = ApiBoard(Array(15) { IntArray(15) })
            val apiTiles = arrayOf(
                ApiTile(6, 7, 'Y', false),
                ApiTile(7, 7, 'O', false)
            )
            val boardWithTiles = Board(emptyApiBoard, apiTiles)

            assertThat(boardWithTiles.squares[6][6].isAnchor).`as`("squares[6][6] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[6][7].isAnchor).`as`("squares[6][7] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[7][5].isAnchor).`as`("squares[7][5] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[7][8].isAnchor).`as`("squares[7][8] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[8][6].isAnchor).`as`("squares[8][6] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[8][7].isAnchor).`as`("squares[8][7] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares.flatten().count { it.isAnchor }).`as`("brettet skal ha 6 anchors").isEqualTo(6)
        }

    }

    @Test
    fun `remove tile from rack`() {
        val rack = Rack("ABCDEFA".toList())

        val aRemoved = rack.without('A')

        assertThat(aRemoved.tiles).isEqualTo("BCDEFA".toList())
    }

    @Test
    fun transpose() {
        val emptyApiBoard = ApiBoard(Array(15) { IntArray(15) })
        val apiTiles = arrayOf(
            ApiTile(6, 7, 'Y', false),
            ApiTile(7, 7, 'O', false)
        )
        val boardWithTiles = Board(emptyApiBoard, apiTiles)

        val transposed = boardWithTiles.transpose()

        assertThat(transposed.squares[6][7].getLetter()).isEqualTo('Y')
        assertThat(transposed.squares[7][7].getLetter()).isEqualTo('O')
    }
}


