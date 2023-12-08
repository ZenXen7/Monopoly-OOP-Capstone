package gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public abstract class BoardObject extends JPanel {
    protected int xCoord;
    protected int yCoord;
    protected int width;
    protected int height;

    public BoardObject(int xCoord, int yCoord, int width, int height) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;
        setBorder(new LineBorder(new Color(0, 0, 0)));
        setBounds(xCoord, yCoord, width, height);
        this.setLayout(null);
    }

    // Add other common methods if needed
}
