package com.example.match3.Events;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;

import com.example.match3.Box.Box;
import com.example.match3.Box.BoxGrid;
import com.example.match3.GameScreen;
import com.example.match3.R;

public class MyDragShadowBuilder extends View.DragShadowBuilder {

    // The drag shadow image, defined as a drawable thing
    private static Drawable shadow;
    private int sizeX;
    private int sizeY;
    private Box b;
    private Box[] boxesRow;
    private Box[] boxesColumn;

    // Defines the constructor for myDragShadowBuilder
    public MyDragShadowBuilder(View v) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(v);

        // cast v to box so we can use box functions
        b = (Box) v;

        //get the grid
        int posX = b.getBoxX();
        int posY = b.getBoxY();

        //todo : make dragshadow of row/column depending on drag direction and clamp that to the respective row/column, and make that wrap around the edges

        // Creates a draggable image that will fill the Canvas provided by the system.
        shadow = b.getDrawable();
        int w = shadow.getIntrinsicWidth();
        int h = shadow.getIntrinsicHeight();
        LayerDrawable ld = new LayerDrawable(new Drawable[] {});
        ld.addLayer(b.getDrawable());
        ld.addLayer(new ColorDrawable(Color.GREEN));
        ld.setLayerInset(0,0,0, w,0);
        ld.setLayerInset(1, w, 0, w, 0);

        shadow =  ld;

    }

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        // Defines local variables
        int width, height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth() * 2;

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight() * 1;

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width*2, height);

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height);

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(b.getDrawable().getIntrinsicWidth()/2, b.getDrawable().getIntrinsicHeight()/2);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas);
    }
}
