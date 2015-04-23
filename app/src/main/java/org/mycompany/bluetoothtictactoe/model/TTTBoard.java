package org.mycompany.bluetoothtictactoe.model;

/**
 * Created by Saylee on 4/19/2015.
 */
public class TTTBoard {
    private char board[];
    private int blankCells;

    public TTTBoard(){
        initializeBoard();
        blankCells = 9;
    }
    private void initializeBoard(){
        board = new char[9];
        for (int i=0;i<9;i++){
            board[i]=' ';
        }
    }
    public char setMove(char symbol,int location){
        board[location] = symbol;
        blankCells--;
        return checkWinner(symbol);
    }
    public char getSymbol(int position){
        return board[position];
    }
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
        return result;
    }
    public boolean noWinner(){
        if (blankCells == 0)
            return true;
        return false;
    }
}
