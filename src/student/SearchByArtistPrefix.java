package student;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Search by Artist Prefix searches the artists in the song database for artists
 * that begin with the input String
 *
 * @author Jed Newcomb
 */
public class SearchByArtistPrefix {

    // keep a local direct reference to the song array
    private Song[] songs;

    /**
     * constructor initializes the property.
     *
     * @param sc a SongCollection object
     */
    public SearchByArtistPrefix(SongCollection sc) {
        songs = sc.getAllSongs();
    }

    /**
     * find all songs matching artist prefix uses binary search should operate
     * in time log n + k (# matches) converts artistPrefix to lowercase and
     * creates a Song object with artist prefix as the artist in order to have a
     * Song to compare. walks back to find the first "beginsWith" match, then
     * walks forward adding to the arrayList until it finds the last match.
     *
     * @param artistPrefix all or part of the artist's name
     * @return an array of songs by artists with substrings that match the
     * prefix
     */
    public Song[] search(String artistPrefix) {
        Song[] result = null;
        artistPrefix = artistPrefix.toLowerCase();
        //created new song instance for searching
        Song key = new Song(artistPrefix, "none", "none");

        Song.CmpArtist cmp = new Song.CmpArtist();

        int artistLength = artistPrefix.length();

        int i = Arrays.binarySearch(songs, key, cmp);

        System.out.println("Index from binary Search: " + i);
        int temp = ((CmpCnt)cmp).getCmpCnt();
 
        System.out.println("Binary search comparisions: "
                + temp);
        ((CmpCnt) cmp).resetCmpCnt();

        if (i < 0) {
            i = -i - 1;
        }

        System.out.println("Front found at " + i);

        ArrayList<Song> songList = new ArrayList<>();
        if (i >= 0) {

            while (i >= 0
                    && songs[i].getArtist().length() >= artistLength
                    && songs[i].getArtist().substring(0, artistLength).
                            compareToIgnoreCase(artistPrefix) == 0) {
                cmp.cmpCnt += 1;
                i--;
            }

            i++;

            while (i < songs.length
                    && songs[i].getArtist().length() >= artistLength
                    && songs[i].getArtist().substring(0, artistLength).
                            compareToIgnoreCase(artistPrefix) == 0) {
                cmp.cmpCnt += 1;
                songList.add(songs[i]);

                i++;
            }

            result = new Song[songList.size()];
            songList.toArray(result);
        }

        System.out.println("Comparisons to build the list: "
                + ((CmpCnt) cmp).getCmpCnt());
        System.out.println("Actual complexity " + (temp + cmp.getCmpCnt())+"\n");
        System.out.println("k is " + result.length);
        int log = (int)((Math.log(10514)) / Math.log(2));
        System.out.println("log_2(n) = " + log);
        System.out.println("Theoretical complexity k + log n is: " + (log + result.length));
        System.out.println("\nTotal number of matches for \"" + artistPrefix
                + "\" is: " + result.length);
        
        
        
        Stream.of(result).limit(10).forEach(System.out::println);
        return result;
    }

    /**
     * testing method for this unit
     *
     * @param args command line arguments set in Project Properties - the first
     * argument is the data file name and the second is the partial artist name,
     * e.g. be which should return beatles, beach boys, bee gees, etc.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }

        SongCollection sc = new SongCollection(args[0]);
        SearchByArtistPrefix sbap = new SearchByArtistPrefix(sc);

        if (args.length > 1) {
            System.out.println("Searching for: " + args[1]);
            Song[] byArtistResult = sbap.search(args[1]);

            Stream.of(sc).limit(10).forEach(System.out::println);
        }
    }
}
