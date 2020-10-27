import domain.BoardDO;
import domain.MoveDO;
import org.junit.Before;
import org.junit.Test;
import wordfeudapi.domain.ApiBoard;
import wordfeudapi.domain.ApiTile;
import wordfeudapi.domain.TileMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MoveFinderTest {

    private MoveFinder moveFinder;

    @Before
    public void setup() {
        Dictionary.initialize();
        moveFinder = new MoveFinder(getStandardBoard());
    }

    @Test
    public void findHighestScoringMoveWithBlanks() {

        char[][] charBoard = {
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', 'K', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', 'J', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'R', 'E', 'I', 'F', 'E', 'N', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', 'P', '-', '-', 'H', 'O', 'P', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', 'p', '-', '-', '-', '-', 'I', 'L', '-', '-', '-', '-', '-', '-', '-'},
                {'-', 'E', 'D', '-', '-', 'G', 'N', 'O', 'G', '-', '-', '-', '-', '-', 'T'},
                {'S', 'T', 'O', '-', 'W', 'U', '-', 'F', 'I', 'L', '-', '-', '-', 'T', 'V'},
                {'Y', '-', 'K', 'J', 'E', 'D', '-', 'F', '-', 'Ø', 'D', '-', '-', 'R', '-'},
                {'D', '-', 'T', 'A', 'B', '-', 'F', 'A', 'R', 'V', 'A', 'N', 'N', 'A', '-'},
                {'D', '-', 'O', 'K', '-', '-', '-', '-', '-', '-', '-', 'U', '-', 'S', '-'},
                {'E', 'I', 'R', 'E', '-', '-', '-', '-', '-', '-', 'B', 'L', 'Æ', 'H', '-'},
                {'-', '-', 'e', 'R', '-', '-', '-', '-', '-', 'V', 'A', 'L', 'S', '-', '-'},
                {'-', '-', 'N', '-', '-', '-', '-', '-', '-', 'E', 'H', '-', 'A', 'R', 'K'}};

        ArrayList<MoveDO> allMoves = moveFinder.findAllMoves(new BoardDO(charBoard), "SSSSSSS");

        List<TileMove> sorted = allMoves.stream()
                .map(MoveDO::toTileMove)
                .sorted(Comparator.comparingInt(TileMove::getPoints))
                .collect(Collectors.toList());

        assertThat(sorted.get(sorted.size()-1).getWord(), is("SKJEPpET"));
        assertThat(sorted.get(sorted.size()-1).getPoints(), is(16));
    }

    @Test
    public void findHighestScoringMove() {
        char[][] charBoard = {
                {'-', '-', '-', '-', 'S', '-', 'K', 'O', 'N', 'T', 'I', '-', '-', '-', '-'},
                {'-', '-', '-', '-', 'K', 'Y', 'L', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', 'S', 'A', 'R', 'V', 'I', 'N', 'G', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', 'V', 'Å', '-', '-', '-', '-', '-', '-', 'K', '-', '-', '-'},
                {'G', 'L', 'A', 'F', 'S', '-', '-', 'A', 'U', 'T', 'O', 'l', 'I', 'N', 'E'},
                {'-', 'U', 'L', 'L', '-', '-', '-', '-', 'T', '-', '-', 'A', '-', '-', '-'},
                {'-', '-', '-', 'A', '-', '-', '-', '-', 'E', '-', '-', 'F', '-', '-', '-'},
                {'-', '-', '-', 'S', '-', 'W', 'E', 'B', 'B', '-', '-', 'F', '-', '-', '-'},
                {'-', '-', '-', 'S', 'I', 'C', '-', '-', 'A', '-', '-', '-', '-', '-', '-'},
                {'B', 'Æ', 'R', 'E', 'R', '-', '-', '-', 'N', '-', '-', '-', '-', '-', '-'},
                {'-', 'H', 'U', 'T', 'E', '-', '-', '-', 'E', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'}};

        ArrayList<MoveDO> allMoves = moveFinder.findAllMoves(new BoardDO(charBoard), "HIMLANÅ");

        List<TileMove> sorted = allMoves.stream()
                .map(MoveDO::toTileMove)
                .sorted(Comparator.comparingInt(TileMove::getPoints))
                .collect(Collectors.toList());

        assertThat(sorted.get(sorted.size()-1).getWord(), is("HEIMLÅNA"));
        assertThat(sorted.get(sorted.size()-1).getPoints(), is(136));

    }

    @Test
    public void findHighestScoringMovePartII() {
        char[][] charBoard = {
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', 'J', 'A', 'R', '-', '-', '-', 'S', '-', '-'},
                {'-', '-', '-', '-', 'p', 'R', 'O', 'S', 'O', 'D', 'I', '-', 'P', '-', '-'},
                {'-', '-', '-', 'S', 'M', 'A', 'K', '-', '-', '-', 'T', 'R', 'A', 'C', 'E'},
                {'-', '-', '-', 'M', '-', '-', '-', '-', '-', '-', '-', '-', 'H', 'U', '-'},
                {'-', '-', '-', 'Ø', '-', '-', 'K', 'I', 'T', 'S', 'J', '-', 'I', 'T', '-'},
                {'-', '-', '-', 'R', '-', 'W', 'I', 'T', '-', '-', 'Æ', 'R', 'E', '-', '-'},
                {'-', '-', '-', 'F', '-', '-', '-', '-', '-', '-', 'V', 'A', 'N', '-', '-'},
                {'-', '-', '-', 'O', '-', '-', '-', '-', '-', '-', 'L', 'A', '-', '-', '-'},
                {'B', 'O', 'E', 'R', '-', 'F', 'L', 'I', 'D', 'D', 'E', '-', '-', '-', '-'},
                {'-', '-', '-', 'M', '-', '-', '-', '-', '-', '-', 'T', 'U', '-', '-', '-'},
                {'-', '-', 'G', 'A', 'N', 'G', 'E', 'N', 'E', '-', '-', 'H', '-', '-', '-'},
                {'-', '-', '-', '-', '-', 'Å', 'P', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', 'F', 'Y', 'S', '-', '-', '-', '-', '-', '-', '-', '-', '-'}};

        ArrayList<MoveDO> allMoves = moveFinder.findAllMoves(new BoardDO(charBoard), "DULRES*");

        List<TileMove> sorted = allMoves.stream()
                .map(MoveDO::toTileMove)
                .sorted(Comparator.comparingInt(TileMove::getPoints))
                .collect(Collectors.toList());

        assertThat(sorted.get(sorted.size()-1).getWord(), is("DULeRES"));
        assertThat(sorted.get(sorted.size()-1).getPoints(), is(67));
    }

    @Test
    public void comparison() {
        char[][] charBoard = {
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', 'G', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', 'R', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', 'G', 'J', 'O', 'R', 'S', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', 'W', '-', 'L', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', 'L', '-', 'U', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', 'F', 'Y', 'R', 'T', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', 'F', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-'}};

        ArrayList<MoveDO> allMovesGammel = moveFinder.findAllMoves(new BoardDO(charBoard), "DNSPRIA");
        List<String> gammelWords = allMovesGammel.stream().map(move -> move.word).collect(Collectors.toList());

        ApiBoard emptyApiBoard = new ApiBoard(new int[15][15]);
        ArrayList<ApiTile> apiTiles = new ArrayList<>();
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if (charBoard[x][y] != '-') {
                    apiTiles.add(new ApiTile(x, y, charBoard[x][y], false));
                }
            }
        }
        ApiTile[] apiTilesArray = Arrays.stream(apiTiles.toArray()).toArray(ApiTile[]::new);

        Board boardWithTiles = new Board(emptyApiBoard, apiTilesArray);

        List<Move> nyeMoves = boardWithTiles.findAllMoves("DNSPRIA");

        System.out.println();
    }

    private static ApiBoard getStandardBoard() {
        int[] a = {2,0,0,0,4,0,0,1,0,0,4,0,0,0,2};
        int[] b = {0,1,0,0,0,2,0,0,0,2,0,0,0,1,0};
        int[] c = {0,0,3,0,0,0,1,0,1,0,0,0,3,0,0};
        int[] d = {0,0,0,2,0,0,0,3,0,0,0,2,0,0,0};
        int[] e = {4,0,0,0,3,0,1,0,1,0,3,0,0,0,4};
        int[] f = {0,2,0,0,0,2,0,0,0,2,0,0,0,2,0};
        int[] g = {0,0,1,0,1,0,0,0,0,0,1,0,1,0,0};
        int[] h = {1,0,0,3,0,0,0,0,0,0,0,3,0,0,1};
        int[] i = {0,0,1,0,1,0,0,0,0,0,1,0,1,0,0};
        int[] j = {0,2,0,0,0,2,0,0,0,2,0,0,0,2,0};
        int[] k = {4,0,0,0,3,0,1,0,1,0,3,0,0,0,4};
        int[] l = {0,0,0,2,0,0,0,3,0,0,0,2,0,0,0};
        int[] m = {0,0,3,0,0,0,1,0,1,0,0,0,3,0,0};
        int[] n = {0,1,0,0,0,2,0,0,0,2,0,0,0,1,0};
        int[] o = {2,0,0,0,4,0,0,1,0,0,4,0,0,0,2};
        return new ApiBoard(new int[][]{a, b, c, d, e, f, g, h, i, j, k, l, m, n, o});
    }
}
