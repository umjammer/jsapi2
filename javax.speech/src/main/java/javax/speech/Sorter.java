/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 3 $
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

import java.util.Comparator;
import java.util.List;


class Sorter {

    private static <T> void quicksort(Comparator<T> comparator, List<T> vec, int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        T mid;

        // pause for redraw
        if (hi0 > lo0) {

            /*
             * Arbitrarily establishing partition element as the midpoint of the
             * array.
             */
            mid = vec.get((lo0 + hi0) / 2);

            // loop through the array until indices cross
            while (lo <= hi) {
                /*
                 * find the first element that is greater than or equal to the
                 * partition element starting from the left Index.
                 */
                while ((lo < hi0) && (comparator.compare(vec.get(lo), mid) < 0)) {
                    ++lo;
                }

                /*
                 * find an element that is smaller than or equal to the
                 * partition element starting from the right Index.
                 */
                while ((hi > lo0) && (comparator.compare(vec.get(hi), mid) > 0)) {
                    --hi;
                }

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    swap(vec, lo, hi);
                    // pause

                    ++lo;
                    --hi;
                }
            }

            /*
             * If the right index has not reached the left side of array must
             * now sort the left partition.
             */
            if (lo0 < hi) {
                quicksort(comparator, vec, lo0, hi);
            }

            /*
             * If the left index has not reached the right side of array must
             * now sort the right partition.
             */
            if (lo < hi0) {
                quicksort(comparator, vec, lo, hi0);
            }

        }
    }

    private static <T> void swap(List<T> vec, int i, int j) {
        T object1 = vec.get(i);
        T object2 = vec.get(j);

        vec.set(i, object2);
        vec.set(j, object1);
    }

    public static <T> void sort(List<T> vec, Comparator<T> comparator) {
        quicksort(comparator, vec, 0, vec.size() - 1);
    }
}
