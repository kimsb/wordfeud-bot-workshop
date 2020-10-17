package domain;

import wordfeudapi.domain.Tile;

public class BoardDO {

    public char[][] charBoard;

    public BoardDO(char[][] charBoard) {
        this.charBoard = charBoard;
    }

    public BoardDO(Tile[] tiles) {
        charBoard = getEmptyCharBoard();
        for (Tile tile : tiles) {
            charBoard[tile.getX()][tile.getY()]
                    = tile.isWildcard() ? Character.toLowerCase(tile.getCharacter()) : tile.getCharacter();
        }
    }

    public char[][] getCharBoard() {
        return charBoard;
    }

    public char[][] getEmptyCharBoard() {
        char[][] charBoard = new char[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                charBoard[i][j] = '-';
            }
        }
        return charBoard;
    }

    public char[][] getTransposedCharBoard() {

        char[][] transposedCharBoard = new char[15][15];

        //"transpose"
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                transposedCharBoard[i][j] = charBoard[j][i];

            }
        }
        return transposedCharBoard;
    }

    public boolean[][] getAnchors(char[][] charBoard) {
        boolean[][] isAnchor = new boolean[15][15];
        int anchorCount = 0;

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (charBoard[i][j] == '-') {
                    if (i != 0 && charBoard[i - 1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 0 && charBoard[i][j - 1] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (i != 14 && charBoard[i + 1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 14 && charBoard[i][j + 1] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    }
                }
            }
        }

        if (anchorCount == 0) {
            isAnchor[7][7] = true;
        }

        return isAnchor;
    }
}
