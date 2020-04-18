package com.example.match3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView listHighscores;
    private HighScores highScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listHighscores = (ListView) findViewById(R.id.listHighscores);
        highScores = new HighScores(getApplicationContext());
        highScores.load();

        //get name and score from game screen, if passed
        if (getIntent().hasExtra("name") && getIntent().hasExtra("score")) {
            String name = getIntent().getExtras().getString("name");
            TextView vTxt = findViewById(R.id.dynTxtNameGameScreen);
            vTxt.setText(name);
            EditText eTxt = findViewById(R.id.editTxtName);
            eTxt.setText(name);
            String score = getIntent().getExtras().getString("score");
            highScores.add(name, score);
            highScores.save();
        }


        //to combine the list with the strings to populate, we need a separate layout .xml file, and
        //we need also a new class that adapts the items, PlayerAdapter
        //String[] playersArr = players.toArray(new String[players.size()]);
        //String[] highscoresArr = highscores.toArray(new String[highscores.size()]);
        //PlayerScore[] hsTable = new PlayerScore[playersArr.length];
        //for (int i=0; i<playersArr.length; i++) {
        //    hsTable[i] = new PlayerScore(playersArr[i], Integer.parseInt(highscoresArr[i]));
        //}
        //Arrays.sort(hsTable);
        //String[] playersTable = new String[3];
        //String[] highscoresTable = new String[3];
        //for (int i=0; i<3 && i < hsTable.length; i++){
        //    playersTable[i] = hsTable[i].getPlayer();
        //    highscoresTable[i] = Integer.toString(hsTable[i].getScore());
        //}

        highScores.sort();
        PlayerAdapter playerAdapter = new PlayerAdapter(this, highScores.getPlayerArray(), highScores.getScoreArray());
        listHighscores.setAdapter(playerAdapter);


        // Go to Game Screen
        Button btnStartGame = findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the name
                EditText eTxt = findViewById(R.id.editTxtName);
                String txt = eTxt.getText().toString();

                //if name is empty, prompt user to enter name
                if (txt.equals("")) {
                    Toast enterName = Toast.makeText(getApplicationContext(), "Please enter your name!", Toast.LENGTH_LONG);
                    enterName.show();
                    return;
                }

                //goto game screen and pass name
                Intent startIntent = new Intent(getApplicationContext(), GameScreen.class); //create a new Intent and int he application context, have it create a new gamescreen class instance
                startIntent.putExtra("name", txt); //as an extra, attach the name
                startActivity(startIntent); //start that new Activity through the Intent
            }
        });

        // Clear Highscores
        Button btnClearHighscores = findViewById(R.id.btnClearHighscores);
        btnClearHighscores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highScores.clear();
                listHighscores.setAdapter(null);
            }
        });
    }


}
