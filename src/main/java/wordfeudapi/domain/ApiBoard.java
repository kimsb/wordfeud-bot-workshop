package wordfeudapi.domain;

import com.google.gson.Gson;

import java.util.Arrays;

/**
 * @author Pierre Ingmansson (piin00)
 */
public class ApiBoard {
    private int[][] board;

    public ApiBoard(final int[][] board) {
        this.board = board;
    }

    public ApiBoard() {
    }

    public int[][] getBoard() {
        return board;
    }

    public static ApiBoard fromJson(final String json) {
        return new Gson().fromJson(json, ApiBoard.class);
    }

    @Override
    public String toString() {
        return "domain.Board{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    public int getLetterMultiplier(int x, int y) {
        switch (board[x][y]) {
            case 1 : return 2;
            case 2 : return 3;
            default: return 1;
        }
    }

    public int getWordMultiplier(int x, int y) {
        switch (board[x][y]) {
            case 3 : return 2;
            case 4 : return 3;
            default: return 1;
        }
    }
}
