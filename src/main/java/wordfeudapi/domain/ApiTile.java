package wordfeudapi.domain;

/**
 * @author Pierre Ingmansson
 */
public class ApiTile {
    public static final char WILDCARD = '*';

    private final int x;
    private final int y;
    private final String character;
    private final boolean wildcard;

    public ApiTile(final int x, final int y, final char character, final boolean wildcard) {
        this.x = x;
        this.y = y;
        this.character = character + "";
        this.wildcard = wildcard;
    }

    public ApiTile(final Object[] tile) {
        x = ((Double) tile[0]).intValue();
        y = ((Double) tile[1]).intValue();
        character = (String) tile[2];
        wildcard = (Boolean) tile[3];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getCharacter() {
        return character.charAt(0);
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public static Object[][] convert(final ApiTile[] apiTiles) {
        final Object[][] ret = new Object[apiTiles.length][4];
        for (int i = 0, tilesLength = apiTiles.length; i < tilesLength; i++) {
            final ApiTile apiTile = apiTiles[i];
            ret[i] = new Object[]{apiTile.getX(), apiTile.getY(), apiTile.getCharacter(), apiTile.isWildcard()};
        }
        return ret;
    }

    @Override
    public String toString() {
        return "['" + character + "'" + (wildcard ? " *" : "") + " (" + x + ", " + y + ")]";
    }
}
