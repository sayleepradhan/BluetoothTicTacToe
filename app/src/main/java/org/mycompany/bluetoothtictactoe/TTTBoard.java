package org.mycompany.bluetoothtictactoe;

/**
 * Created by Saylee on 4/19/2015.
 */
public class TTTBoard {
    private char mboard[];
    private int blankCells;

    public TTTBoard(){
        initializeBoard();
        blankCells = 9;
    }
    private void initializeBoard(){
        mboard = new char[9];
        for (int i=0;i<9;i++){
            mboard[i]=' ';
        }
    }
    public char setMove(char symbol,int location){
        mboard[location] = symbol;
        blankCells--;
        return checkWinner(symbol);
    }
    public char getSymbol(int position){
        return mboard[position];
    }
    public char checkWinner(char symbol){
        char result = ' ';
        boolean flag = false;

        if ((mboard[0]==symbol && mboard[1]==symbol && mboard[2]==symbol) ||
                (mboard[3]==symbol && mboard[4]==symbol && mboard[5]==symbol) ||
                (mboard[6]==symbol && mboard[7]==symbol && mboard[8]==symbol) ||
                (mboard[0]==symbol && mboard[3]==symbol && mboard[6]==symbol) ||
                (mboard[1]==symbol && mboard[4]==symbol && mboard[7]==symbol) ||
                (mboard[2]==symbol && mboard[5]==symbol && mboard[8]==symbol) ||
                (mboard[0]==symbol && mboard[4]==symbol && mboard[8]==symbol) ||
                (mboard[2]==symbol && mboard[4]==symbol && mboard[6]==symbol)
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
