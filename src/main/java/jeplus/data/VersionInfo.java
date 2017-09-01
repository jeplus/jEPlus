/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeplus.data;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
@XmlRootElement
public class VersionInfo implements Comparable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(VersionInfo.class);
    
    public Integer Major = 0;
    public Integer Minor = 0;
    public Integer Revision = null;
    public Integer Update = null;
    
    public VersionInfo () {}
    public VersionInfo (String verstr) {
        if (verstr != null && verstr.trim().length() > 0) {
            String [] parts = verstr.split("\\.");
            try {
                Major = new Integer (parts[0]);
                if (parts.length > 1) {
                    Minor = new Integer (parts[1]);
                }
                if (parts.length > 2) {
                    Revision = new Integer (parts[2]);
                }
                if (parts.length > 3) {
                    Update = new Integer (parts[3]);
                }
            }catch (NumberFormatException nfe) {
                logger.error ("Version string is not recognized: " + verstr);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.Major);
        hash = 97 * hash + Objects.hashCode(this.Minor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionInfo other = (VersionInfo) obj;
        if (!Objects.equals(this.Major, other.Major)) {
            return false;
        }
        if (!Objects.equals(this.Minor, other.Minor)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder();
        buf.append(Major).append(".").append(Minor);
        if (Revision != null) {
            buf.append(".").append(Revision);
            if (Update != null) {
                buf.append(".").append(Update);
            }
        }
        return buf.toString();
    }

    @Override
    public int compareTo(Object obj) {
        if (obj == null) {
            return 1;
        }
        if (getClass() != obj.getClass()) {
            return 1;
        }
        final VersionInfo other = (VersionInfo) obj;
        if (this.Major.compareTo(other.Major) > 0) {
            return 1;
        }else if (this.Major.compareTo(other.Major) == 0) {
            return this.Minor.compareTo(other.Minor);
        }else {
            return -1;
        }
    }
    
    
}
