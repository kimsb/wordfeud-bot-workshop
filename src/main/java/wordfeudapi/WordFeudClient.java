package wordfeudapi;

import wordfeudapi.domain.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Pierre Ingmansson
 */
public interface WordFeudClient {
    void useSessionId(String sessionId);

    User logon(String email, String password);

    String invite(String username, RuleSet ruleset, BoardType boardType);

    int acceptInvite(long inviteId);

    String rejectInvite(long inviteId);

    Notifications getNotifications();

    Game[] getGames();

    Game getGame(long gameId);

    ApiBoard getBoard(Game game);

    ApiBoard getBoard(int boardId);

    Status getStatus();

    PlaceResult makeMove(Game game, TileMove tileMove);

    PlaceResult place(long gameId, RuleSet ruleset, ApiTile[] apiTiles, char[] word);

    String pass(Game game);

    String pass(long gameId);

    SwapResult swap(Game game, char[] tiles);

    SwapResult swap(long gameId, char[] tiles);

    String chat(Game game, String message);

    String chat(long gameId, String message);

    String getChatMessages(Game game);

    String getChatMessages(long gameId);

    String uploadAvatar(File file) throws IOException;

    String uploadAvatar(byte[] imageData);

    String createAccount(String username, String email, String password);
}
