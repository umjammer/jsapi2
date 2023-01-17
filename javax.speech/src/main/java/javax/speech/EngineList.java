/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 54 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.speech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

//Comp. 2.0.6

public class EngineList {

    /**
     * The features in this list.
     */
    private List<EngineMode> features;

    public EngineList(EngineMode[] features) {
        this.features = new ArrayList<>(features.length);
        Collections.addAll(this.features, features);
    }

    public boolean anyMatch(EngineMode require) {
        // A match for require==null is handled by the match methods
        for (EngineMode mode : features) {
            if (mode.match(require)) {
                return true;
            }
        }

        return false;
    }

    public EngineMode elementAt(int index) throws ArrayIndexOutOfBoundsException {
        try {
            return features.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(e.getMessage());
        }
    }

    public Enumeration<EngineMode> elements() {
        return Collections.enumeration(features);
    }

    public void orderByMatch(EngineMode require) {
        if (require == null) {
            return;
        }
        Comparator<EngineMode> comparator = new EngineListComparator(require);
        Sorter.sort(features, comparator);
    }

    public void rejectMatch(EngineMode reject) {
        List<EngineMode> cleaned = new ArrayList<>();
        
        for (EngineMode mode : features) {
            if (!mode.match(reject)) {
                cleaned.add(mode);
            }
        }
        
        features = cleaned;
    }

    public void removeElementAt(int index) throws ArrayIndexOutOfBoundsException {
        try {
            features.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(e.getMessage());
        }
    }

    public void requireMatch(EngineMode require) {
        List<EngineMode> cleaned = new ArrayList<>();
        
        for (EngineMode mode : features) {
            if (mode.match(require)) {
                cleaned.add(mode);
            }
        }
        
        features = cleaned;
    }

    public int size() {
        return features.size();
    }

    /**
     * 
     * @author Dirk Schnelle Note: this comparator imposes orderings that are
     *         inconsistent with equals.
     */
    private static class EngineListComparator implements Comparator<EngineMode> {
        final EngineMode require;

        public EngineListComparator(EngineMode require) {
            this.require = require;
        }

        public int compare(EngineMode mode1, EngineMode mode2) {
            boolean object1Matches = mode1.match(require);
            boolean object2Matches = mode2.match(require);

            if (object1Matches == object2Matches) {
                return 0;
            }

            if (object1Matches) {
                return -1;
            }

            return 1;
        }
    }

    public String toString() {
        return getClass() + "[" + features.stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
    }
}
