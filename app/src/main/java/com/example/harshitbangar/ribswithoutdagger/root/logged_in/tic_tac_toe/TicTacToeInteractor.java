/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.harshitbangar.ribswithoutdagger.root.logged_in.tic_tac_toe;

import android.support.annotation.Nullable;
import com.example.harshitbangar.ribswithoutdagger.root.UserName;
import com.example.harshitbangar.ribswithoutdagger.root.logged_in.tic_tac_toe.Board.MarkerType;
import com.uber.rib.core.Bundle;
import com.uber.rib.core.NonInjectableInteractor;
import com.uber.rib.core.RibInteractor;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Coordinates Business Logic for {@link TicTacToeScope}.
 */
@RibInteractor
public class TicTacToeInteractor
    extends NonInjectableInteractor<TicTacToeInteractor.TicTacToePresenter, TicTacToeRouter> {

  Board board;
  Listener listener;
  TicTacToePresenter presenter;
  UserName playerOne;
  UserName playerTwo;

  private MarkerType currentPlayer = MarkerType.CROSS;

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);

    presenter
        .squareClicks()
        .subscribe(
            new Consumer<BoardCoordinate>() {
              @Override
              public void accept(BoardCoordinate xy) throws Exception {
                if (board.cells[xy.getX()][xy.getY()] == null) {
                  if (currentPlayer == MarkerType.CROSS) {
                    board.cells[xy.getX()][xy.getY()] = MarkerType.CROSS;
                    board.currentRow = xy.getX();
                    board.currentCol = xy.getY();
                    presenter.addCross(xy);
                    currentPlayer = MarkerType.NOUGHT;
                  } else {
                    board.cells[xy.getX()][xy.getY()] = MarkerType.NOUGHT;
                    board.currentRow = xy.getX();
                    board.currentCol = xy.getY();
                    presenter.addNought(xy);
                    currentPlayer = MarkerType.CROSS;
                  }
                }
                if (board.hasWon(MarkerType.CROSS)) {
                  presenter.setPlayerWon(playerOne.getUserName());
                  listener.gameWon(playerOne);
                } else if (board.hasWon(MarkerType.NOUGHT)) {
                  presenter.setPlayerWon(playerTwo.getUserName());
                  listener.gameWon(playerTwo);
                } else if (board.isDraw()) {
                  presenter.setPlayerTie();
                  listener.gameWon(null);
                } else {
                  updateCurrentPlayer();
                }
              }
            });
    updateCurrentPlayer();
  }

  private void updateCurrentPlayer() {
    if (currentPlayer == MarkerType.CROSS) {
      presenter.setCurrentPlayerName(playerOne.getUserName());
    } else {
      presenter.setCurrentPlayerName(playerTwo.getUserName());
    }
  }


  /**
   * Presenter interface implemented by this RIB's view.
   */
  interface TicTacToePresenter {
    Observable<BoardCoordinate> squareClicks();

    void setCurrentPlayerName(String currentPlayer);

    void setPlayerWon(String playerName);

    void setPlayerTie();

    void addCross(BoardCoordinate xy);

    void addNought(BoardCoordinate xy);
  }

  public interface Listener {

    /**
     * Called when the game is over.
     *
     * @param winner player that won, or null if it's a tie.
     */
    void gameWon(UserName winner);
  }
}
