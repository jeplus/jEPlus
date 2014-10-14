/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.gui;

import java.util.ArrayList;
import java.util.Objects;
import javax.swing.text.AttributeSet;

/**
 * Search string class
 */
public class MarkedSearchTag {
    String Entry = null;
    boolean Requested = false;
    ArrayList<Integer> Markers = new ArrayList<>();
    int CurrentSelection = 1; // 1-based

    public MarkedSearchTag(String str, boolean req) {
        Entry = str;
        Requested = req;
    }

    public void setRequested(boolean req) {
        Requested = req;
    }

    public void resetMarkers() {
        Markers.clear();
    }

    public void addMarker(int cursor) {
        Integer Pos = new Integer(cursor);
        if (!Markers.contains(Pos)) {
            Markers.add(Pos);
        }
    }

    public void addAllMarkers(ArrayList<Integer> markers) {
        Markers.clear();
        Markers.addAll(markers);
    }

    public int getCurrentSelection() {
        return CurrentSelection;
    }

    public void setCurrentSelection(int CurrentSelection) {
        this.CurrentSelection = CurrentSelection;
    }

    public void selectNext() {
        CurrentSelection = (CurrentSelection >= Markers.size()) ? 1 : CurrentSelection + 1;
    }

    public String getEntry() {
        return Entry;
    }

    public void setEntry(String Entry) {
        this.Entry = Entry;
    }

    public ArrayList<Integer> getMarkers() {
        return Markers;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MarkedSearchTag) {
            if (Entry.equals(((MarkedSearchTag) o).Entry)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.Entry);
        return hash;
    }

    @Override
    public String toString() {
        return Entry + " (" + CurrentSelection + "/" + Markers.size() + ")";
    }

    public AttributeSet getAttributeSet() {
        if (Requested && Markers.isEmpty()) {
            return EPlusTextPanel.AttributeSets.getHighlight3Attr();
        } else if (Requested && !Markers.isEmpty()) {
            return EPlusTextPanel.AttributeSets.getHighlightAttr();
        }
        return EPlusTextPanel.AttributeSets.getHighlight2Attr();
    }
    
}
