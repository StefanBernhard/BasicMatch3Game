package com.example.match3;

public class PlayerScore implements Comparable<PlayerScore> {

    private String player;
    private int score;

    public PlayerScore (String p, int s){
        player = p;
        score = s;
    }

    @Override
    public int compareTo(PlayerScore o) {
        if (this.score == o.score) {
            return 0;
        } else if (this.score < o.score) {
            return 1;
        } else return -1;
    }

    public String getPlayer(){
        return player;
    }

    public int getScore(){
        return score;
    }
}
