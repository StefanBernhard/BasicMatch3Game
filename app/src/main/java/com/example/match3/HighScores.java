package com.example.match3;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighScores extends ArrayList<PlayerScore> {

    List<PlayerScore> playerScores;
    Context context;

    public HighScores(Context c) {
        playerScores = new ArrayList<PlayerScore>();
        context = c;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        if (playerScores.size() > 0) {
            for (PlayerScore ps : playerScores) {
                sb.append(ps.getPlayer());
                sb.append("§§");
            }
            sb.append("%%");
            for (PlayerScore ps : playerScores) {
                sb.append(Integer.toString(ps.getScore()));
                sb.append("§§");
            }
        }

        return sb.toString();
    }

    public boolean add(String name, String score){
        PlayerScore ps = new PlayerScore(name.replaceAll("[^A-Za-z0-9]",""), Integer.parseInt(score));
        playerScores.add(ps);
        return true;
    }

    public void sort(){
        PlayerScore[] psa = playerScores.toArray(new PlayerScore[playerScores.size()]);
        Arrays.sort(psa);
        playerScores.clear();
        for (PlayerScore ps : psa) {
            playerScores.add(ps);
        }
    }

    public String[] getPlayerArray(){
        String[] pa = new String[playerScores.size()];
        int ind = 0;
            for (PlayerScore ps : playerScores) {
                if (ps != null) {
                    pa[ind] = ps.getPlayer();
                }
                ind++;
            }
        return pa;
    }

    public String[] getScoreArray(){
        String[] pa = new String[playerScores.size()];
        int ind = 0;
            for (PlayerScore ps : playerScores) {
                if (ps != null) {
                    pa[ind] = Integer.toString(ps.getScore());
                }
                ind++;
            }
        return pa;
    }

    public void save(){
        writeToFile(toString());
    }

    public void load(){
        String strg = readFromFile();
        if (strg.contains("%%")) {
            String[] playersAndScores = strg.split("%%");
            String[] players = playersAndScores[0].split("§§");
            String[] scores = playersAndScores[1].split("§§");
            for (int i = 0; i < players.length; i++) {
                playerScores.add(new PlayerScore(players[i], Integer.parseInt(scores[i])));
            }
        } else return;
    }

    public void clear(){
        writeToFile("");
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("highscores.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("highscores.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
