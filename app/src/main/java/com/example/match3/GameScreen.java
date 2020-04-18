package com.example.match3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.match3.Box.Box;
import com.example.match3.Box.BoxGrid;
import com.example.match3.Events.MyDragShadowBuilder;
import com.example.match3.Events.OnBoxRemovedEventListener;

import java.util.Random;
import java.util.zip.Inflater;

public class GameScreen extends AppCompatActivity {

    private String name = "";
    private int score = 0;
    private TextView scoreText;
    private int turns;
    private TextView turnsText;
    private boolean gameRunning = false;
    private BoxGrid grid;
    private int sizeX;
    private int sizeY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        scoreText = (TextView) findViewById(R.id.txtScoreValue);
        turnsText = (TextView) findViewById(R.id.txtTurnsValue);

        //get name from main screen, if passed
        if (getIntent().hasExtra("name")) {
            TextView n = (TextView) findViewById(R.id.dynTxtNameGameScreen);
            name = getIntent().getExtras().getString("name");
            n.setText(name);
        }

        //on back or game over, return to main screen and pass name + score
        Button btnBack = findViewById(R.id.btnBackToMain);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass name and score to main screen
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                backIntent.putExtra("name", name);
                backIntent.putExtra("score", Integer.toString(score));
                startActivity(backIntent);
            }
        });

        //on Re(Start), start new game
        final Button btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int boxW = ContextCompat.getDrawable(getApplicationContext(), R.drawable.green).getIntrinsicWidth();
                int boxH = ContextCompat.getDrawable(getApplicationContext(), R.drawable.green).getIntrinsicHeight();
                int screenW = findViewById(R.id.gameFrame).getWidth();
                int screenH = findViewById(R.id.gameFrame).getHeight();
                sizeX = screenW / boxW;
                sizeY = screenH / boxH;
                makeNewGame(sizeX, sizeY);

            }
        });

        // set a global layout listener which will be called when the layout pass is completed and the view is drawn
        //necessary, because the sizes for the start action can only be known after drawing is finished
        findViewById(R.id.gameFrame).getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        //Remove the listener before proceeding
                        findViewById(R.id.gameFrame).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        btnRestart.performClick();
                    }
                }
        );

        // set drag & drop functionality
        findViewById(R.id.gameFrame).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Create a new ClipData.
                // This is done in two steps to provide clarity. The convenience method
                // ClipData.newPlainText() can create a plain text ClipData in one step.
                // Create a new ClipData.Item from the ImageView object's tag
                ClipData.Item item = new ClipData.Item(v.getTag().toString());

                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This will create a new ClipDescription object within the
                // ClipData, and set its MIME type entry to "text/plain"
                ClipData dragData = new ClipData(
                        v.getTag().toString(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);

                // Instantiates the drag shadow builder.
                View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);

                // Starts the drag

                v.startDrag(dragData,  // the data to be dragged
                        myShadow,  // the drag shadow builder
                        null,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );

                return false;
            }
        });

    }

    //makes a ned game board of size X by Y
    private void makeNewGame(int X, int Y) {
        if (!gameRunning) {
            score = 0;
            updateScoreText();
            turns = 10;
            updateTurnsText();
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.gameFrame);
            BoxGrid newGrid = new BoxGrid(X, Y);
            grid = newGrid;
            newGrid.setOnBoxRemovedEventListener(new OnBoxRemovedEventListener() {
                @Override
                public void onBoxRemoved(int points) {
                    score += points;
                    updateScoreText();
                    turns--;
                    updateTurnsText();
                    if (turns <= 0){
                        Toast scoreToast = Toast.makeText(getApplicationContext(), "Your score: " + score, Toast.LENGTH_LONG);
                        scoreToast.show();
                        Button btnBack = findViewById(R.id.btnBackToMain);
                        btnBack.performClick();
                    }
                }
            });
            for (int x = 0; x <= X - 1; x++) {
                for (int y = 0; y <= Y - 1; y++) {
                    Box newBox = new Box(x, y, cl);
                    newGrid.addBox(newBox);
                }
            }
            gameRunning = true;
        } else { //if game is currently running
            restartGame();
        }
    }

    private void restartGame() {
        clearUpGame();
        makeNewGame(sizeX, sizeY);
    }

    private void updateScoreText(){
        scoreText.setText(score + "");
    }

    private void updateTurnsText(){
        turnsText.setText(turns + "");
    }

    //clears up a running game for restart
    private void clearUpGame(){
        grid.clear();
        grid = null;
        gameRunning = false;
    }
}
