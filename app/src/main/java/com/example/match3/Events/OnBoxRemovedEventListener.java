package com.example.match3.Events;

import com.example.match3.Box.Box;


    /*
The event that gets invoked when a box is removed from the grid.
It needs to communicate a score to the game manager.
 */
    public interface OnBoxRemovedEventListener {
        public void onBoxRemoved(int points);
    }

