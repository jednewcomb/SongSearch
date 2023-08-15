package student;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * SearchByTitlePrefix uses the data from SongCollection to build a RAL of
 * songs ordered by title
 *
 * @author : Jed Newcomb
 */
public class SearchByTitlePrefix {
    private RaggedArrayList<Song> ral;
    private Comparator<Song> comp;
    // direct local reference to the songArray 
    //(like we use in SearchByArtistPrefix)
    private Song[] songs;

    /**
     * Parameterized class constructor, which is our ragged array list, a comparator
     * for song comparison, and the array of songs
     *
     * @author : Jed Newcomb
     * @param sc : a SongCollection object
     */
    public SearchByTitlePrefix(SongCollection sc) {
        // songs represents a copy of all the songs in the "AllSongs" txt file
        songs = sc.getAllSongs();
        comp = new Song.CmpTitle();

        // precautionary measure to reset the comparison counter to ensure 
        // that we start our comparison from the correct point.
        ((CmpCnt) comp).resetCmpCnt();

        // ral represents the RaggedArrayList 
        // our matching songs will be stored in
        ral = new RaggedArrayList<Song>(comp);

        for (int i = 0; i < songs.length; i++) {
            ral.add(songs[i]);
        }

        System.out.println(((CmpCnt)comp).getCmpCnt());
        ral.stats();

    }

    /**
     * search returns all Songs matching the "titleString"
     * @author : Jed Newcomb
     * @param titleString : title prefix that for which matches should be found
     * in the songs
     * @return : an array of songs
     */
    public Song[] search(String titleString) {
        
        String fromElement = titleString.toLowerCase();
        
        //dummy char that we will use to change the last letter of our word,
        //used to eventually stop the search
        char dummy = titleString.charAt(titleString.length() - 1);
        //turns 'l' into 'm' in the case of "Angel"
        dummy++;

        //we used a stringbuilder to take the contents of every letter
        //in the word except the last, and then put the new last character
        //at the end of it.
        StringBuilder toElement = new StringBuilder();
        toElement.append(titleString);
        toElement.setCharAt(titleString.length() - 1, dummy);
        
        //string conversion for easier code later
        String toElementString = toElement.toString().toLowerCase();
        
        
        //dummy prefixes to hand to sublist for our search
        Song standardPrefix = new Song("none", fromElement, "none");
        Song endPrefix = new Song("none", toElementString, "none");
        
        ((CmpCnt) comp).resetCmpCnt();
        
        RaggedArrayList<Song> matches = ral.subList(standardPrefix, endPrefix);
        matches.stats();
        
        Song[] array = new Song[matches.size()];
        
        array = (Song[]) matches.toArray(array);
        System.out.println("Comparisons to build the list: " +
                ((CmpCnt)comp).getCmpCnt());

        System.out.println("Theoretical complexity: " + (Math.sqrt(10514) + array.length));
        return array;
    }

    /**
    * testing method for the class
    * @author: Jed Newcomb
    * 
    * @param args command line properties set inside "Project Properties"
    * the first being AllSongs.txt- a text file containing all the song objects
    * the second being the partial title prefix that should return 
    * the first ten song matches of each search
    */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songFile [search string]");
            return;
        }
        SongCollection sc = new SongCollection(args[0]);
        SearchByTitlePrefix test = new SearchByTitlePrefix(sc);

        if (args.length > 1) {
            System.out.println("Searching for: " + args[1]);
            Song[] titleResult = test.search(args[1]);

            System.out.println("Matching Titles: " + titleResult.length);
            Stream.of(titleResult).limit(10).forEach(System.out::println);
        }
    }

}
