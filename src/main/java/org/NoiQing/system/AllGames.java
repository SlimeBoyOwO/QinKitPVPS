package org.NoiQing.system;

import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.mainGaming.Game;

public class AllGames {
    private final Game game;
    private final AllayGame allayGame;
    public AllGames() {
        game = new Game(QinKitPVPS.getPlugin());
        allayGame = new AllayGame();
    }
    public Game getKitGame() {return game;}
    public AllayGame getAllayGame() {return allayGame;}
}
