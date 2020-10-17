package domain;

import constants.ScoreConstants;
import wordfeudapi.domain.Board;
import wordfeudapi.domain.Tile;
import wordfeudapi.domain.TileMove;

import java.util.ArrayList;

public class MoveDO {


    //Dette liker jeg ikke... men om legget er vertikalt, så er 'row' raden på det transposa brettet...
    //Tilsvarende med column.
    //TODO: representere row og column kun riktig vei
    //addedTiles kommer til å representere det faktiske brettet -> row er alltid rad på brettet riktig vei.

    public String word = "";
    public int row;
    public int startColumn;
    public boolean horizontal;
    public int moveScore;
    String usedFromRack;
    ArrayList<Tile> addedTiles = new ArrayList<>();
    Board board;

    public MoveDO(int r, int startColumn, boolean trans, String fromRack, char[][] charBoard, Board board) {
        this.board = board;
        horizontal = trans;
        row = r;
        this.startColumn = startColumn;
        usedFromRack = fromRack;
        moveScore = getMoveScore(charBoard);
    }

    private int getMoveScore(char[][] charBoard) {
        //TODO: trenger ikke i/j - kan bruke row, wordstart direkte - for de endres ikke på
        int i = row;
        int j = startColumn;
        int horizontalMultiplier = 1;
        int horizontalScore = 0;
        int sum = 0;
        int letterFromRack = 0;

        int k = 0;

        int l = 0;
        while (j+l > 0 && charBoard[i][j+(--l)] != '-') {
            k--;
        }

        startColumn = j+k;

        while (letterFromRack < usedFromRack.length() || (j+k < 15 && charBoard[i][j+k] != '-')) {
            char letter = charBoard[i][j+k];
            if (letter == '-') {
                letter = usedFromRack.charAt(letterFromRack++);
                addedTiles.add(new Tile(horizontal ? j+k : i, horizontal ? i : j+k, Character.toUpperCase(letter), Character.isLowerCase(letter)));
                int letterScore = ScoreConstants.letterScore(letter) * board.getLetterMultiplier(i, j+k);
                horizontalScore += letterScore;
                int wordMultiplier = board.getWordMultiplier(i, j+k);
                horizontalMultiplier *= wordMultiplier;
                sum += getVerticalScore(charBoard, i, j+k, letter, letterScore, wordMultiplier);
            } else {
                horizontalScore += ScoreConstants.letterScore(letter);
            }
            word += letter;
            k++;
        }

        return sum + (horizontalScore * horizontalMultiplier) + (isBingo() ? 40 : 0);
    }

    private int getVerticalScore(char[][] charBoard, int i, int j, char c, int letterScore, int wordMultiplier) {
        int sum = letterScore;
        String word = "" + c;

        //upwards
        for (int k = i-1; k >= 0; k--) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            word = letter + word;
            sum += ScoreConstants.letterScore(letter);
        }
        //downwards
        for (int k = i+1; k < 15; k++) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            word += letter;
            sum += ScoreConstants.letterScore(letter);
        }
        if (word.length() > 1) {
            return sum * wordMultiplier;
        }
        return 0;
    }

    private boolean isBingo() {
        return usedFromRack.length() == 7;
    }

    public TileMove toTileMove() {
        Tile[] tiles = addedTiles.toArray(new Tile[addedTiles.size()]);
        return new TileMove(tiles, word, moveScore, horizontal);
    }

}
