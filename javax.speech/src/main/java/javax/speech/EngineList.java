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

// Comp. 2.0.6

/**
 * Container for a set of EngineMode objects.
 * <p>
 * An EngineList is used in the selection of speech engines in conjunction
 * with the methods of the EngineManager class. It provides convenience methods
 * for the purpose of testing and manipulating the EngineMode objects it contains.
 * <p>
 * An EngineList object is typically obtained through the
 * availableEngines method of the javax.speech.EngineManager class.
 * The orderByMatch, anyMatch, requireMatch and rejectMatch methods
 * are used to prune the list to find the best match given multiple criteria.
 * @see javax.speech.EngineMode
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)
 */
public class EngineList {

    /**
     * The features in this list.
     */
    private List<EngineMode> features;

    /**
     * Constructs the initial EngineList.
     * @param features the initial list of features
     */
    public EngineList(EngineMode[] features) {
        this.features = new ArrayList<>(features.length);
        Collections.addAll(this.features, features);
    }

    /**
     * Returns true if one or more EngineMode in the
     * EngineList match the required features.
     * <p>
     * The require object is tested with the match
     * method of each EngineMode in the list.
     * If any match call returns true then this method returns true.
     * <p>
     * anyMatch is often used to test whether pruning a list
     * (with requireMatch or rejectMatch)
     * would leave the list empty.
     * @param require required features to match
     * @return true if a matching engine exists
     * @see javax.speech.EngineMode#match(javax.speech.EngineMode)
     * @see javax.speech.EngineList#requireMatch(javax.speech.EngineMode)
     * @see javax.speech.EngineList#rejectMatch(javax.speech.EngineMode)
     */
    public boolean anyMatch(EngineMode require) {
        // A match for require==null is handled by the match methods
        for (EngineMode mode : features) {
            if (mode.match(require)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the EngineMode at the specified index.
     * @param index an index into this list
     * @return the EngineMode at the specified index
     * @throws java.lang.ArrayIndexOutOfBoundsException if an invalid index was given
     * @see javax.speech.EngineList#elements()
     * @see javax.speech.EngineList#size()
     */
    public EngineMode elementAt(int index) throws ArrayIndexOutOfBoundsException {
        try {
            return features.get(index);
        } catch (IndexOutOfBoundsException e) {
            // for compatibility
            throw new ArrayIndexOutOfBoundsException(e.getMessage());
        }
    }

    /**
     * Returns an Enumeration of the components of this list.
     * @return an Enumeration of the components of this list.
     * @see java.util.Enumeration
     * @see javax.speech.EngineList#elementAt(int)
     */
    public Enumeration<EngineMode> elements() {
        return Collections.enumeration(features);
    }

    /**
     * Orders this list so that elements matching the required features are at
     * the head of the list, and others are at the end.
     * <p>
     * Within categories, the original order of the list is preserved.
     * <p>
     * <A/>
     * Example:
     * <p>
     * // Put running engines at the head of the list.
     * EngineList list = ....;
     * // Running is the 3rd feature in the constructor
     * EngineMode mode = new EngineMode(null, null, true, null, null);
     * list.orderByMatch(mode);
     *
     * @param require features to match
     * @see javax.speech.EngineMode#match(javax.speech.EngineMode)
     */
    public void orderByMatch(EngineMode require) {
        if (require == null) {
            return;
        }
        Comparator<EngineMode> comparator = new EngineListComparator(require);
        Sorter.sort(features, comparator);
    }

    /**
     * Removes EngineMode entries from the list that match reject.
     * <p>
     * The match method for each EngineMode
     * in the list is called and
     * if it returns true it is removed from the list.
     * <p>
     * <A/>
     * Example:
     * <p>
     * // Remove EngineMode objects if they support US English.
     * EngineList list = ....;
     * EngineMode mode = new EngineMode(new Locale("en", "US"));
     * list.rejectMatch(mode);
     *
     * @param reject features to reject
     * @see javax.speech.EngineMode#match(javax.speech.EngineMode)
     */
    public void rejectMatch(EngineMode reject) {
        List<EngineMode> cleaned = new ArrayList<>();

        for (EngineMode mode : features) {
            if (!mode.match(reject)) {
                cleaned.add(mode);
            }
        }

        features = cleaned;
    }

    /**
     * Removes the EngineMode at the specified index.
     * <p>
     * The index must be a value greater than or equal to 0 and
     * less than the current size of the list.
     * @param index the index of the EngineMode to remove
     * @throws java.lang.ArrayIndexOutOfBoundsException if the index was invalid.
     * @see javax.speech.EngineList#elementAt(int)
     * @see javax.speech.EngineList#size()
     */
    public void removeElementAt(int index) throws ArrayIndexOutOfBoundsException {
        try {
            features.remove(index);
        } catch (IndexOutOfBoundsException e) {
            // for compatibility
            throw new ArrayIndexOutOfBoundsException(e.getMessage());
        }
    }

    /**
     * Removes EngineMode entries from the list that
     * do not match require.
     * <p>
     * The match method for each EngineMode
     * in the list is called and
     * if it returns false it is removed from the list.
     * <p>
     * <A/>
     * Example:
     * <p>
     * // Require EngineMode objects to support US English.
     * EngineList list = ....;
     * EngineMode mode = new EngineMode(new Locale("en", "US"));
     * list.requireMatch(mode);
     *
     * @param require features to match
     * @see javax.speech.EngineMode#match(javax.speech.EngineMode)
     */
    public void requireMatch(EngineMode require) {
        List<EngineMode> cleaned = new ArrayList<>();

        for (EngineMode mode : features) {
            if (mode.match(require)) {
                cleaned.add(mode);
            }
        }

        features = cleaned;
    }

    /**
     * Returns the number of EngineMode objects in this list.
     * @return the number of EngineMode objects in this list.
     */
    public int size() {
        return features.size();
    }

    /**
     * @author Dirk Schnelle Note: this comparator imposes orderings that are
     * inconsistent with equals.
     */
    private static class EngineListComparator implements Comparator<EngineMode> {
        final EngineMode require;

        public EngineListComparator(EngineMode require) {
            this.require = require;
        }

        @Override
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
        return getClass() +
                "[" +
                features.stream().map(Object::toString).collect(Collectors.joining(",")) +
                "]";
    }
}
