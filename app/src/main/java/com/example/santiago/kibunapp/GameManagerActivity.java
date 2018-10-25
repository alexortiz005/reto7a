package com.example.santiago.kibunapp;

import android.app.NotificationManager;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GameManagerActivity extends AppCompatActivity {
    private Button mNewGameButton;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);
        initializeElements();
    }

    private void initializeElements(){
        mNewGameButton = findViewById(R.id.b_new_game);
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd_MM_yy_hh_mm_ss");
                String gameName= "Juego " + dateFormat.format(date);
                List<String> board = Arrays.asList(new String[]{" "," "," "," "," "," "," "," "," "});
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("games").child(gameName).child("players").setValue(1);
                mDatabase.child("games").child(gameName).child("player_turn").setValue(1);
                mDatabase.child("games").child(gameName).child("board").setValue(board);

                Intent myIntent = new Intent(GameManagerActivity.this, AndroidTicTacToeActivity.class);
                myIntent.putExtra("player", 1);
                GameManagerActivity.this.startActivity(myIntent);
            }
        });
    }
}
