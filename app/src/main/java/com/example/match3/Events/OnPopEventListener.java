package com.example.match3.Events;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.match3.Box.Box;

/*
The event that gets invoked when a box is clicked.
It needs to communicate which box was clicked, what position the box was in, and in which constraintlayout that box is.
 */
public interface OnPopEventListener {
    public void onPopBox(Box popped);
}
