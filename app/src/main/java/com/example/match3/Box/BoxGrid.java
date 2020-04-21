package com.example.match3.Box;

import com.example.match3.Events.OnBoxDraggedEventListener;
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
    public int getSizeX(){
        return sizeX;
    }
    public int getSizeY(){
        return sizeY;
    }
    public Box[] getBoxesRow(int posY){
        Box[] row = new Box[sizeY];
        for(int i=0; i<row.length; i++){
            row[i] = grid[i][posY];
        }
        return row;
    }
    public Box[] getBoxesColumn(int posX){
        Box[] column = new Box[sizeY];
        for(int i=0; i<column.length; i++){
            column[i] = grid[i][posX];
        }
        return column;
    }


    //methods


    //adds a single box to the grid
    public boolean addBox(Box newBox){
        int x = newBox.getBoxX();
        int y = newBox.getBoxY();
        if (grid[x][y] == null){
            grid[x][y] = newBox;
            //set the listener to replace the box on pop
            newBox.setOnPopEventListener(new OnPopEventListener() {
                @Override
                public void onPopBox(Box popped) {
                    replaceBox(popped);
                }
            });
            newBox.setOnBoxDraggedEventListener(new OnBoxDraggedEventListener() {
                @Override
                public void onBoxDropped(int dragX, int dragY, int dropX, int dropY) {
                    moveBoxes(dragX, dragY, dropX, dropY);
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
            Box newBox = new Box(b.getBoxX(), b.getBoxY(), b.getConstraint());
            addBox(newBox);
        }
        boxRemovedEvent(points);
    }

    //moves boxes according to game rules
    private void moveBoxes(int fromX, int fromY, int toX, int toY){
        boolean moved = false;
        //allow only cardinal movements
        int diffX = toX - fromX;
        int diffY = toY - fromY;
        if (diffX != 0 && diffY == 0){
            //horizontal movement: for each diff, move boxes over one step, wrap around sides
            //determine if we move left or right? no, left-movement is just the inverse of right movement!
            //so get the steps to move right from whatever diffX is
            int movesRight = diffX;
            if (diffX < 0) {
                movesRight = sizeX + diffX;
            }
            for (int i=1; i<=movesRight; i++){
                //to move boxes, we must put each box over one step, for each box in sizeX
                    Box currBox = grid[0][fromY];
                for (int k=0; k<sizeX-1; k++){ //do this sizeX-1 times, as the last step is the wrap-around that will be handled separately
                    //make a tmp box to store the box to override in.
                    Box tmpBox = grid[k+1][fromY];
                    //move current box to that backed-up space
                    grid[k+1][fromY] = currBox;
                    grid[k+1][fromY].setXY(k+1, fromY);
                    //tmp box is now our new curr Box
                    currBox = tmpBox;
                } //after the loop the final step is to move the last box to the first position
                grid[0][fromY] = currBox;
                grid[0][fromY].setXY(0, fromY);
            }
            moved = true;
        }
        if (diffX == 0 && diffY != 0){
            //vertical movement: for each diff, move boxes down one step, wrap around bottom
            //get the steps to move down from whatever diffY is
            int movesDown = diffY;
            if (diffY < 0) {
                movesDown = sizeY + diffY;
            }
            for (int i=1; i<=movesDown; i++){
                //to move boxes, we must put each box over one step, for each box in sizeX
                Box currBox = grid[fromX][0];
                for (int k=0; k<sizeY-1; k++){ //do this sizeY-1 times, as the last step is the wrap-around that will be handled separately
                    //make a tmp box to store the box to override in.
                    Box tmpBox = grid[fromX][k+1];
                    //move current box to that backed-up space
                    grid[fromX][k+1] = currBox;
                    grid[fromX][k+1].setXY(fromX, k+1);
                    //tmp box is now our new curr Box
                    currBox = tmpBox;
                } //after the loop the final step is to move the last box to the first position
                grid[fromX][0] = currBox;
                grid[fromX][0].setXY(fromX, 0);
            }
            moved = true;
        }
        //finally, if there was valid movement, or no movement at all, perform a click on the dropped position
        if (moved || (diffX == 0 && diffY == 0)){
            grid[toX][toY].performClick();
        }
    }

    //finds all boxes of the same colour horizontally and vertically
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

    //finds all boxes of the same colour that are connected in any direction, recursively
    private List<Box> findAllConnectedBoxes(Box current, List<Box> totalList){
        if (!totalList.contains(current)) {totalList.add(current);} //if so same box doesn't get added multiple times
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
        if (startSize == endSize) { //check if any boxes were added, if not, we are finished
            return totalList;
        } else for (Box b : futureSearches) {
            //recursion
            findAllConnectedBoxes(b, totalList);
        }
    return totalList;
    }

    //returns an adjacent box if it has the same colour
    private Box findAdjacent(Box current, int dirX, int dirY){
        Box adj = null;
        int currentX = current.getBoxX();
        int currentY = current.getBoxY();
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
