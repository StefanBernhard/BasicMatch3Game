package com.example.match3.Box;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.match3.Events.OnPopEventListener;
import com.example.match3.R;

/*
A box for the game. A box is a box and doesn't need to know about anything outside.
A box always gets instantiated with a random color.
A box can be clicked. When clicked, it will call an event, so someone else can do something to the box.
 */
public class Box extends AppCompatActivity {

    //box shall be a clickable button
    private ImageButton ib;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pop();
        }
    };

    //some aux fields
    private ConstraintLayout constraint;
    private Drawable colour;
    private int colourID;
    private int posX;
    private int posY;
    private int w;
    private int h;

    //Event handling for the pop action
    private OnPopEventListener mOnPopEventListener;

    public Box(int X, int Y, ConstraintLayout cl){
        posX = X;
        posY = Y;
        constraint = cl;
        Context context = constraint.getContext();

        // Initialize a new ImageView widget
        ib = new ImageButton(context);
        ib.setId(View.generateViewId());
        // Set an image for ImageView based on random image
        Drawable red = ContextCompat.getDrawable(context, R.drawable.red);
        Drawable green = ContextCompat.getDrawable(context, R.drawable.green);
        Drawable blue = ContextCompat.getDrawable(context, R.drawable.blue);
        Drawable[] cols = new Drawable[] {red, green, blue};
        colourID = (int)(System.nanoTime() % cols.length);
        colour = cols[colourID];
        ib.setImageDrawable(colour);
        ib.setPadding(0, 0, 0, 0); //remove border
        // add the ImageView to layout
        constraint.addView(ib);
        // leave invisible, as boxgrid sets visibility later, when box is added to grid
        setBoxVisible(false);

        //set the box at the correct position on screen
        w = colour.getIntrinsicWidth();
        h = colour.getMinimumHeight();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraint);
        constraintSet.connect(ib.getId(),ConstraintSet.TOP,constraintSet.PARENT_ID,constraintSet.TOP);
        constraintSet.connect(ib.getId(),ConstraintSet.LEFT,constraintSet.PARENT_ID,constraintSet.LEFT);
        constraintSet.setMargin(ib.getId(),ConstraintSet.TOP, (h * posY)+15);
        constraintSet.setMargin(ib.getId(),ConstraintSet.LEFT, (w * posX)+15);
        constraintSet.applyTo(constraint);

        //attach the onClickListener
        ib.setOnClickListener(mOnClickListener);
    }

    //getters and setters
    public void setOnPopEventListener(OnPopEventListener eventListener){
        mOnPopEventListener = eventListener;
    }

    public int getX(){
        return posX;
    }

    public int getY(){
        return posY;
    }

    public int getColourID(){
        return colourID;
    }

    public ConstraintLayout getConstraint(){
        return constraint;
    }

    public void setBoxVisible(boolean bool){
        if (bool == false) {
            ib.setVisibility(View.INVISIBLE);
        }
        else {ib.setVisibility(View.VISIBLE);}
    }

    //methods

    //invokes the pop event
    private void pop(){
        //call the event
        if (mOnPopEventListener != null){
            mOnPopEventListener.onPopBox(this);
        }
    }


}
