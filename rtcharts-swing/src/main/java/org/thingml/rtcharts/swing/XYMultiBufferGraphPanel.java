/**
 * Copyright (C) 2012 SINTEF <franck.fleurey@sintef.no>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June
 * 2007; you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.thingml.rtcharts.swing;

import java.awt.*;
import java.util.ArrayList;

public class XYMultiBufferGraphPanel extends AbstractXYGraphPanel {
    
    public class BufferInstance {
      public DataBuffer graphBuffer;
      public Color dataColor = Color.YELLOW;
      public int[][] graphValues;
      
      BufferInstance(DataBuffer buffer, Color color) {
          graphBuffer = buffer;
          dataColor = color;
      }
    }

    protected ArrayList<BufferInstance> bufferInstances = new ArrayList<BufferInstance>();
    protected Color graphColor = Color.RED;
    protected static int TOP_OFFSET = 20;
    protected static int BOTTOM_OFFSET = 2;
    protected static int LEFT_OFFSET = 0;
    protected static int RIGHT_OFFSET = 0;
    //Variables with getters/setters 
    protected long sleepTime = 100;
    protected int ymin = 0;
    protected int ymax = 1000;
    protected int xmin = 0;
    protected int xmax = 1000;
    protected int yminor = 100;
    protected int xminor = 50;

    public int getXminor() {
        return xminor;
    }

    public void setXminor(int xminor) {
        this.xminor = xminor;
    }
    protected String name = "";

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Color getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(Color color) {
        this.graphColor = color;
        jLabelXAVG.setForeground(color);
        jLabelYAVG.setForeground(color);
        jLabelTitle.setForeground(color);
    }

    public int getYmin() {
        return ymin;
    }

    public void setYmin(int ymin) {
        this.ymin = ymin;
        jLabelXYMin.setText("" + ymin);
    }

    public int getYmax() {
        return ymax;
    }

    public void setYmax(int ymax) {
        this.ymax = ymax;
        jLabelYMax.setText("" + ymax);
    }

    public int getYminor() {
        return yminor;
    }

    public void setYminor(int yminor) {
        this.yminor = yminor;
    }

    public void addBuffer(DataBuffer buffer, Color dataColor) {
        BufferInstance bp = new BufferInstance(buffer, dataColor);
        bufferInstances.add(bp);
    }
    
    /**
     * Create the panel.
     */
    public XYMultiBufferGraphPanel(String name, int xmin, int xmax, int xminor, int ymin, int ymax, int yminor, Color graphColor) {
        super();
        //this.graphBuffer = buffer;
        this.graphColor = graphColor;
        this.ymin = ymin;
        this.ymax = ymax;
        this.yminor = yminor;
        this.xmin = xmin;
        this.xmax = xmax;
        this.xminor = xminor;
        this.name = name;

        jLabelYMax.setText("" + ymax);
        jLabelXYMin.setText("" + ymin);

        jLabelTitle.setText(name);

        jLabelXAVG.setForeground(graphColor);
        jLabelYAVG.setForeground(graphColor);
        jLabelTitle.setForeground(graphColor);
    }

    protected int computeX(int value) {
        //return value * getWidth() / graphBuffer.getRows();
        return map(value, xmin, xmax, LEFT_OFFSET, getWidth() - RIGHT_OFFSET);
    }

    protected int computeY(int value) {
        return getHeight() - BOTTOM_OFFSET - map(value, ymin, ymax, BOTTOM_OFFSET, getHeight() - TOP_OFFSET);
    }
    final static float dash1[] = {5.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
    final static Color cline = new Color(128, 128, 128);
    //final static Color cxline = new Color(90,90,90);

    protected void drawData(Graphics g) {
        for (int inst = 0; inst < bufferInstances.size(); inst++) {
            int[][] graphValues = bufferInstances.get(inst).graphValues;
            DataBuffer graphBuffer = bufferInstances.get(inst).graphBuffer;
                    
            if (graphValues == null) break;
            g.setColor(bufferInstances.get(inst).dataColor);

            int X, Y;
            int xval, yval;

            for (int i = 0; i < graphBuffer.getRows(); i++) {

                xval = graphValues[0][i];
                yval = graphValues[1][i];

                if (xval != graphBuffer.getInvalidNumber()) {
                    if (yval != graphBuffer.getInvalidNumber()) {

                        // do not plot "out of range" values
                        if (xval < xmin || xval > xmax || yval < ymin || yval > ymax) continue;


                        X = computeX(xval);
                        Y = computeY(yval);

                        g.drawLine(X-2, Y, X+2, Y);
                        g.drawLine(X, Y-2, X, Y+2);
                    }
                }
            }
        }
    }

    protected void drawAxis(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        Stroke s = g2.getStroke();

        g2.setColor(cline);

        g2.fillRect(0, 0, getWidth(), jLabelTitle.getHeight());

        g2.setStroke(new BasicStroke(2));

        int _Ymin = computeY(ymin);
        int _Ymax = computeY(ymax);
        int _Xmin = computeX(xmin);
        int _Xmax = computeX(xmax);

        // draw the 0 axis:
        if (xmin <= 0 && xmax >= 0) {
            int x0 = computeX(0);
            g.drawLine(x0, _Ymin, x0, _Ymax);
        }
        if (ymin <= 0 && ymax >= 0) {
            int y0 = computeY(0);
            g.drawLine(_Xmin, y0, _Xmax, y0);
        }

        g2.setStroke(dashed);

        // g2.setStroke(new BasicStroke(1));
        for (int ypos = yminor; ypos <= ymax; ypos += yminor) {
            if (ypos >= ymin) {
                int y = computeY(ypos);
                g.drawLine(_Xmin, y, _Xmax, y);
            }
        }
        for (int ypos = -yminor; ypos >= ymin; ypos -= yminor) {
            if (ypos <= ymax) {
                int y = computeY(ypos);
                g.drawLine(_Xmin, y, _Xmax, y);
            }
        }

        for (int xpos = xminor; xpos <= xmax; xpos += xminor) {
            if (xpos >= xmin) {
                int x = computeX(xpos);
                g.drawLine(x, _Ymin, x, _Ymax);
            }
        }
        for (int xpos = -xminor; xpos >= xmin; xpos -= xminor) {
            if (xpos <= xmax) {
                int x = computeX(xpos);
                g.drawLine(x, _Ymin, x, _Ymax);
            }
        }

        g2.setStroke(s);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAxis(g);
        jLabelXYMin.setText(ymin + " | " + xmin);
        jLabelXMax.setText(""+ xmax);
        jLabelYMax.setText(""+ ymax);
        drawData(g);
        if (bufferInstances.size() > 0) {
            int[][] sd = bufferInstances.get(0).graphBuffer.stdDevColumns();
            jLabelXAVG.setText(sd[1][0] + " ["+sd[0][0]+"]");
            jLabelYAVG.setText(sd[1][1] + " ["+sd[0][1]+"]");
        }
    }

    protected int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    private boolean stop = false;

    public void start() {
        new PaintManager().start();
    }

    public void stop() {
        stop = true;
    }

    protected class PaintManager extends Thread {

        public void run() {
            boolean hasData = false;
            while (!stop) {

                hasData = false;
                for (int inst = 0; inst < bufferInstances.size(); inst++) {
                    BufferInstance bi = bufferInstances.get(inst);
                    if (bi.graphBuffer != null) {
                        bi.graphValues = bi.graphBuffer.getDataClone();
                        hasData = true;
                    }
                }
                if (hasData) {
                    repaint();
                }
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("End of paint thread.");
        }
    }
}
