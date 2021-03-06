package com.example.match3.Box;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.match3.Events.MyDragShadowBuilder;
import com.example.match3.Events.OnBoxDraggedEventListener;
import com.example.match3.Events.OnPopEventListener;
import com.example.match3.R;

/*
A box for the game. A box is a box and doesn't need to know about anything outside.
A box always gets instantiated with a random color.
A box can be clicked. When clicked, it will call an event, so someone else can do something to the box.
 */

public class Box extends AppCompatImageButton {

    //box shall be a clickable button
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

    //event handling for the drop action
    private OnBoxDraggedEventListener mOnBoxDraggedEventListener;

    public Box(int X, int Y, ConstraintLayout cl){
        super(cl.getContext());
        posX = X;
        posY = Y;
        constraint = cl;
        Context context = constraint.getContext();

        // Initialize a new ImageView widget
        setId(View.generateViewId());
        // Set an image for ImageView based on random image
        Drawable red = ContextCompat.getDrawable(context, R.drawable.red);
        Drawable green = ContextCompat.getDrawable(context, R.drawable.green);
        Drawable blue = ContextCompat.getDrawable(context, R.drawable.blue);
        Drawable[] cols = new Drawable[] {red, green, blue};
        colourID = (int)(System.nanoTime() % cols.length);
        colour = cols[colourID];
        setImageDrawable(colour);
        setPadding(0, 0, 0, 0); //remove border
        // add the ImageView to layout
        constraint.addView(this);
        // leave invisible, as boxgrid sets visibility later, when box is added to grid
        setBoxVisible(false);

        //draw in layout
        updatePositionInLayout();

        //attach the onClickListener
        setOnClickListener(mOnClickListener);

        //attach a drag&drop functionality
        setDragAndDrop();
    }

    private void updatePositionInLayout() {
        //set the box at the correct position on screen
        w = colour.getIntrinsicWidth();
        h = colour.getMinimumHeight();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraint);
        constraintSet.connect(getId(),ConstraintSet.TOP,constraintSet.PARENT_ID,constraintSet.TOP);
        constraintSet.connect(getId(),ConstraintSet.LEFT,constraintSet.PARENT_ID,constraintSet.LEFT);
        constraintSet.setMargin(getId(),ConstraintSet.TOP, (h * posY)+15);
        constraintSet.setMargin(getId(),ConstraintSet.LEFT, (w * posX)+15);
        constraintSet.applyTo(constraint);
    }

    //getters and setters
    public void setOnPopEventListener(OnPopEventListener eventListener){
        mOnPopEventListener = eventListener;
    }

    public void setOnBoxDraggedEventListener(OnBoxDraggedEventListener eventListener){
        mOnBoxDraggedEventListener = eventListener;
    }

    public int getBoxX(){
        return posX;
    }

    public int getBoxY(){
        return posY;
    }

    public void setXY(int x, int y){
        posX = x;
        posY = y;
        //also change position in layout
        updatePositionInLayout();
    }

    public int getColourID(){
        return colourID;
    }

    public ConstraintLayout getConstraint(){
        return constraint;
    }

    public void setBoxVisible(boolean bool){
        if (bool == false) {
            setVisibility(View.INVISIBLE);
        }
        else {setVisibility(View.VISIBLE);}
    }

    //methods

    //invokes the pop event
    private void pop(){
        //call the event
        if (mOnPopEventListener != null){
            mOnPopEventListener.onPopBox(this);
        }
    }

    // set drag & drop functionality
    private void setDragAndDrop () {
        //this is the drag functionality
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData dragData = ClipData.newPlainText("id", getBoxX() + "," + getBoxY());
                    // Instantiates the drag shadow builder.
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

                    // Starts the drag

                    v.startDrag(dragData,  // the data to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local data
                            0          // flags (not currently used, set to 0)
                    );

                    return true;
                } else {
                    return false;
                }
            }
        });

        //this is the listener for the drop functionality
        setOnDragListener(new View.OnDragListener() {
            @Override
            // v is the view that receives the drag event == view that is being dropped on
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        ClipData dragData = event.getClipData();
                        int dragX = Integer.parseInt(dragData.getItemAt(0).getText().toString().split(",")[0]);
                        int dragY = Integer.parseInt(dragData.getItemAt(0).getText().toString().split(",")[1]);
                        int dropX = getBoxX();
                        int dropY = getBoxY();
                        //move operation
                        //this should probably invoke an event for the BoxGrid, as Boxes shouldn't know about each other too much
                        //sadly, ClipData cannot carry an object reference to the dragged box, otherwise we could just pass the two Boxes to BoxGrid...
                        mOnBoxDraggedEventListener.onBoxDropped(dragX, dragY, dropX, dropY);
                        break;
                    default:
                        break;

                }
                return true;
            }

        });
    }


}
