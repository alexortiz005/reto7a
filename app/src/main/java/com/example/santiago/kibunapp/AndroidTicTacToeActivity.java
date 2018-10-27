package com.example.santiago.kibunapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class AndroidTicTacToeActivity extends AppCompatActivity {

   // static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private char player;
    private String gameName;
    private DatabaseReference mDatabase;

    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    private char mTurn;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mResultsTextView;

    boolean mGameOver = false;
    boolean mSoundOn = true;

    private BoardView mBoardView;
    private SharedPreferences mPrefs;

    int humanWins, androidWins , ties;
    int turn;
    int selected;



    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            //Check if position is enbales

            if (!mGameOver && mTurn == player && posEnabled(pos)) {
                setMove(pos);
                checkWinner();
                //Handler handler = new Handler();
                /*handler.postDelayed(new Runnable() {
                    public void run() {
                        checkWinner();
                    }
                }, 1000);*/


            }

            return false;


            /*if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                // If no winner yet, let the computer make a move


                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                Log.d("winner: ", Integer.toString(winner));
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    Log.d("borad", Arrays.toString(mGame.getBoardState()));
                    winner = mGame.checkForWinner();
                    Log.d("winner2: ", Integer.toString(winner));
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    ties++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    humanWins++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                }
                else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidWins++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                }

            }*/

// So we aren't notified of continued events when finger is moved
            //return false;
        }
    };

    void checkWinner(){
        int winner = mGame.checkForWinner();
        Log.d("winner: ", Integer.toString(winner));
        /*if (winner == 0) {
            mInfoTextView.setText("Turno de " + Character.toString(mTurn));
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            Log.d("borad", Arrays.toString(mGame.getBoardState()));
            winner = mGame.checkForWinner();
            Log.d("winner2: ", Integer.toString(winner));
        }*/

        if (winner == 0)
            if(mTurn == 'N'){
                mInfoTextView.setText("Esperando a otro Jugador");
            }else{
                mInfoTextView.setText("Turno de " + Character.toString(mTurn));
            }
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            if(!mGameOver) ties++;
            mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
            mGameOver = true;

        }
        else if (winner == 2) {
            //Log.d("ganooo  : ", "asdfdsf");
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            String msg = mPrefs.getString("victory_message", defaultMessage);
            //Log.d("win msg : ", msg);
            mInfoTextView.setText(Character.toString(TicTacToeGame.HUMAN_PLAYER) + " ganador!");
            if(!mGameOver)humanWins++;
            mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
            mGameOver = true;

        }
        else{
            mInfoTextView.setText(Character.toString(TicTacToeGame.COMPUTER_PLAYER) + " ganador!");
            if(!mGameOver)androidWins++;
            mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
            mGameOver = true;

        }
    }



    private void setMove(int location) {
        //mGame.setMove(player, location);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("games").child(gameName).child("board").child(Integer.toString(location)).setValue(Character.toString(player));

        if (TicTacToeGame.HUMAN_PLAYER == player) {
            mDatabase.child("games").child(gameName).child("player_turn").setValue(Character.toString(TicTacToeGame.COMPUTER_PLAYER));
            if(mSoundOn)
                mHumanMediaPlayer.start(); // Play the sound effect
        } else {
            mDatabase.child("games").child(gameName).child("player_turn").setValue(Character.toString(TicTacToeGame.HUMAN_PLAYER));
            if(mSoundOn)
                mComputerMediaPlayer.start(); // Play the sound effect
        }
        mBoardView.invalidate();
       /* if (player == TicTacToeGame.COMPUTER_PLAYER) {
            // EXTRA CHALLENGE!
            // Make the computer move after a delay of 1 second
            final int loc = location;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, loc);
                    mBoardView.invalidate();   // Redraw the board
                    mComputerMediaPlayer.start();

                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mTurn = TicTacToeGame.HUMAN_PLAYER;
                        mInfoTextView.setText(R.string.turn_human);
                    }

                }
            }, 1000);
            return true;
        }
        else if (mGame.setMove(TicTacToeGame.HUMAN_PLAYER, location)) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mBoardView.invalidate();   // Redraw the board
            mHumanMediaPlayer.start();
            return true;
        }

        return false;
*/
        /*if (player == TicTacToeGame.COMPUTER_PLAYER) {
            mComputerMediaPlayer.start();
        }else {
            mHumanMediaPlayer.start();
        }

            if (mGame.setMove(player, location)) {
            mBoardView.invalidate();   // Redraw the board
            return true;
        }
        return false;*/

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        Bundle b = getIntent().getExtras();
        player = b.getChar("player");
        gameName = b.getString("gameName");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mResultsTextView = (TextView) findViewById(R.id.results);
        TextView identity = (TextView) findViewById(R.id.identity);
        identity.setText("Te corresponden las " + Character.toString(player));
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setBoardColor(mPrefs.getInt("board_color", R.color.defaultBoardColor));
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        addDataBaseListener();
       // addDataBaseListener();
        //mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.valueOf(mPrefs.getString("difficulty", TicTacToeGame.DifficultyLevel.Expert.name())));
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        if (savedInstanceState == null) {
            startNewGame();
            mGameOver = false;
            humanWins = mPrefs.getInt("humanWins", 0);
            androidWins = mPrefs.getInt("androidWins", 0);
            ties = mPrefs.getInt("ties", 0);

            turn = 0;
        }
        else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            humanWins = savedInstanceState.getInt("humanWins");
            androidWins = savedInstanceState.getInt("androidWins");
            ties = savedInstanceState.getInt("ties");
            turn = savedInstanceState.getInt("turn");
            selected = savedInstanceState.getInt("selected");
            mTurn = savedInstanceState.getChar("mTurn");

        }
        displayScores();


    }

   private void addDataBaseListener(){
        //DatabaseReference players = mDatabase.child("games").child(gameName).child("players");
        DatabaseReference player_turn = mDatabase.child("games").child(gameName).child("player_turn");
        DatabaseReference board = mDatabase.child("games").child(gameName).child("board");


        player_turn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTurn = dataSnapshot.getValue(String.class).charAt(0);
                checkWinner();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("NOTIFI", "Failed to read value.", error.toException());
            }
        });

        board.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> gti = new GenericTypeIndicator<List<String>>() {};
                List<String> newBoardList = dataSnapshot.getValue(gti);
                char newBoard[] = new char[newBoardList.size()];
                for(int i = 0; i < newBoardList.size(); i++){
                    newBoard[i] = newBoardList.get(i).charAt(0);
                }
                mGame.setBoardState(newBoard);
                mBoardView.invalidate();
                checkWinner();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("NOTIFI", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mBoardView.setBoardColor(mPrefs.getInt("board_color", R.color.defaultBoardColor));
            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("humanWins", humanWins);
        outState.putInt("androidWins", androidWins);
        outState.putInt("ties", ties);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putInt("turn", turn);
        outState.putInt("selected", selected);
        outState.putChar("mTurn", mTurn);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.reset_socores:
                humanWins = 0;
                ties = 0;
                androidWins = 0;
                updateDisplay();
                return true;
            case R.id.about:
                showDialog(10);

                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            /*case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.
                selected = -1;
                if(mGame.getDifficultyLevel() == TicTacToeGame.DifficultyLevel.Easy)
                    selected = 0;
                if(mGame.getDifficultyLevel() == TicTacToeGame.DifficultyLevel.Harder)
                    selected = 1;
                if(mGame.getDifficultyLevel() == TicTacToeGame.DifficultyLevel.Expert)
                    selected = 2;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                if(item == 0){
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                }
                                if(item == 1){
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                }
                                if(item == 2){
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                }


                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;
*/
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case 10:

                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
        }



        return dialog;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("humanWins", humanWins);
        ed.putInt("androidWins", androidWins);
        ed.putInt("ties", ties);
        ed.putString("difficulty", mGame.getmDifficultyLevel().name());
        ed.commit();
        super.onStop();



    }


    private void displayScores() {
        mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
    }



    private void startNewGame(){

        mGame.clearBoard();
        mBoardView.invalidate();

        mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
        mGameOver = false;
        turn++;
        if(turn % 2 == 0){
            //int move = mGame.getComputerMove();
            //setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            //TODO
            mInfoTextView.setText(R.string.turn_human);
        }else{
            mInfoTextView.setText(R.string.first_human);
        }

    }




    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.s1);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.s2);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }



    /*private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    ties++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                    }
                else if (winner == 2) {
                    Log.d("ganooo  : ", "asdfdsf");
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    String msg = mPrefs.getString("victory_message", defaultMessage);
                    Log.d("win msg : ", msg);
                    mInfoTextView.setText(msg);
                    humanWins++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                    }
                else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidWins++;
                    mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
                    mGameOver = true;

                }

            }
        }
    }*/

    private void updateDisplay(){
        mResultsTextView.setText("Human: "+humanWins+" Ties: "+ ties + " PC: " + androidWins);
    }

    boolean posEnabled(int pos){
        return mGame.posEnabled(pos);
    }

}

