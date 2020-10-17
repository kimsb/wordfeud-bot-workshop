import domain.BoardDO;
import domain.MoveDO;
import wordfeudapi.RestWordFeudClient;
import wordfeudapi.domain.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Bot {

    private final RestWordFeudClient botClient = new RestWordFeudClient();

    Bot() {
        botClient.logon("moominbottest", "moominbottest");
        botLoop();
    }

    private void botLoop() {
        while (true) {
            acceptInvites();
            final List<Long> myTurnGameIds = getMyTurnGameIds();

            if (myTurnGameIds.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (Long id : myTurnGameIds) {
                Game game = botClient.getGame(id);
                if (game.getRuleset().getApiIntRepresentation() != 1) {
                    botClient.pass(id);
                } else {
                    makeBestMove(id, game);
                }
            }
        }
    }

    private void makeBestMove(Long id, Game game) {
        final List<TileMove> bestMoves = findBestMoves(game);
        if (bestMoves.isEmpty()) {
            if (game.getBagCount() >= 7) {
                botClient.swap(id, game.getMyRack().chars());
            } else {
                botClient.pass(id);
            }
        } else {
            TileMove tileMove = bestMoves.get(bestMoves.size() - 1);
            System.out.println("Legger: " + tileMove.getWord());
            botClient.makeMove(game, tileMove);
        }
    }

    private List<Long> getMyTurnGameIds() {
        return Stream.of(botClient.getGames())
                .filter(game -> game.isRunning() && game.isMyTurn())
                .map(Game::getId)
                .collect(Collectors.toList());
    }

    private void acceptInvites() {
        //TODO: bare accept norsk-bokmål
        Stream.of(botClient.getStatus().getInvitesReceived())
                .forEach(invite -> {
                    /*if (invite.getRuleset().getApiIntRepresentation() != 1) {
                        botClient.rejectInvite(invite.getId());
                    }*/
                    final long gameId = botClient.acceptInvite(invite.getId());
                });
    }

    private List<TileMove> findBestMoves(Game game) {
        Rack rack = game.getMyRack();

        Board board = botClient.getBoard(game);
        Tile[] tiles = game.getTiles();

        ArrayList<MoveDO> allMoves = new MoveFinder(board).findAllMoves(new BoardDO(tiles), new String(rack.chars()));

        return allMoves.stream()
                .map(MoveDO::toTileMove)
                .sorted(Comparator.comparingInt(TileMove::getPoints))
                .collect(Collectors.toList());
    }

}
