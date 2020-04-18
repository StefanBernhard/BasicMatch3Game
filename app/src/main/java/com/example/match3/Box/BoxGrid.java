package com.example.match3.Box;

import com.example.match3.Events.OnBoxRemovedEventListener;
import com.example.match3.Events.OnPopEventListener;

import java.util.ArrayList;
import java.util.List;

/*
This class stores all the game boxes in a grid and provides functionality to find nearby boxes
 */
public class BoxGrid {

    private Box[][] grid;
    private int sizeX;
    private int sizeY;

    //Event handling for the remove action
    private OnBoxRemovedEventListener mOnBoxRemovedEventListener;

    public BoxGrid(int x, int y){
        sizeX = x;
        sizeY = y;
        grid = new Box[sizeX][sizeY];
    }

    //getters and setters
    public void setOnBoxRemovedEventListener(OnBoxRemovedEventListener eventListener){
        mOnBoxRemovedEventListener = eventListener;
    }

    //methods

    //adds a single box to the grid
    public boolean addBox(Box newBox){
        int x = newBox.getX();
        int y = newBox.getY();
        if (grid[x][y] == null){
            grid[x][y] = newBox;
            //set the listener to replace the box on pop
            newBox.setOnPopEventListener(new OnPopEventListener() {
                @Override
                public void onPopBox(Box popped) {
                    replaceBox(popped);
                }
            });
            newBox.setBoxVisible(true);
            return true;
        }
        return false;
    }

    //removes a single box from the grid
    public boolean removeBox(Box toRemove){

        for (int x=0; x <= sizeX-1; x++) {
            for (int y=0; y <= sizeY-1; y++){
                Box search = grid[x][y];
                if (search == toRemove){
                    grid[x][y] = null;
                    toRemove.setBoxVisible(false);
                    toRemove = null;
                    return true;
                }
            }
        }
        return false;
    }

    //replaces a box and calculates the score for the box
    private void replaceBox(Box popped) {
        List<Box> boxesToPop = findAllConnectedBoxes(popped, new ArrayList<Box>());
        int points = boxesToPop.size();
        for (Box b : boxesToPop) {
            removeBox(b);
            Box newBox = new Box(b.getX(), b.getY(), b.getConstraint());
            addBox(newBox);
        }
        boxRemovedEvent(points);
    }

    private List<Box> findChainedBoxes2Directions(Box popped){
        List<Box> boxes = new ArrayList<Box>();
        Box adjacent = null;
        for (int dirX = -1; dirX <= 1; dirX += 2){
            adjacent = findAdjacent(popped, dirX, 0);
            while (adjacent != null) {
                boxes.add(adjacent);
                adjacent = findAdjacent(adjacent, dirX, 0);
            }
        }
        for (int dirY = -1; dirY <= 1; dirY += 2){
            adjacent = findAdjacent(popped, 0, dirY);
            while (adjacent != null) {
                boxes.add(adjacent);
                adjacent = findAdjacent(adjacent, 0, dirY);
            }
        }
        return boxes;
    }

    private List<Box> findAllConnectedBoxes(Box current, List<Box> totalList){
        if (!totalList.contains(current)) {totalList.add(current);}
        int startSize = totalList.size();
        List<Box> futureSearches = new ArrayList<Box>();
        for (int dirX = -1; dirX <= 1; dirX += 2){
            Box adj = findAdjacent(current, dirX, 0);
            if (adj != null && !totalList.contains(adj)) {
                totalList.add(adj);
                futureSearches.add(adj);
                }
            }
        for (int dirY = -1; dirY <= 1; dirY += 2){
            Box adj = findAdjacent(current, 0, dirY);
            if (adj != null && !totalList.contains(adj)) {
                totalList.add(adj);
                futureSearches.add(adj);
            }
        }
        int endSize = totalList.size();
        if (startSize == endSize) {
            return totalList;
        } else for (Box b : futureSearches) {
            findAllConnectedBoxes(b, totalList);
        }
    return totalList;
    }

    private Box findAdjacent(Box current, int dirX, int dirY){
        Box adj = null;
        int currentX = current.getX();
        int currentY = current.getY();
        int adjX = currentX+dirX;
        int adjY = currentY+dirY;
        if (adjX < 0 || adjY < 0 || adjX > sizeX-1 || adjY > sizeY-1) {return null;}
        adj = grid[adjX][adjY];
        int currentCol = current.getColourID();
        int adjCol = adj.getColourID();
        if (currentCol == adjCol){
            return adj;
        } else {
            return null;
        }
    }

    //invokes the box Removed event
    private void boxRemovedEvent(int points){
        if (mOnBoxRemovedEventListener != null){
            mOnBoxRemovedEventListener.onBoxRemoved(points);
        }
    }

    //clears the grid of all boxes
    public void clear(){
        for (int x=0; x <= sizeX-1; x++) {
            for (int y=0; y <= sizeY-1; y++){
                grid[x][y].setBoxVisible(false);
                grid[x][y] = null;
            }
        }
    }


}
