package com.kmcguire.slc.LobbyService;

public class AddStartRectEvent extends Event {
    private int         ally;
    private int         left;
    private int         top;

    public int getAlly() {
        return ally;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    public AddStartRectEvent(int ally, int left, int top, int right, int bottom) {
        this.ally = ally;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    private int         right;
    private int         bottom;
}
