package student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * SongCollection.java Reads the specified data file and build an array of
 * songs.
 *
 * @author Jed Newcomb
 */
public class SongCollection {

    private Song[] songs;

    /**
     * Parse the file containing the songs and the song info and put them
     * into our database, the array of songs.
     *
     * @param filename The path and filename to the datafile that we are using
     * must be set in the Project Properties as an argument.
     */
    public SongCollection(String fileName) {

        // use a try catch block
        // read in the song file and build the songs array
        // there are several ways to read in the lyrics correctly.  
        // the line feeds between lines and the blank lines between verses
        // must be retained.
        try {
            Scanner inFile = new Scanner(new File(fileName));
            ArrayList<Song> listOfSongs = new ArrayList<>();
            StringBuilder build = new StringBuilder();

            if (!inFile.hasNext()) {
                System.err.println("No data in file " + fileName);
                return;
            }

            while (inFile.hasNextLine()) {
                String artistLine = inFile.nextLine();
                String artist = artistLine.substring(8, artistLine.length() - 1);

                String titleLine = inFile.nextLine();
                String title = titleLine.substring(7, titleLine.length() - 1);

                String lyrics = inFile.nextLine();
                //might be bad to have a while in a while, there must be a more
                //elegant way to do this????
                build.append(lyrics.substring(8));

                //we can thank our partner for this, it is much more efficient
                //and doesn't create any errors.
                while (!(lyrics = inFile.nextLine()).startsWith("\"")) {
                    build.append("\n").append(lyrics);
                }

                Song song = new Song(artist, title, build.toString());
                listOfSongs.add(song);
                build.setLength(0);
            }

            songs = listOfSongs.toArray(new Song[listOfSongs.size() - 1]);

            inFile.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.err.println("file " + fileName + " not found.");
            System.exit(1);
        }
        // sort the songs array using Arrays.sort (see the Java API)
        // this will use the compareTo() in Song to do the job.

        Arrays.sort(songs);
    }

    /**
     * this is used as the data source for building other data structures
     *
     * @return the songs array
     */
    public Song[] getAllSongs() {
        return songs;
    }

    /**
     * unit testing method
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile");
            return;
        }

        SongCollection sc = new SongCollection(args[0]);
        Song[] list = sc.getAllSongs();

        System.out.println("The number of songs is: " + list.length);
        Stream.of(list).limit(10).forEach(System.out::println);
    }
}
