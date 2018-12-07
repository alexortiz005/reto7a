package com.example.ticactoe.reto7a;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;



public class TicTacToeGame {

    // The computer's difficulty levels
    public  enum DifficultyLevel {Easy, Harder, Expert};

    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    private char mBoard[] = {' ',' ',' ',' ',' ',' ',' ',' ',' '};
    public static final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public TicTacToeGame() {

        // Seed the random number generator
        mRand = new Random();
    }




    public boolean posEnabled(int pos){
        if(mBoard[pos] == OPEN_SPOT)
            return true;
        return false;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            mBoard[i] = OPEN_SPOT;
        }
    }

    public boolean setMove(char player, int location) {
        if (location >= 0 && location < BOARD_SIZE &&
                mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    /**
     * Return the board occupant (HUMAN_PLAYER, COMPUTER_PLAYER,
     * or OPEN_SPOT) for the given location or '?' if an invalid
     * location is given.
     *
     * @param location - A value between 0 and 8
     * @return The board occupant
     */
    public char getBoardOccupant(int location) {
        if (location >= 0 && location < BOARD_SIZE)
            return mBoard[location];
        return '?';
    }

    /**
     * Check for a winner.  Return a status value indicating the board status.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)
        {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++)
        {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    /** Get the AI's difficulty level.
     *
     * @return The AI's difficulty level.
     */
    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    /** Set the difficulty level.
     *
     * @param difficultyLevel
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    /** Return the best move for the computer to make.
     * You must call setMove() to actually make the computer
     * move to that location.
     * @return The best move for the computer to make.
     */
    public int getComputerMove() {

        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {

            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }

        return move;
    }

    private int getRandomMove() {

        // Generate random move
        int move;
        do {
            move = mRand.nextInt(9);
        } while (mBoard[move] == HUMAN_PLAYER ||
                mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    private int getBlockingMove() {

        // See if there's a move I can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {
            char curr = mBoard[i];

            if (curr != HUMAN_PLAYER && curr != COMPUTER_PLAYER) {
                // What if X moved here?
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;   // Restore space
                    return i;
                }
                else
                    mBoard[i] = OPEN_SPOT;
            }
        }

        // No blocking move is possible
        return -1;
    }

    private int getWinningMove() {

        // See if there's a move I can make to win
        for (int i = 0; i < BOARD_SIZE; i++) {
            char curr = mBoard[i];

            if (curr != HUMAN_PLAYER && curr != COMPUTER_PLAYER) {
                // What if O moved here?
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;   // Restore space
                    return i;
                }
                else
                    mBoard[i] = OPEN_SPOT;
            }
        }

        // No winning move is possible
        return -1;
    }

    public char[] getBoardState(){
        return mBoard;
    }

    public void setBoardState(char[] board){
        this.mBoard = board;
    }

    public DifficultyLevel getmDifficultyLevel() {
        return mDifficultyLevel;
    }




}

