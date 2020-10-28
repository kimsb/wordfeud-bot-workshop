import domain.Board
import domain.Move
import domain.Rack
import wordfeudapi.RestWordFeudClient
import wordfeudapi.domain.Game
import java.lang.Thread.sleep

class Bot {
    private val wfClient: RestWordFeudClient = RestWordFeudClient()
    private val botName = "<dittnavn>bot"

    init {
        wfClient.logon(botName, botName)
        println("Logged in as $botName")
        botLoop()
    }

    private fun botLoop() {
        while (true) {
            acceptInvites()

            val gameIdsMyTurn = wfClient.games
                .filter(Game::isRunning)
                .filter(Game::isMyTurn)
                .map(Game::getId)

            gameIdsMyTurn.forEach {
                val game = wfClient.getGame(it)
                makeMove(game)
            }

            if (gameIdsMyTurn.isEmpty()) {
                sleep(1000)
            }
        }
    }

    private fun acceptInvites() {
        wfClient.status.invitesReceived.forEach {
            //Only accept norwegian bokmål
            if (it.ruleset.apiIntRepresentation == 1) {
                println("Starting game against ${it.inviter}")
                wfClient.acceptInvite(it.id)
            } else {
                wfClient.rejectInvite(it.id)
            }
        }
    }

    private fun makeMove(game: Game) {
        val allMovesSorted = allMovesSorted(game)
        if (allMovesSorted.isEmpty()) {
            if (game.bagCount >= 7) {
                wfClient.swap(game, game.myRack.chars())
            } else {
                wfClient.pass(game)
            }
        } else {
            val tileMove = allMovesSorted.first().toTileMove()
            println("Playing: ${tileMove.word} for ${tileMove.points} points")
            wfClient.makeMove(game, tileMove)
        }
    }

    private fun allMovesSorted(game: Game): List<Move> {
        return Board(wfClient.getBoard(game), game.tiles)
            .findAllMoves(Rack(game.myRack.chars().toList()))
            .sortedByDescending(Move::score)
    }
}
