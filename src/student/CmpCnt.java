package student;

import java.util.Comparator;

/**
 * ****************************************************************************
 * A class for adding comparison counting to a comparator in a clean fashion.
 *
 * This is used as a super class when declaring a comparator class, e.g.:
 * public static class CmpArtist extends CmpCnt implements Comparator<Song> {
 *
 * @author Jed Newcomb
 */

public class CmpCnt {

    protected int cmpCnt;

    /**
     * constructor initializes the counter to zero
     */
    public CmpCnt() {
        cmpCnt = 0;
    }
    
    /**
     * resets the counter to 0
     */
    public void resetCmpCnt() {
        cmpCnt = 0;
    }
    
    /**
     * Accessor for the property
     *
     * @return the value of the embedded counter
     */
    public int getCmpCnt() {
        return cmpCnt;
    }
}
