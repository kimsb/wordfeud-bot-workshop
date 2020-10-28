import Constants.VALID_LETTERS
import domain.Row
import domain.Square
import domain.Tile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class RowTest {

    @Test
    fun `Cross-checks`() {

        val squares = createTestRow("-----BJØRN-BÆR-").crossChecks()

        assertThat(squares[0].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[1].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[2].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[3].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[4].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[5].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[6].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[7].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[8].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[9].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        val e = BitSet(26)
        e.flip(VALID_LETTERS.indexOf('E'))
        assertThat(squares[10].crossChecks).`as`("Her skulle bare bit e (index 4) vært satt").isEqualTo(e)
        assertThat(squares[11].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[12].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[13].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        val ae = BitSet(26)
        ae.flip(VALID_LETTERS.indexOf('A'))
        ae.flip(VALID_LETTERS.indexOf('E'))
        assertThat(squares[14].crossChecks).`as`("Her skulle bit a og e (index 0 og 4) vært satt").isEqualTo(ae)
    }

    @Nested
    @DisplayName("Cross sums")
    inner class CrossSumsTest {

        @Test
        fun `Cross sums`() {
            val squares = createTestRow("-----BJØRN-BÆR-").crossChecks()

            assertThat(squares[0].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[1].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[2].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[3].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[4].crossSum).`as`("Her skulle crossSum vært 14 (for BJØRN)").isEqualTo(14)
            assertThat(squares[5].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[6].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[7].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[8].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[9].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[10].crossSum).`as`("Her skulle crossSum vært 27 (14 for BJØRN + 13 for BÆR)").isEqualTo(27)
            assertThat(squares[11].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[12].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[13].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[14].crossSum).`as`("Her skulle crossSum vært 13 (for BÆR)").isEqualTo(13)
        }

        @Test
        fun `Cross sums with blank`() {
            val squares = createTestRow("-s---BJØRN-BÆR-").crossChecks()

            assertThat(squares[0].crossSum).`as`("Her skulle crossSum vært 0 (har blank bokstav som suffix)").isEqualTo(0)
            assertThat(squares[1].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[2].crossSum).`as`("Her skulle crossSum vært 0 (har blank bokstav som prefix)").isEqualTo(0)
            assertThat(squares[3].crossSum).`as`("Her skulle crossSum vært null (har ingen prefix eller suffix)").isNull()
            assertThat(squares[4].crossSum).`as`("Her skulle crossSum vært 14 (for BJØRN)").isEqualTo(14)
            assertThat(squares[5].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[6].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[7].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[8].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[9].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[10].crossSum).`as`("Her skulle crossSum vært 27 (14 for BJØRN + 13 for BÆR)").isEqualTo(27)
            assertThat(squares[11].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[12].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[13].crossSum).`as`("Her skulle crossSum vært null (feltet er allerede brukt)").isNull()
            assertThat(squares[14].crossSum).`as`("Her skulle crossSum vært 13 (for BÆR)").isEqualTo(13)
        }
    }

    private fun createTestRow(rowString: String): Row {
        return Row(rowString
            .map {
                if (it == '-')
                    Square()
                else
                    Square(tile = Tile(it))
            })
    }

    private fun fullBitSet(): BitSet {
        val bitSet = BitSet(26)
        bitSet.flip(0, 26)
        return bitSet
    }
}


