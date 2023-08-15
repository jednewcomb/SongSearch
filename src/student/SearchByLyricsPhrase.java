package student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 *
 * @author Jed Newcomb
 */
public class SearchByLyricsPhrase {
    // array of songs where the lyrics of that song contains every word in the phrase being search for
    private Song[] byLyricsResults;
    // map of key song ranking and song sets
    private static TreeMap<Integer, TreeSet<Song>> phraseSongMap;
    // holds a song where the lyrics contain the phrase in order
    private TreeSet<Song> songMatch;
    private SearchByLyricsWords sblw;
    // holds all songs where the lyrics contain the phrase in order
    private ArrayList<Song> songMatchList;

    /**
     * holds the ranking of song 
     */
    public int rank;

    /**
     * Parameterized constructor for SearchByLyricsPhrase
     * @param sc - The collection of songs
     */
    public SearchByLyricsPhrase(SongCollection sc) {
        sblw = new SearchByLyricsWords(sc);
    }

    /**
     * Search method
     *
     * @param lyricsPhrase
     * @return
     */
    public Song[] search(String lyricsPhrase) {
        phraseSongMap = new TreeMap<>();
        byLyricsResults = sblw.search(lyricsPhrase);
        songMatchList = new ArrayList<>();

        for (Song s : byLyricsResults) {
            songMatch = new TreeSet<>();
            songMatch.clear();
            rank = 0;

            int fromIndex = 0;
            int indexVal = 0;

            String lyrics = s.getLyrics().toLowerCase();

            // ArrayList that holds all the indexes where the first word in the phrase was found
            ArrayList<Integer> firstPhraseWord = new ArrayList<Integer>();
            ArrayList<String> phrase = new ArrayList<String>();
            phrase.addAll(Arrays.asList(lyricsPhrase.split(" ")));

            // findWord = single word out of lyricsPhrase to search for
            String firstWord = phrase.get(0);
            for (int i = 0; i < lyrics.length(); i++) {

                indexVal = lyrics.indexOf(firstWord, fromIndex);
                fromIndex = indexVal + firstWord.length();

                if (indexVal == -1) {
                    break;
                } else if (lyrics.charAt(fromIndex) == 32) {
                    firstPhraseWord.add(indexVal);
                }
            }

            rank = rankSong(lyrics, phrase, firstPhraseWord);
            if (rank == 0) {
                continue;
            }
            if (phraseSongMap.containsKey(rank)) {
                songMatch = phraseSongMap.get(rank);

                songMatch.add(s);
                songMatchList.add(s);

                phraseSongMap.put(rank, songMatch);
            } else {
                songMatch.add(s);
                songMatchList.add(s);

                phraseSongMap.put(rank, songMatch);
            }
        }

        Song[] results = new Song[songMatchList.size()];
        results = songMatchList.toArray(results);

        return results;
    }

    /**
     *
     * @param lyrics contains the lyrics from one song
     * @param phrase contains the phrase being searched for in the lyrics
     * @param firstPhraseWord contains all the indexes where the first word in the phrase
     *                        was found in the lyrics
     * @return the ranking for that lyrics
     */
    public int rankSong(String lyrics, ArrayList<String> phrase, ArrayList<Integer> firstPhraseWord) {
        int fromIndex = 0;
        int indexVal = 0;
        int firstIndex = 0;
        int lastIndex = 0;

        ArrayList<Integer> songRank = new ArrayList<Integer>();
        for (int i = 0; i < firstPhraseWord.size(); i++) {
            // sets starting search index by adding index value in firstPhraseWord to length of first word in phrase
            fromIndex = firstPhraseWord.get(i) + phrase.get(0).length();
            firstIndex = firstPhraseWord.get(i);
            // skips first word in phrase. We already found all the indexes for first word which are contained
            // in the ArrayList firstPhrasaeWord
            for (int j = 1; j < phrase.size(); j++) {
                // findWord = next word out of lyricsPhrase to search for
                String findWord = phrase.get(j);
                do {
                    indexVal = lyrics.indexOf(findWord, fromIndex);
                    if (indexVal == -1) {
                        break;
                    }
                    // change starting index from current index + length of search word
                    fromIndex = indexVal + findWord.length();

                    if (indexVal == -1) {
                        break;
                    }
                    // continue while loop if index after word found does not contain a space(32), period(46), LF(10),
                    // apostrophe(44) or a question mark(63)
                } while (lyrics.charAt(fromIndex) != 32 && lyrics.charAt(fromIndex) != 46
                        && lyrics.charAt(fromIndex) != 10 && lyrics.charAt(fromIndex) != 44
                        && lyrics.charAt(fromIndex) != 63);

                // checks to see if beginning index for ranking has been set already
                if (indexVal == -1) {
                    break;
                }

                // set index value for last index
                lastIndex = fromIndex;

                if (j == phrase.size() - 1) {
                    rank = lastIndex - firstIndex;
                    songRank.add(rank);
                }
            }
        }
        if (songRank.isEmpty()) {
            songRank.add(0);
        } else {
            Collections.sort(songRank);
        }
        return songRank.get(0);
    }

    /**
     * Testing method for class.
     *
     * @param args
     */
    public static void main(String[] args) {
        SongCollection sc = new SongCollection("allSongs.txt");
        String phraseToSearch = "she loves you";
        SearchByLyricsPhrase sblp = new SearchByLyricsPhrase(sc);

        // calls search method with returns an arr of Songs that contain the phrase being searched for
        Song[] byTitlePhraseResult = sblp.search(phraseToSearch);
        System.out.println("\nsearching for: " + phraseToSearch);
        System.out.println("Total songs is " + byTitlePhraseResult.length + ", first 10 matches:");

        // for loop that prints the song rankings and the songs that have that rank
        for (Map.Entry<Integer, TreeSet<Song>> entry : phraseSongMap.entrySet()) {
            String key = entry.getKey().toString();
            TreeSet<Song> value = entry.getValue();

            for (Song s : value) {
                System.out.println(key + "  " + s);
            }
        }
    }
}