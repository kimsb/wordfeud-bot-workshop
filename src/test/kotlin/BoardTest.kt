import domain.Board
import domain.Move
import domain.Rack
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
        fun `Board with tiles`() {
            val apiTiles = arrayOf(
                ApiTile(6, 7, 'Y', false),
                ApiTile(7, 7, 'O', false)
            )
            val boardWithTiles = Board(standardApiBoard(), apiTiles)

            assertThat(boardWithTiles.squares[6][6].isAnchor).`as`("squares[6][6] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[6][7].isAnchor).`as`("squares[6][7] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[7][5].isAnchor).`as`("squares[7][5] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[7][8].isAnchor).`as`("squares[7][8] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[8][6].isAnchor).`as`("squares[8][6] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares[8][7].isAnchor).`as`("squares[8][7] skulle vært anchor").isTrue
            assertThat(boardWithTiles.squares.flatten().count { it.isAnchor }).`as`("brettet skal ha 6 anchors").isEqualTo(6)
        }

        @Test
        fun `Empty board`() {
            val emptyBoard = Board(standardApiBoard(), emptyArray())

            assertThat(emptyBoard.squares[7][7].isAnchor).`as`("Midterste square skal være anchor på tomt brett").isTrue
            assertThat(emptyBoard.squares.flatten().count { it.isAnchor }).`as`("Tomt brett skal bare ha én anchor").isEqualTo(1)
        }

    }

    @Test
    fun `Find all words`() {
        val apiTiles = arrayOf(
            ApiTile(6, 7, 'Y', false),
            ApiTile(7, 7, 'O', false)
        )
        val allMoves = Board(standardApiBoard(), apiTiles).findAllMoves(Rack("ERNATSL".toList()))

        assertThat(allMoves.size).isEqualTo(1568)
    }

    @Test
    fun `Find all words with blank`() {
        val apiTiles = arrayOf(
            ApiTile(6, 7, 'Y', false),
            ApiTile(7, 7, 'O', false)
        )
        val allMoves = Board(standardApiBoard(), apiTiles).findAllMoves(Rack("ERNATS*".toList()))

        assertThat(allMoves.size).isEqualTo(17244)
    }

    @Nested
    @DisplayName("Calculate score")
    inner class CalculateScoreTest {

        @Test
        fun `Find highest scoring move`() {
            val apiTiles = arrayOf(
                ApiTile(8, 10, 'E', false)
            )

            val highestScoringMove = Board(standardApiBoard(), apiTiles)
                .findAllMoves(Rack("HIMLANÅ".toList()))
                .maxByOrNull(Move::score)!!

            assertThat(highestScoringMove.word).isEqualTo("HEIMLÅNA")
            assertThat(highestScoringMove.score).isEqualTo(136)
        }

        @Test
        fun `Find highest scoring move with blank cross sum`() {
            val apiTiles = arrayOf(
                ApiTile(8, 10, 'E', false),
                ApiTile(7, 9, 'A', true)
            )

            val highestScoringMove = Board(standardApiBoard(), apiTiles)
                .findAllMoves(Rack("HIMLANÅ".toList()))
                .maxByOrNull(Move::score)!!

            assertThat(highestScoringMove.word).isEqualTo("HEIMLÅNA")
            assertThat(highestScoringMove.score).isEqualTo(139)
        }

        @Test
        fun `Find highest scoring move with two way letter multiplier`() {
            val apiTiles = arrayOf(
                ApiTile(7, 10, 'H', false),
                ApiTile(8, 9, 'B', false)
            )

            val highestScoringMove = Board(standardApiBoard(), apiTiles)
                .findAllMoves(Rack("EIMLANÅ".toList()))
                .maxByOrNull(Move::score)!!

            assertThat(highestScoringMove.word).isEqualTo("HEIMLÅNA")
            assertThat(highestScoringMove.score).isEqualTo(148)
        }

        @Test
        fun `Find highest scoring move with two way word multiplier`() {
            val apiTiles = arrayOf(
                ApiTile(7, 10, 'H', false),
                ApiTile(10, 9, 'Ø', false)
            )

            val highestScoringMove = Board(standardApiBoard(), apiTiles)
                .findAllMoves(Rack("EIMLANÅ".toList()))
                .maxByOrNull(Move::score)!!

            assertThat(highestScoringMove.word).isEqualTo("HEIMLÅNA")
            assertThat(highestScoringMove.score).isEqualTo(154)
        }
    }

    private fun standardApiBoard(): ApiBoard {
        return ApiBoard(
            arrayOf(
                intArrayOf(2, 0, 0, 0, 4, 0, 0, 1, 0, 0, 4, 0, 0, 0, 2),
                intArrayOf(0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0),
                intArrayOf(0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0),
                intArrayOf(0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0),
                intArrayOf(4, 0, 0, 0, 3, 0, 1, 0, 1, 0, 3, 0, 0, 0, 4),
                intArrayOf(0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0),
                intArrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
                intArrayOf(1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1),
                intArrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
                intArrayOf(0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0),
                intArrayOf(4, 0, 0, 0, 3, 0, 1, 0, 1, 0, 3, 0, 0, 0, 4),
                intArrayOf(0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0),
                intArrayOf(0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0),
                intArrayOf(0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0),
                intArrayOf(2, 0, 0, 0, 4, 0, 0, 1, 0, 0, 4, 0, 0, 0, 2)
            )
        )
    }

}
