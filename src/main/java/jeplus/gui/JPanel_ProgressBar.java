/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yi@jeplus.org>                          *
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
 ***************************************************************************/
package jeplus.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.Timer;
import jeplus.agent.EPlusAgent.AgentState;

/**
 *
 * @author zyyz
 */
public class JPanel_ProgressBar extends javax.swing.JPanel implements WindowListener {

    JFrameAgentMonitor Monitor = null;
    // Get current time
    long StartTime = System.currentTimeMillis();
    protected Timer AutoUpdateTimer = null;
    
    /**
     * Creates new form JPanel_ProgressBar
     */
    public JPanel_ProgressBar() {
        initComponents();
    }

    /**
     * Creates new form JPanel_ProgressBar
     */
    public JPanel_ProgressBar(JFrameAgentMonitor monitor) {
        initComponents();
        Monitor = monitor;
        int nthreads = Monitor.getAgent().getSettings().getNumThreads();
        sldThreads.setMaximum(Runtime.getRuntime().availableProcessors());
        sldThreads.setValue(nthreads);
        ActionListener taskPerformer = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
              refresh();
          }
        };
        int delay = 2000; //milliseconds
        AutoUpdateTimer = new Timer(delay, taskPerformer);
    }

    public void refresh () {
        // Progress bar
        int finished = Monitor.getAgent().getFinishedJobs().size() + Monitor.getAgent().getRejectedJobs().size();
        int total = finished + Monitor.getAgent().getRunningJobs().size() + Monitor.getAgent().getJobQueue().size();
        this.barProgress.setValue((total > 0) ? (int)(100 * finished / total) : 0);
        // Info
        //lblInfo.setText(finished + " of " + total + " jobs have finished.");
        lblInfo.setText(Monitor.getAgent().getStatus());
        // Get elapsed time in milliseconds
        if (Monitor.getAgent().getState() == AgentState.RUNNING) {
            long elapsedTime = System.currentTimeMillis() - StartTime;
            String str = "Elapsed time: " + showElapsedTime(elapsedTime);
            if (finished > 0) str = str.concat(" (Estimated remaining time: " + showElapsedTime (elapsedTime * (total - finished)/finished) + ")");
            lblTime.setText(str);
        }else if (Monitor.getAgent().getState() == AgentState.FINISHED || Monitor.getAgent().getState() == AgentState.CANCELLED) {
            Monitor.setSimulationFinished();
        }
    }
    
    protected String showElapsedTime (long elapsedTimeMillis) {
        // Get elapsed time in days
        long days = elapsedTimeMillis / (24*60*60*1000);
        long remainder = elapsedTimeMillis % (24*60*60*1000);
        long hours = remainder / (60*60*1000);
        remainder = remainder % (60*60*1000);
        long minutes = remainder / (60*1000);
        remainder = remainder % (60*1000);
        long seconds = remainder / 1000;
        return ((days>0)?(days + " day " + hours + " hr " + minutes + " min") : 
            (((hours>0)?(hours + " hr ") : "") + minutes + " min " + seconds + " sec"));
    }
    
    public final void resetClock () {
        this.StartTime = System.currentTimeMillis();
    }
    
    public final void dispose () {
        if (AutoUpdateTimer.isRunning()) AutoUpdateTimer.stop();
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        barProgress = new javax.swing.JProgressBar();
        lblInfo = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        sldThreads = new javax.swing.JSlider();

        barProgress.setStringPainted(true);

        lblInfo.setText("Currently running: ");

        lblTime.setText("Elapsed Time: ");

        sldThreads.setMajorTickSpacing(2);
        sldThreads.setMaximum(8);
        sldThreads.setMinorTickSpacing(1);
        sldThreads.setOrientation(javax.swing.JSlider.VERTICAL);
        sldThreads.setPaintLabels(true);
        sldThreads.setPaintTicks(true);
        sldThreads.setSnapToTicks(true);
        sldThreads.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Threads", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        sldThreads.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldThreadsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barProgress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblInfo)
                        .addGap(18, 18, 18)
                        .addComponent(barProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                        .addComponent(lblTime))
                    .addComponent(sldThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sldThreadsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldThreadsStateChanged
        Monitor.getAgent().getSettings().setNumThreads(sldThreads.getValue());

    }//GEN-LAST:event_sldThreadsStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barProgress;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblTime;
    private javax.swing.JSlider sldThreads;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
        AutoUpdateTimer.start();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        dispose();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
