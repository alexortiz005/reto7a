package com.example.ticactoe.reto7a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GameManagerActivity extends AppCompatActivity {
    private Button mNewGameButton;
    private DatabaseReference mDatabase;
    private LinearLayout gameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);
        initializeElements();
    }

    private void initializeElements(){
        mNewGameButton = findViewById(R.id.b_new_game);
        gameList = (LinearLayout) findViewById(R.id.ll_btns);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd_MM_yy_hh_mm_ss");
                String gameName= "Juego " + dateFormat.format(date);
                List<String> board = Arrays.asList(new String[]{" "," "," "," "," "," "," "," "," "});
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("games").child(gameName).child("players").setValue(1);
                mDatabase.child("games").child(gameName).child("player_turn").setValue("N");
                mDatabase.child("games").child(gameName).child("board").setValue(board);
                mDatabase.child("games").child(gameName).child("init_turn").setValue(-1);
                mDatabase.child("games").child(gameName).child("game_over").setValue(false);
                mDatabase.child("games").child(gameName).child("scores").child("x_wins").setValue(0);
                mDatabase.child("games").child(gameName).child("scores").child("o_wins").setValue(0);
                mDatabase.child("games").child(gameName).child("scores").child("ties").setValue(0);

                Intent myIntent = new Intent(GameManagerActivity.this, AndroidTicTacToeActivity.class);
                myIntent.putExtra("player", 'X');
                myIntent.putExtra("gameName", gameName);
                GameManagerActivity.this.startActivity(myIntent);
            }
        });
        showGameList();
    }

    private void showGameList(){
        DatabaseReference games = mDatabase.child("games");


        games.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gameList.removeAllViews();
                for(DataSnapshot gameSnapshot: dataSnapshot.getChildren()){
                    int players = gameSnapshot.child("players").getValue(Integer.class);
                    final String gameName = gameSnapshot.getKey();
                    if(players < 2){
                        Button btn = new Button(GameManagerActivity.this);
                        btn.setText(gameName);
                        gameList.addView(btn);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("games").child(gameName).child("players").setValue(2);
                                mDatabase.child("games").child(gameName).child("player_turn").setValue("X");

                                Intent myIntent = new Intent(GameManagerActivity.this, AndroidTicTacToeActivity.class);
                                myIntent.putExtra("player", 'O');
                                myIntent.putExtra("gameName", gameName);
                                GameManagerActivity.this.startActivity(myIntent);
                            }
                        });

                    }
                    Log.d("asdfg", gameName);

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("NOTIFI", "Failed to read value.", error.toException());
            }
        });

    }
}
