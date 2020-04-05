/**
 * Copyright (C) 2012 SINTEF <franck.fleurey@sintef.no>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingml.rtcharts.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: franck
 * Date: 01/07/12
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class TestXYMultiBufferGraphPanel extends JFrame implements Runnable {

    DataBuffer graph_buffer_sin  = new DataBuffer(2, 100);
    DataBuffer graph_buffer_cos  = new DataBuffer(2, 100);
    DataBuffer graph_buffer_2cos = new DataBuffer(2, 100);
    XYMultiBufferGraphPanel graph_panel =  new XYMultiBufferGraphPanel("Sinus Graph !", -400, 400, 100,-110, 110, 25, Color.RED);

    public TestXYMultiBufferGraphPanel() {

        graph_panel.addBuffer(graph_buffer_cos, Color.YELLOW);
        graph_panel.addBuffer(graph_buffer_sin, Color.GREEN);
        graph_panel.addBuffer(graph_buffer_2cos, Color.RED);
        
        this.getContentPane().add(graph_panel);

        this.setSize(400, 300);

        this.setVisible(true);
        new Thread(this).start();
        graph_panel.start();
    }


    public void run() {
          double angle = -Math.PI;

          while (this.isVisible()) {
               try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
               
            {
                angle += Math.random();
                if (angle > Math.PI) angle = -Math.PI;
                int s = (int)(Math.sin(angle)*100.0);
                graph_buffer_sin.appendDataRow(new int[] {(int)(angle*100), s});
            }

            {
                angle += Math.random();
                if (angle > Math.PI) angle = -Math.PI;
                int c = (int)(Math.cos(angle)*100.0);
                graph_buffer_cos.appendDataRow(new int[] {(int)(angle*100), c});
            }

            {
                angle += Math.random();
                if (angle > Math.PI) angle = -Math.PI;
                int c = (int)(Math.cos(2*angle)*100.0);
                graph_buffer_2cos.appendDataRow(new int[] {(int)(angle*100), c});
            }

          }
        graph_panel.stop();
        System.out.println("End of producer thread.");

        System.exit(0);
    }


    public static void main(String[] args) {
        new TestXYMultiBufferGraphPanel();
        System.out.println("End of main thread.");
    }
}
