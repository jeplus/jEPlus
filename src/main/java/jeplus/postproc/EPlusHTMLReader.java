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
package jeplus.postproc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 *
 * @author Yi
 */
public class EPlusHTMLReader {
    
    
    public static void main (String [] args) throws IOException {
        File input = new File("eplustbl.htm");
        Document doc = Jsoup.parse(input, "UTF-8");
        HashMap<String, EPlusHTMLTable> Tables = new HashMap <> ();
        String curReport = "General";
        String curInfo = "";
        String curTitle = "";
        String curCaption = "";
        
        for (int i = 0; i < doc.body().childNodes().size(); i++) {
            Node child = doc.body().childNode(i);
            if (child.nodeName().equals("#comment")) {
                Element el = findPrevElement(child, "b");
                curCaption = (el == null) ? "" : el.text();
                curTitle = ((Comment)child).getData();
                Element tb = findNextElement(child, "table");
                Tables.put(curCaption, new EPlusHTMLTable (curTitle, curCaption, curInfo, tb));
            }
        }
        System.out.println(Tables.size() + " tables found.");
    }
    
    protected static Element findPrevElement (Node from, String type) {
        Node prev = from.previousSibling();
        while (prev != null) {
            if (prev instanceof Element)  {
                Element el = (Element)prev;
                if (el.tagName().equals(type)) {
                    return el;
                }
            }
            prev = prev.previousSibling();
        }
        return null;
    }
    
    protected static Element findNextElement (Node from, String type) {
        Node prev = from.nextSibling();
        while (prev != null) {
            if (prev instanceof Element)  {
                Element el = (Element)prev;
                if (el.tagName().equals(type)) {
                    return el;
                }
            }
            prev = prev.nextSibling();
        }
        return null;
    }
}

class EPlusHTMLTable {
    String FullTitle = null;    // from comment
    String Caption = null;      // from text preceeding the table
    String Report = null;       // Category level 1
    String For = null;          // Note of this report
    String Timestamp = null;    // Timestamp of this report
    String Note = null;         // Further note
    
    String [][] Table = null;

    public EPlusHTMLTable () {}
    public EPlusHTMLTable (String title, String caption, String info, Element table) {
        FullTitle = title;
        Caption = caption;
        Note = info;
        if (table.tagName().equals("table")) {
            parseTable (table);
        }
    }
    
    public final void parseTable (Element table) {        
        Elements trs = table.select("tr");
        String[][] trtd = new String[trs.size()][];
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            trtd[i] = new String[tds.size()];
            for (int j = 0; j < tds.size(); j++) {
                trtd[i][j] = tds.get(j).text(); 
            }
        }
        // trtd now contains the desired array for this table
        Table = trtd;
    }
}