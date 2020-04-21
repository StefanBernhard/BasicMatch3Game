package com.example.match3.Events;


/*
This event gets invoked when a box is dragged onto another box, with conditions set in Box
 */
public interface OnBoxDraggedEventListener {
    public void onBoxDropped(int dragX, int dragY, int dropX, int dropY);
}
