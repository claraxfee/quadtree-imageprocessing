/* Name: Clara Fee, Julia Rieger 
 * File: MyColor.java
 * Desc: 
 * 
 * Class to store one pixel with its RGB value and index within the image
 * 
 */



public class MyColor implements Comparable<MyColor>  {
    
    private int red;
    private int green;
    private int blue;
    private int i; //HEIGHT idx
    private int j; //WIDTH idx

    public MyColor(int red, int green, int blue) {
	this.red = red;
	this.green = green;
	this.blue = blue;
	this.i = -1;
	this.j = -1;
    }

    public MyColor(int red, int green, int blue, int i, int j) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.i = i;
        this.j = j;
    }

    public int getR() {
        return this.red;
    }
    public int getG() {
        return this.green;
    }
    public int getB() {
        return this.blue;
    }
    public int getI() {
        return this.i;
    }
    public int getJ() {
        return this.j;
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public void setRed(int red) {
        this.red = red;
    }
    public void setGreen(int green) {
        this.green = green;
    }
    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void shade(double shadeFactor) {
        this.red = (int) (this.red * shadeFactor);
        this.green = (int) (this.green * shadeFactor);
        this.blue = (int) (this.blue * shadeFactor);
    }

    public void negative() {
        this.red = 255 - this.red;
        this.green = 255 - this.green;
        this.blue = 255 - this.blue;
    }

    public void greyScale() {
        int c = (int) (this.red * 0.3 + this.green * 0.59 + this.blue * 0.11);
        this.red = c;
        this.green = c;
        this.blue = c;
    }

    public String toString() {
        return this.red + "/" + this.green + "/" + this.blue;
    }

    public int compareTo(MyColor c) {
        if ((this.i == c.i) && (this.j == c.j)) {
            return 0;
        }
        else {
            return -1;
        }
    }
}
