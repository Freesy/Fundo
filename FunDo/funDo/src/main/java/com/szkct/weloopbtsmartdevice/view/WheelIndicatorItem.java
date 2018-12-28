
package com.szkct.weloopbtsmartdevice.view;

/**
 * Created by david on 23/5/15.
 */
public class WheelIndicatorItem {

    private float weight;
    private int color;

    public WheelIndicatorItem(){
        weight = 0;
    }

    public WheelIndicatorItem(float weight,int color) {
        if (weight < 0 )
            throw new IllegalArgumentException("weight value should be positive");

        this.weight = weight;
        this.color = color;
    }

    public void setWeight(float weight) {
        if (weight < 0 )
            throw new IllegalArgumentException("weight value should be positive");

        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

}
