/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>               *
 *                                                                         *
 *   This program is free software: you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 *                                                                         *
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanelStreamViewer extends javax.swing.JPanel implements Runnable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanelStreamViewer.class);
    
    /** Current viewing stream */
    protected BufferedReader CurrentStream = null;
    // Use a pair of piped streams to communicate with external processes in Stream mode
    protected PipedInputStream PIS = null;
    protected PrintStream POS = null;
    
    /**
     * Creates new form JPanelStreamViewer
     */
    public JPanelStreamViewer() {
        initComponents();
        txpContent.setText("");
        
    }

    /**
     * Note: print stream mode only works when this panel is running in a
     * separate thread
     * @return The PrintStream to which callers can send contents
     */
    public PrintStream getPrintStream() {
        if (POS == null) {
            if (PIS == null) PIS = new PipedInputStream();
            try {
                POS = new PrintStream(new PipedOutputStream(PIS));
                this.CurrentStream = new BufferedReader (new InputStreamReader (PIS));
            } catch (IOException ex) {
                logger.error("Cannot get output stream.", ex);
                POS = null;
            }
        }
        return POS;
    }

    /**
     * Clear the contents of the log text pane
     */
    public void clearLog () {
        StyledDocument doc = txpContent.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            logger.error("Cannot clear log contents.", ex);
        }
    }
    
    /**
     * Run as a thread
     */
    @Override
    public void run() {
        if (this.CurrentStream == null) {
            this.getPrintStream();
        }
        if (this.CurrentStream != null) {
            StyledDocument doc = txpContent.getStyledDocument();
            //  Define a keyword attribute
//            SimpleAttributeSet keyWord = new SimpleAttributeSet();
//            StyleConstants.setForeground(keyWord, Color.RED);
//            StyleConstants.setBackground(keyWord, Color.YELLOW);
//            StyleConstants.setBold(keyWord, true);
            while (true) {
                try {
                    while (CurrentStream.ready()) {
                        try {
                            doc.insertString(doc.getLength(), CurrentStream.readLine() + "\n", null );
                            txpContent.setCaretPosition(doc.getLength());
                        } catch (IOException | BadLocationException e) { }            
                    }
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException ioe) {
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txpContent = new javax.swing.JTextPane();

        jScrollPane1.setViewportView(txpContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane txpContent;
    // End of variables declaration//GEN-END:variables
}
