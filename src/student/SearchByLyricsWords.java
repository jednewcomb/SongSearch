package student;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * SearchByLyricsWords creates a TreeMap where the keys are words and the values
 * are a TreeSet that contains all Songs containing the key word
 *
 * @author Jed Newcomb
 */
public class SearchByLyricsWords {

    private TreeMap<String, TreeSet<Song>> songMap = new TreeMap<>();

    /**
     * Parameterized Constructor for the class: creates a HashSet of common
     * words to be ignored (not used) by our TreeMap and populates the TreeMap
     * with the correct values
     *
     * @author: Jed Newcomb
     * @param sc : a SongCollection object
     */
    public SearchByLyricsWords(SongCollection sc) {
        Set<String> commonWords = new HashSet<>();

        //A list of common words exists as a txt file in order
        //to simplify the searching/changing of data
        StringBuilder wordsBuilder = new StringBuilder();
        try (Scanner scan = new Scanner(Paths.get("commonWords.txt"))) {

            while (scan.hasNext()) {
                wordsBuilder.append(scan.next() + " ");
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        String words = wordsBuilder.toString();

        // add the commonWords into an array that is split by spaces 
        // therefore each word is stored as its own index and not grouped
        words = words.trim();
        words = words.toLowerCase();
        String[] splitWords = words.split(" ");

        //populate set with common words
        for (int i = 0; i < splitWords.length; i++) {
            commonWords.add(splitWords[i]);
        }

        String lyrics = null;
        List<String> splitLyrics = null;
        ArrayList<String> splitLyricsFinal = new ArrayList<>();
        //this loop goes through each song and creates an arraylist with
        //the correctly formatted list of lyrics word by word so that we 
        //can easily compare them to the songs later.
        for (Song song : sc.getAllSongs()) {

            //lyric string for splitting
            lyrics = song.getLyrics().toLowerCase();

            //splitting operation
            splitLyrics
                    = new ArrayList<>(Arrays.asList(lyrics.split("[^a-zA-Z]+")));

            //check for common words and single character words
            //remove if they are present
            for (int i = splitLyrics.size() - 1; i >= 0; i--) {
                if (commonWords.contains(splitLyrics.get(i))
                        || splitLyrics.get(i).length() <= 1) {
                    splitLyrics.remove(i);
                }
            }

            //remove duplicates
            for (int i = 0; i < splitLyrics.size(); i++) {
                if (!splitLyricsFinal.contains(splitLyrics.get(i))) {
                    splitLyricsFinal.add(splitLyrics.get(i));
                }
            }

            //add to TreeMap
            for (String s : splitLyricsFinal) {
                //if theirs no TreeSet, make a new one
                if (songMap.get(s) == null) {
                    TreeSet<Song> temp = new TreeSet<Song>();
                    temp.add(song);
                    songMap.put(s, temp);
                }
                //otherwise add to the existing TreeSet
                if (songMap.get(s) != null) {
                    songMap.get(s).add(song);
                }

            }
            //clear ArrayList for next loop
            splitLyricsFinal.clear();
        }

    }

    /**
     * A search method which takes in a String of words from the command args
     * and returns an array of the songs which contain the words found in the
     * String.
     *
     * Search method Authors: Jed Newcomb
     *
     * @param lyricsWords - command line arguments String we are searching for
     * @return songs - array of songs containing our searched phrase
     */
    public Song[] search(String lyricsWords) {
        Song[] songs = null;
        List<String> splitLyrics = null;
        Set<String> commonWords = new HashSet<>();

        StringBuilder wordsBuilder = new StringBuilder();
        try (Scanner scan = new Scanner(Paths.get("commonWords.txt"))) {

            while (scan.hasNext()) {
                wordsBuilder.append(scan.next() + " ");
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        String words = wordsBuilder.toString();

        // add the commonWords into an array that is split by spaces 
        // therefore each word is stored as its own index and not grouped
        words = words.trim();
        words = words.toLowerCase();
        String[] splitWords = words.split(" ");

        //populate set with common words
        for (int i = 0; i < splitWords.length; i++) {
            commonWords.add(splitWords[i]);
        }

        //set searched words to lowercase
        lyricsWords = lyricsWords.toLowerCase();

        //split searched words into list
        splitLyrics
                = new ArrayList<>(Arrays.asList(lyricsWords.split("[^a-zA-Z]+")));

        //remove common words and single char words
        for (int i = splitLyrics.size() - 1; i >= 0; i--) {
            if (commonWords.contains(splitLyrics.get(i))
                    || splitLyrics.get(i).length() <= 1) {
                splitLyrics.remove(i);
            }
        }

        //We'll be putting the lyrics of each song found to
        //have the first word in splitLyrics into this list
        List<String> songLyrics = null;
        
        //The final set of songs, which extract from songSet, which will have
        //all the songs containing the first word in the searched phrase
        TreeSet<Song> songSetFinal = new TreeSet<>();
        
        //A set of songs which contain only the first word in the searched phrase
        TreeSet<Song> songSet = songMap.get(splitLyrics.get(0));

        //If there's no word found in the set, meaning it wasn't found in the
        //Map, then we should return 0 songs
        if (songSet != null) {
            
            //otherwise, for each song in songSet, create a list of words
            //from the lyrics of the song and see if they contain the current
            //word we're on in our searched lyrics. If it is found, increment a
            //counter
            for (Song song : songSet) {
                int count = 0;
                for (int i = 1; i < splitLyrics.size(); i++) {

                    String lyrics = song.getLyrics().toLowerCase();

                    songLyrics
                            = new ArrayList<>(Arrays.asList(lyrics.split("[^a-zA-Z]+")));

                    if (songLyrics.contains(splitLyrics.get(i))) {
                        count++;
                    }

                }

                //if the counter is the same as the number of words
                //in the searched phrase, that means in contains each
                //word in the searched phrase, so add it to another set
                //(-1 because our loop starts at 1 because we know the songs
                //have the first word)
                if (count == splitLyrics.size() - 1) {
                    songSetFinal.add(song);
                }

            }
        }
        
        //initialize array to the same size as the set we're about to copy from
        songs = new Song[songSetFinal.size()];

        //copy songs from set to the array
        int i = 0;
        for (Song s : songSetFinal) {
            songs[i++] = s;
        }
        
        return songs;

    }

    /**
     * Gathers statistics about the TreeMap and prints them
     *
     * @author Jed Newcomb
     */
    public void statistics() {

        int songCount = 0;

        System.out.println("Number of Keys: " + songMap.size());

        //loop which counts the total number of references in the TreeSets
        //within the TreeMap
        for (Map.Entry<String, TreeSet<Song>> entry : songMap.entrySet()) {
            for (Song song : entry.getValue()) {
                songCount++;
            }

        }

        System.out.println("Number of song references found in all sets in the"
                + " map: " + songCount);

        System.out.println("Space taken in bytes by Map: "
                + songMap.size() * 8);

        System.out.println("Space taken in bytes by Set: " + songCount * 8);

        System.out.println("Total size of data structure: " + ((songCount * 8)
                + (songMap.size() * 8)));

        //where k is the number of keys in the map and n is the total 
        //number of song references in all the sets
        System.out.println("Space usage expressed as function of N: 8K + 8N");

    }

    /**
     * testing method for this unit
     *
     * @author : Jed Newcomb
     * @param args command line arguments set in Project Properties - the first
     * argument is the data file name and the second is the partial artist name,
     * e.g. be which should return beatles, beach boys, bee gees, etc.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songFile [search string]");
            return;
        }
        
        SongCollection sc = new SongCollection(args[0]);
        SearchByLyricsWords test = new SearchByLyricsWords(sc);

        if (args.length > 1) {
            System.out.println("Searching for: " + args[1]);
            Song[] titleResult = test.search(args[1]);

            System.out.println("Matching Titles: " + titleResult.length);
            Stream.of(titleResult).limit(10).forEach(System.out::println);
        }
    }
}
