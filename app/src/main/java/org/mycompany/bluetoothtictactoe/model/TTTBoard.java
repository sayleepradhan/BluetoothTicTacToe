package org.mycompany.bluetoothtictactoe.model;

/**
 * Created by Saylee Pradhan (sap140530) on 4/20/2015.
 * Course: CS6360.001
 *
 * This class creates and initialize the game board,
 * set the particular player's symbol to board as game
 * moves on and player who won the game.
 */
public class TTTBoard {
    private char board[];
    private int blankCells;

    /**
     * This is a constructor to initialize the game board.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public TTTBoard(){
        initializeBoard();
        blankCells = 9;
    }

    /**
     * This method creates the empty board.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    private void initializeBoard(){
        board = new char[9];
        for (int i=0;i<9;i++){
            board[i]=' ';
        }
    }

    /**
     * This method sets the particular player's symbol to board.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param symbol
     *          it is 'O' or 'X' symbol
     * @param location
     *          it is a position on board
     *
     * @return char checkWinner(symbol)
     */
    public char setMove(char symbol,int location){
        board[location] = symbol;
        blankCells--;
        return checkWinner(symbol);
    }

    /**
     * This method returns the symbol at particular board position.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param position
     *         board's position
     *
     * @return char board[position]
     */
    public char getSymbol(int position){
        return board[position];
    }

    /**
     * This method returns the symbol of player who won.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param symbol
     *          it is 'O' or 'X' symbol
     *
     * @return char result
     */
    public char checkWinner(char symbol){
        char result = ' ';
        boolean flag = false;

        if ((board[0]==symbol && board[1]==symbol && board[2]==symbol) ||
                (board[3]==symbol && board[4]==symbol && board[5]==symbol) ||
                (board[6]==symbol && board[7]==symbol && board[8]==symbol) ||
                (board[0]==symbol && board[3]==symbol && board[6]==symbol) ||
                (board[1]==symbol && board[4]==symbol && board[7]==symbol) ||
                (board[2]==symbol && board[5]==symbol && board[8]==symbol) ||
                (board[0]==symbol && board[4]==symbol && board[8]==symbol) ||
                (board[2]==symbol && board[4]==symbol && board[6]==symbol)
                )
            flag = true;
        if (flag)
            result = symbol;
        else if (noWinner())
            result = 'N';
        return result;
    }

    /**
     * This method returns true when there is no blank space on board.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @return boolean
     */
    public boolean noWinner(){
        if (blankCells == 0)
            return true;
        return false;
    }
}
