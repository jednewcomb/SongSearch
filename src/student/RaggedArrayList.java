package student;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * *
 * The RaggedArrayList is a 2 level data structure that is an array of arrays.
 *
 * It keeps the items in sorted order according to the comparator. Duplicates
 * are allowed. New items are added after any equivalent items.
 *
 * @author Jed Newcomb
 * @param <E> A generic data type so that this structure can be built with any
 * data type (object)
 */
public class RaggedArrayList<E> implements Iterable<E> {

    // must be even so when split get two equal pieces
    private static final int MINIMUM_SIZE = 4;

    public int size;

    public Object[] l1Array;

    public int l1NumUsed;

    private Comparator<E> comp;

    /**
     * create an empty list
     *
     * @param c a comparator object
     */
    public RaggedArrayList(Comparator<E> c) {
        size = 0;
        // you can't create an array of a generic type
        l1Array = new Object[MINIMUM_SIZE];
        // first 2nd level array
        l1Array[0] = new L2Array(MINIMUM_SIZE);
        l1NumUsed = 1;
        comp = c;
    }

    /**
     * ***********************************************************
     * nested class for 2nd level arrays
     */
    private class L2Array {

        /**
         * the array of items
         */
        private E[] items;
        /**
         * number of items in this L2Array with values
         */
        private int numUsed;

        /**
         * Constructor for the L2Array
         *
         * @param capacity the initial length of the array
         */
        public L2Array(int capacity) {
            // you can't create an array of a generic type
            items = (E[]) new Object[capacity];
            numUsed = 0;
        }
    }

    /**
     * total size
     *
     * @return total size of the data structure
     */
    public int size() {
        return size;
    }

    /**
     * null out all references so garbage collector can grab them but keep
     * otherwise empty l1Array and 1st L2Array
     */
    public void clear() {
        size = 0;
        // clear all but first l2 array
        Arrays.fill(l1Array, 1, l1Array.length, null);
        l1NumUsed = 1;
        L2Array l2Array = (L2Array) l1Array[0];
        // clear out l2array
        Arrays.fill(l2Array.items, 0, l2Array.numUsed, null);
        l2Array.numUsed = 0;
    }

    /**
     * *********************************************************
     * nested class for a list position used only internally 2 parts: level 1
     * index and level 2 index
     */
    private class ListLoc {

        /**
         * Level 1 index
         */
        private int level1Index;

        /**
         * Level 2 index
         */
        private int level2Index;

        /**
         * Parameterized constructor
         *
         * @param level1Index input value for property
         * @param level2Index input value for property
         */
        public ListLoc(int level1Index, int level2Index) {
            this.level1Index = level1Index;
            this.level2Index = level2Index;
        }

        /**
         * test if two ListLoc's are to the same location
         *
         * @param otherObj the other listLoc
         * @return true if they are the same location and false otherwise
         */
        public boolean equals(Object otherObj) {
            // not really needed since it will be ListLoc
            if (getClass() != otherObj.getClass()) {
                return false;
            }
            ListLoc other = (ListLoc) otherObj;

            return level1Index == other.level1Index
                    && level2Index == other.level2Index;
        }

        /**
         * move ListLoc to next entry when it moves past the very last entry it
         * will be one index past the last value in the used level 2 array. Can
         * be used internally to scan through the array for sublist. Also, can be
         * used to implement the iterator.
         *
         * Author: Jed Newcomb
         */
        public void moveToNext() {
            level2Index++;
            if (level2Index >= ((L2Array) l1Array[level1Index]).numUsed) {
                if (level1Index + 1 < l1NumUsed) {
                    level1Index++;
                    level2Index = 0;
                }
            }
        }
    }

    /**
     * find 1st matching entry, was instructed NOT to use a double
     * nested for loop for this part of the project, which made it
     * a bit more complicated, which is why the comments are heavy.
     *
     * @Author Jed Newcomb
     * @param item the thing we are searching for a place to put.
     * @return ListLoc of 1st matching item or of 1st item greater than the item
     * if no match this might be an unused slot at the end of a level 2 array
     */
    public ListLoc findFront(E item) {
        int i1 = 0;
        int i2 = 0;

        if (size > 0) {
            //our l2Array casted from l1Array and index i1
            L2Array l2Array = (L2Array) l1Array[i1];

            //linear search of Level 1 array. The minus one from the
            //first conditional block comes from the fact that each Level 1 
            //array has at least one empty array at the end.
            while (i1 < l1NumUsed - 1
                    && comp.compare(item, l2Array.items[l2Array.numUsed - 1])
                    > 0) {
                i1++;
                l2Array = (L2Array) l1Array[i1];
            }

            //linear search of l2 array now that we've found the correct
            //Level 1 Array
            while (i2 < l2Array.numUsed - 1
                    && comp.compare(item, l2Array.items[i2]) > 0) {
                i2++;
            }

            if (i1 == l1Array.length) {
                i1 = -1;
            }

            if (i2 == l2Array.items.length) {
                i2 = -1;
            }
            //if else block to check if we need to go back in the
            //Level 1 arrays if i2 is in the first element of any
            //array accept the first l1 array, and the square we
            //are on isn't a match
            if (i2 == 0 && i1 > 0
                    && comp.compare(item, ((L2Array) l1Array[i1]).items[i2]) != 0) {
                //go to previous array
                i1--;
                //back to the last l2 element
                i2 = ((L2Array) l1Array[i1]).numUsed;
                //if the target comes after any elements of the found arrays
            } else if (comp.compare(item, l2Array.items[i2]) > 0) {
                //it must go at the end of the current array
                i1 = l1NumUsed - 1;
                //i2 goes to the i1 array
                i2 = ((L2Array) l1Array[i1]).numUsed;
            }
        }
        return new ListLoc(i1, i2);
    }

    /**
     * find location after the last matching entry or if no match, it finds the
     * index of the next larger item this is the position to add a new entry
     * this might be an unused slot at the end of a level 2 array. Was also
     * instructed not to use a double nested for loop here.
     *
     * Author: Jed Newcomb
     *
     * @param item the thing we are searching for a place to put.
     * @return the location where this item should go
     */
    public ListLoc findEnd(E item) {
        int i1 = l1NumUsed - 1;
        L2Array l2Array = (L2Array) l1Array[i1];
        int i2 = l2Array.numUsed - 1;

        if (size > 0) {
            //linear search of Level 1 Arrays (at element 0) to see where
            //our target belongs.
            while (i1 > 0
                    && comp.compare(item, l2Array.items[0]) < 0) {
                i1--;
                l2Array = (L2Array) l1Array[i1];
            }

            //set i2 to the length of the level 2 Array at i1.
            i2 = l2Array.numUsed - 1;
            //linear search of Level 2 Array at i1.
            while (i2 > 0
                    && comp.compare(item, l2Array.items[0]) < 0) {
                i2--;
            }

            //since our array starts from the back and iterates backwards,
            //set i2 to 0 and linear search that level 2 array for our target.
            if (i2 == l2Array.numUsed - 1
                    && comp.compare(item, ((L2Array) l1Array[i1]).items[i2]) != 0) {
                i2 = 0;
                while (i2 < l2Array.numUsed - 1
                        && comp.compare(item, ((L2Array) l1Array[i1]).items[i2]) >= 0) {
                    i2++;
                }
            }

            //if we've found our target, point at the next element in the
            //level 2 array.
            if (comp.compare(item, ((L2Array) l1Array[i1]).items[i2]) == 0) {
                i2++;
                //if there are duplicates, go to the next element in the
                //level 2 array.
            } else if (comp.compare(item, ((L2Array) l1Array[i1]).items[i2]) > 0) {
                i2++;
            }
        }

        //if i2 is negative, then it should be at element 0.
        if (i2 < 0) {
            i2 = 0;
        }
        return new ListLoc(i1, i2);
    }

    /**
     * add object after any other matching values findEnd will give the
     * insertion position
     *
     * @param item the thing we are searching for a place to put.
     * @return
     */
    public boolean add(E item) {
        // TO DO in part 4 and NOT BEFORE
        ListLoc loc = findEnd(item);

        int i1 = loc.level1Index;
        int i2 = loc.level2Index;

        L2Array l2Array = (L2Array) l1Array[i1];

        //first part is inserting the item in the l2Array at the right index
        //return by findEnd()
        System.arraycopy(l2Array.items, i2, l2Array.items,
                i2 + 1, l2Array.numUsed - i2);

        l2Array.items[i2] = item; //the value at the location index to the item
        l2Array.numUsed++;
        size++;

        if (l2Array.numUsed == l2Array.items.length) {
            if (l2Array.items.length < l1Array.length) {
                l2Array.items = Arrays.copyOf(l2Array.items,
                        (l2Array.items.length) * 2);
            } else {
                L2Array l2ArrayT = new L2Array(l2Array.items.length);

                System.arraycopy(l2Array.items, (l2Array.items.length) / 2,
                        l2ArrayT.items, 0, (l2Array.items.length / 2));

                Arrays.fill(l2Array.items, (l2Array.items.length / 2),
                        l2Array.items.length, null);

                l2Array.numUsed = (l2Array.items.length) / 2;
                l2ArrayT.numUsed = (l2Array.items.length) / 2;

                int j = loc.level1Index + 1;

                System.arraycopy(l1Array, j, l1Array, j + 1, l1NumUsed - j);
                l1Array[j] = l2ArrayT;
                l1NumUsed++;

                if (l1NumUsed == l1Array.length) {
                    l1Array = Arrays.copyOf(l1Array, l1NumUsed * 2);
                }
            }
        }
        return true;
    }

    /**
     * check if list contains a match
     *
     * @author Jed Newcomb
     * @param item the thing we are looking for.
     * @return true if the item is already in the data structure
     */
    public boolean contains(E item) {
        ListLoc itemLocation = findFront(item);
        L2Array position = (L2Array) l1Array[itemLocation.level1Index];

        // if comparsion is 0 (equal) we know our item exists inside the RAL
        return comp.compare(item, position.items[itemLocation.level2Index]) == 0;
    }

    /**
     * copy the contents of the RaggedArrayList into the given array
     *
     * Author: Jed Newcomb
     *
     * @param a - an array of the actual type and of the correct size
     * @return the filled in array
     */
    public E[] toArray(E[] a) {

        //create new iterator
        Iterator<E> itr = this.iterator();

        int count = 0;
        //while we're not at the last item
        while (itr.hasNext()) {
            //
            if (a.length <= count) {
                return a;
            }
            a[count++] = itr.next();
        }
        return a;
    }

    /**
     * returns a new independent RaggedArrayList whose elements range from
     * fromElemnt, inclusive, to toElement, exclusive. The original list is
     * unaffected, findStart and findEnd will be useful here
     * Author: Jed Newcomb
     *
     * @param fromElement the starting element
     * @param toElement the element after the last element we actually want
     * @return the sublist
     */
    public RaggedArrayList<E> subList(E fromElement, E toElement) {
        // TO DO in part 5 and NOT BEFORE

        //new ragged arraylist we're adding to
        RaggedArrayList<E> result = new RaggedArrayList<E>(comp);

        //starting spot
        ListLoc loc = findFront(fromElement);
        //ending spot
        ListLoc stop = findFront(toElement);

        while (!loc.equals(stop)) {
            L2Array l2array = (L2Array) l1Array[loc.level1Index];
            result.add(l2array.items[loc.level2Index]);
            loc.moveToNext();
        }

        return result;
    }
    
    /**
     * A stats method which was used for testing purposes as the
     * project was being worked on.
     *
     * @author Jed Newcomb
     */
    public  void stats() {
        System.out.println("STATS:");
        //int size = this.size();
        System.out.println("list size N = " + size);

        // level 1 array
        int l1NumUsed = this.l1NumUsed;
        System.out.println("level 1 array " + l1NumUsed + " of "
                + this.l1Array.length + " used.");

        // level 2 arrays
        int minL2size = Integer.MAX_VALUE, maxL2size = 0;
        for (int i1 = 0; i1 < this.l1NumUsed; i1++) {
            RaggedArrayList<Song>.L2Array l2array
                    = (RaggedArrayList<Song>.L2Array)
                    (this.l1Array[i1]);
            minL2size = Math.min(minL2size, l2array.numUsed);
            maxL2size = Math.max(maxL2size, l2array.numUsed);
        }
        System.out.printf("level 2 array sizes: min = %d used, avg = %.1f "
                + "used, max = %d used.%n%n",
                minL2size, 
                (double) size / l1NumUsed, maxL2size);
    }

    /**
     * returns an iterator for this list
     *
     * @return an iterator
     */
    public Iterator<E> iterator() {
        return new Itr();

    }

    /**
     * Iterator is just a list loc. It starts at (0,0) and finishes with index2
     * 1 past the last item in the last block
     */
    private class Itr implements Iterator<E> {

        private ListLoc loc;

        /*
         * create iterator at start of list
         *
         */
        Itr() {
            loc = new ListLoc(0, 0);
        }

        /**
         * check to see if there are more items
         * Author: Jed Newcomb
         */
        public boolean hasNext() {
            // TO DO in part 5 and NOT BEFORE
            return loc.level1Index < l1NumUsed - 1
                    || loc.level2Index
                    < ((L2Array) l1Array[loc.level1Index]).numUsed;
        }

        /**
         * return item and move to next throws NoSuchElementException if off end
         * of list. An exception is thrown here because calling next() without
         * calling hasNext() shows a certain lack of awareness on the part of
         * the programmer, or so my professor says.
         * 
         * Author: Jed Newcomb
         */
        public E next() {
            // TO DO in part 5 and NOT BEFORE
            L2Array l2Array = (L2Array) l1Array[loc.level1Index];
            if (loc.level2Index >= l2Array.numUsed) {
                throw new IndexOutOfBoundsException();
            }
            E item = l2Array.items[loc.level2Index];
            loc.moveToNext();
            return item;
        }

    }
}
