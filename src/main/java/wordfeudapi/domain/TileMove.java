package wordfeudapi.domain;

/**
 * @author Pierre Ingmansson
 */
public class TileMove implements Comparable<TileMove> {
    private final ApiTile[] apiTiles;
    private final String word;
    private final int points;
    private final boolean horizontalWord;

    public TileMove(final ApiTile[] apiTiles, final String word, final int points, final boolean horizontalWord) {
        this.apiTiles = apiTiles;
        this.word = word;
        this.points = points;
        this.horizontalWord = horizontalWord;
    }

    public ApiTile[] getApiTiles() {
        return apiTiles;
    }

    public String getWord() {
        return word;
    }

    public int getPoints() {
        return points;
    }

    public boolean isHorizontalWord() {
        return horizontalWord;
    }

    @Override
    public int compareTo(TileMove other) {
        return other.getPoints() - getPoints();
    }
}
