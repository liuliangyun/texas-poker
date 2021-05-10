package com.thoughtwork;

import com.thoughtwork.action.Bet;
import com.thoughtwork.action.Fold;
import com.thoughtwork.action.Pass;
import com.thoughtwork.action.Raise;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTest {
    @Test
    public void player_a_should_be_the_first_active_player () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));

        assertEquals("A", game.getActivePlayer().getName());
        assertEquals(0, game.getPot());
        assertEquals(0, game.getCurrentRound().getCurrentBid());
    }

    @Test
    public void player_b_should_be_next_player_when_player_a_pass () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("B", game.getActivePlayer().getName());
    }

    @Test
    public void player_b_should_be_next_player_when_player_a_bet () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("B", game.getActivePlayer().getName());
    }

    @Test
    public void should_set_current_bid_to_min_wager_when_player_a_bet () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals(1, game.getCurrentRound().getCurrentBid());
        assertEquals(1, game.getPot());
    }

    @Test
    public void should_enter_next_round_when_all_players_pass () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));
        assertEquals(Round.PREFLOP, game.getCurrentRound());

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("D", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals(Round.FLOP, game.getCurrentRound());
    }

    @Test
    public void should_enter_next_round_when_all_players_bet () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));
        assertEquals(Round.PREFLOP, game.getCurrentRound());

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("D", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals(Round.FLOP, game.getCurrentRound());
    }

    @Test
    public void should_not_enter_enter_next_round_when_player_d_raise () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));
        assertEquals(Round.PREFLOP, game.getCurrentRound());

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals("D", game.getActivePlayer().getName());
        game.execute(new Raise(2));

        assertEquals(Round.PREFLOP, game.getCurrentRound());
        assertEquals("A", game.getActivePlayer().getName());
    }

    @Test
    public void should_bet_same_wager_after_someone_raise_the_wager () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));
        assertEquals(Round.PREFLOP, game.getCurrentRound());

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());
        int playerAWager = game.getCurrentRound().getCurrentBid();
        assertEquals(1, playerAWager);

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Bet());
        int playerBWager = game.getCurrentRound().getCurrentBid();
        assertEquals(1, playerBWager);

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Bet());
        int playerCWager = game.getCurrentRound().getCurrentBid();
        assertEquals(1, playerCWager);

        assertEquals("D", game.getActivePlayer().getName());
        game.execute(new Raise(2));

        assertEquals(2, game.getCurrentRound().getCurrentBid());
        int potBeforeABet = game.getPot();

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals(2, game.getCurrentRound().getCurrentBid());
        assertEquals(game.getPot(), potBeforeABet + game.getCurrentRound().getCurrentBid() - playerAWager);
        int potBeforeBBet = game.getPot();

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals(2, game.getCurrentRound().getCurrentBid());
        assertEquals(game.getPot(), potBeforeBBet + game.getCurrentRound().getCurrentBid() - playerBWager);
        int potBeforeCBet = game.getPot();

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Bet());

        assertEquals(Round.FLOP, game.getCurrentRound());
        assertEquals(game.getPot(), potBeforeCBet + game.getCurrentRound().getCurrentBid() - playerCWager);
    }

    @Test
    public void should_player_a_exit_game_when_player_a_fold () {
        Game game = new Game(new Player("A"), new Player("B"), new Player("C"), new Player("D"));
        assertEquals(Round.PREFLOP, game.getCurrentRound());

        assertEquals("A", game.getActivePlayer().getName());
        game.execute(new Fold());

        assertEquals("B", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("C", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals("D", game.getActivePlayer().getName());
        game.execute(new Pass());

        assertEquals(Round.FLOP, game.getCurrentRound());
        assertEquals("B", game.getActivePlayer().getName());
    }
}