/* Name: Clara Fee, Julia Rieger 
 * File: Image.java
 * Desc: 
 * 
 * Class to store one image with all RGB values
 * 
 */



public class Image {
    
    int width;
    int height;
    MyColor[][] colorArray;
    String filename;

    public Image(MyColor[][] colorArray, int width, int height) {
        this.colorArray = colorArray;
        this.width = width;
        this.height = height;
        this.filename = "";
    }

    public void setFilename(String newName) {
        this.filename = newName;
    }

    public String getFilename() {
        return this.filename;
    }
    public MyColor[][] getColorArray() {
        return this.colorArray;
    }
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }

    public int getSize() {
        return this.width * this.height;
    }

    public MyColor getStartPixel() {
        return this.colorArray[0][0];
    }
    public MyColor getEndPixel() {
        return this.colorArray[width-1][height-1];
    }

    public void shade(double shadeFactor) throws IllegalArgumentException { 
        if (shadeFactor == -1) {
            for (int i = 0; i < this.getHeight(); i++) {
                for (int j = 0; j < this.getWidth(); j++) {
                    colorArray[i][j].negative();
                }
            }
        }
        else {
            if ((shadeFactor < 0) || (shadeFactor > 1)) {
                throw new IllegalArgumentException("shadeFactor must be between 0 and 1!");
            } 
            for (int i = 0; i < this.getHeight(); i++) {
                for (int j = 0; j < this.getWidth(); j++) {
                    colorArray[i][j].shade(shadeFactor);
                }
            } 
        }   
    }

    
    public Image edgeDetection(double[][] edgeDetectionFilter, int startI, int startJ, int endI, int endJ) {

        double w1 = edgeDetectionFilter[0][0];
        double w2 = edgeDetectionFilter[1][0];
        double w3 = edgeDetectionFilter[2][0];
        double w4 = edgeDetectionFilter[0][1];
        double w5 = edgeDetectionFilter[1][1];
        double w6 = edgeDetectionFilter[2][1];
        double w7 = edgeDetectionFilter[0][2];
        double w8 = edgeDetectionFilter[1][2];
        double w9 = edgeDetectionFilter[2][2];

        double newRed = 0;
        double newBlue = 0;
        double newGreen = 0;

        MyColor bottomLeft;
        MyColor left;
        MyColor topLeft;
        MyColor bottom;
        MyColor center;
        MyColor top;
        MyColor bottomRight;
        MyColor right;
        MyColor topRight;

        MyColor[][] colorArray = this.getColorArray();
        MyColor[][] returnThisArr = new MyColor[colorArray.length][colorArray[0].length];

        for (int i = startI; i < endI; i++) {
            for (int j = startJ; j < endJ; j++) {
                if (((i - 1) > startI) && ((i + 1) < endI) && ((j - 1) > startJ) && ((j + 1) < endJ)) {
                    
                    topLeft = colorArray[i - 1][j - 1];
                    left = colorArray[i][j - 1];
                    bottomLeft = colorArray[i + 1][j - 1];
                    top = colorArray[i - 1][j];
                    center = colorArray[i][j];
                    bottom = colorArray[i + 1][j];
                    topRight = colorArray[i - 1][j + 1];
                    right = colorArray[i][j + 1];
                    bottomRight = colorArray[i + 1][j + 1];
                     
                    newRed = Math.abs((topLeft.getR() * w1) + (top.getR() * w2) + (topRight.getR() * w3) + (left.getR() * w4) + (center.getR() * w5) + (right.getR() * w6) + (bottomLeft.getR() * w7) + (bottom.getR() * w8) + (bottomRight.getR() * w9));

                    newGreen = Math.abs((topLeft.getG() * w1) + (top.getG() * w2) + (topRight.getG() * w3) + (left.getG() * w4) + (center.getG() * w5) + (right.getG() * w6) + (bottomLeft.getG() * w7) + (bottom.getG() * w8) + (bottomRight.getG() * w9));

                    newBlue = Math.abs((topLeft.getB() * w1) + (top.getB() * w2) + (topRight.getB() * w3) + (left.getB() * w4) + (center.getB() * w5) + (right.getB() * w6) + (bottomLeft.getB() * w7) + (bottom.getB() * w8) + (bottomRight.getB() * w9));

                    if (newRed > 255) {
                        newRed = 255;
                    }
                    if (newGreen > 255) {
                        newGreen = 255;
                    }
                    if (newBlue > 255) {
                        newBlue = 255;
                    }
                }
                MyColor newColor = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
                returnThisArr[i][j] = newColor;
            }
        }
        return new Image(returnThisArr, returnThisArr.length, returnThisArr[0].length);
    }
}