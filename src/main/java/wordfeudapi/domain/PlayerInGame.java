package wordfeudapi.domain;

import wordfeudapi.util.ArrayUtil;

import static wordfeudapi.util.DateUtil.format;

/**
 * @author Pierre Ingmansson
 */
public class PlayerInGame {
    private String username;
    private int position;
    private int score;
    private int id;
    private double avatar_updated;
    private String[] rack;

    public PlayerInGame(final String username, final int position, final int score, final int id, final double avatarUpdated, final String[] rack) {
        this.username = username;
        this.position = position;
        this.score = score;
        this.id = id;
        this.avatar_updated = avatarUpdated;
        this.rack = rack;
    }

    public PlayerInGame() {
    }

    public String getUsername() {
        return username;
    }

    public int getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public double getAvatarUpdated() {
        return avatar_updated;
    }

    public char[] getRack() {
        if (rack == null) {
            throw new IllegalStateException("Player " + username + " did not have a rack");
        }
        return ArrayUtil.convertToCharArray(rack);
    }

    @Override
    public String toString() {
        return "PlayerInGame{" +
                "username='" + username + '\'' +
                ", position=" + position +
                ", score=" + score +
                ", id=" + id +
                ", avatarUpdated=" + format(avatar_updated) +
                '}';
    }
}
