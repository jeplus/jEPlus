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
package jeplus.util;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

/**
 * this class provides functions used to generate a relative path from two absolute paths
 *
 * @author David M. Howard
 */
public class RelativeDirUtil {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RelativeDirUtil.class);

    /**
     * break a path down into individual elements and add to a list. example : if a path is /a/b/c/d.txt, the breakdown will be
     * [d.txt,c,b,a]
     *
     * @param f input file
     * @return a List collection with the individual elements of the path in reverse order
     */
    private static List getPathList(File f) {
        List l = new ArrayList();
        File r;
        try {
            r = f.getCanonicalFile();
            while (r != null) {
                l.add(r.getName());
                r = r.getParentFile();
            }
        } catch (IOException e) {
            logger.error("", e);
            l = null;
        }
        return l;
    }

    /**
     * figure out a string representing the relative path of 'f' with respect to 'r'
     *
     * @param r home path
     * @param f path of file
     */
    private static String matchPathLists(List r, List f) {
        int i;
        int j;
        String s;
        // start at the beginning of the lists
        // iterate while both lists are equal
        s = "";
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common root
        while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
            i--;
            j--;
        }

        // If two lists are identical, i==-1, j==-1
        if (i < 0 && j < 0) {
            s += "." + File.separator;
        } else { // Otherwise
            // for each remaining level in the home path, add a ..
            for (; i >= 0; i--) {
                s += ".." + File.separator;
            }
            // for each level in the file path, add the path
            for (; j >= 0; j--) {
                s += f.get(j) + File.separator;
            }
        }
        return s;
    }

    /**
     * get relative path of File 'f' with respect to 'home' directory example : home = /a/b/c/ path = /a/d/e/ s = getRelativePath(home,
     * path) = ../../d/e/
     *
     * @param home base path, should be a directory, not a file, or it doesn't make sense
     * @param path the path (to a file) to generate path for
     * @return path from home to path as a string
     */
    public static String getRelativePath(File home, File path) {
        File r;
        List homelist;
        List filelist;
        String s;

        homelist = getPathList(home);
        filelist = getPathList(path);
        s = matchPathLists(homelist, filelist);

        return s;
    }

    /**
     * Check the path is absolute or not. If it is not, use the specified BaseDir and calculate absolute path
     *
     * @param thispath The Path to check
     * @param BaseDir The Base Directory to which a relative path is associated
     * @return The absolute (canonical where possible) path
     */
    public static String checkAbsolutePath(String thispath, String BaseDir) {
        String abspath;
        File path = new File(thispath);
        if (!path.isAbsolute()) {
            path = new File(BaseDir + thispath);
        }
        try {
            abspath = path.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(RelativeDirUtil.class.getName()).log(Level.WARNING, null, ex);
            abspath = path.getAbsolutePath();
        }
        return abspath;
    }

    /**
     * Get the relative path from one file to another, specifying the directory separator. If one of the provided resources does not exist,
     * it is assumed to be a file unless it ends with '/' or '\'.
     *
     * @param targetPath targetPath is calculated to this file
     * @param basePath basePath is calculated from this file
     * @param pathSeparator directory separator. The platform default is not assumed so that we can test Unix behaviour when running on
     * Windows (for example)
     * @return
     */
    public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);
        // Undo the changes to the separators made by normalization
        switch (pathSeparator) {
            case "/":
                normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
                normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);
                break;
            case "\\":
                normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
                normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuilder common = new StringBuilder();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
                    common.append(target[commonIndex]).append(pathSeparator);
                    commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath
                    + "'");
        }

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        // 
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuilder relative = new StringBuilder();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append("..").append(pathSeparator);
            }
        }
        // deal with current folder (targetPath and basePath are the same)
        if (normalizedTargetPath.length() <= common.length()) {
            relative.append(".");
        }else {
            relative.append(normalizedTargetPath.substring(common.length()));
        }
        return relative.append(pathSeparator).toString();
    }

    static class PathResolutionException extends RuntimeException {

        PathResolutionException(String msg) {
            super(msg);
        }
    }

    /**
     * test the function
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("RelativePath <home> <file>");
            return;
        }
        System.out.println("home = " + args[0]);
        System.out.println("file = " + args[1]);
        System.out.println("path = " + getRelativePath(new File(args[0]), new File(args[1]).getParentFile()) + new File(args[1]).getName());
    }
}
