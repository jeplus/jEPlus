/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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

import javax.swing.*;
import javax.swing.Timer.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.util.regex.*;

/**
 * Incremental search panel
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.1
 */
public class IncSearchPanel extends javax.swing.JPanel implements ActionListener, DocumentListener, KeyListener {

    private int lengthComparison = 0;
    
    protected JTextComponent target;
    protected Matcher matcher;
    private Timer timer = new Timer(2000, this);
    private boolean runAnimation = false;

    /** Creates new form IncSearchPanel */
    public IncSearchPanel(JTextComponent c) {
        initComponents();
        
        target = c;
        search.addActionListener(this);
        query.addActionListener(this);
        query.getDocument().addDocumentListener(this);
        search.setToolTipText("Find text in the current document (CTRL+F)");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    @Override
    public void keyTyped(KeyEvent e)  {
        if(e.getKeyChar() == '\n') {
            
        } else {
            runNewSearch();
        }
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        runNewSearch();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        runNewSearch();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        runNewSearch();
    }
    
    private void runNewSearch() {
        try {
            String q = query.getText();
            Pattern pattern = Pattern.compile(q);
            
            Document doc = target.getDocument();
            String body = doc.getText(0, doc.getLength());
            lengthComparison = body.length();
            matcher = pattern.matcher(body);
            continueSearch();
        } catch(Exception e) {
            System.out.println("Problem with the query "+e);
        }
    }
    
    private void continueSearch() {
        if(matcher != null) {
            if(matcher.find()) {
                if(runAnimation) {
                    if(target.getDocument().getLength() != lengthComparison) {
                        runNewSearch();
                    }
                    query.setBackground(Color.WHITE);
                    timer.stop();
                    try {
                        target.getCaret().setDot(matcher.start());
                        target.getCaret().moveDot(matcher.end());
                        target.getCaret().setSelectionVisible(true);
                    }
                    catch(Exception e) {
                        System.out.println(e);
                    }
                    query.requestFocusInWindow();
                }
                runAnimation = true;
            }
            else {
                if(runAnimation) {
                    timer.setInitialDelay(3000);
                    query.setBackground(Color.RED);
                    timer.start();
                    matcher.reset();
                }
                runAnimation = true;
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(query.getText().equals("")) {
            query.requestFocusInWindow();
        }
        
        if(e.getSource() == timer) {
            query.setBackground(Color.WHITE);
            runAnimation = false;
            timer.stop();
        }
        
        continueSearch();
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        query = new javax.swing.JTextField();
        search = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(310, 22));

        query.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryActionPerformed(evt);
            }
        });

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/find.png"))); // NOI18N
        search.setToolTipText("Find next");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(query, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(query, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void queryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_queryActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField query;
    private javax.swing.JButton search;
    // End of variables declaration//GEN-END:variables

}
