package ch.zhaw.prog2.io.picturedb;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/* This test-application reads some picture data from terminal,
 * saves it to the datasource, read it from the DB and prints the result
 */

public class PictureImport {
    private static final String PICTUREDB = "handout/PictureDB/db/picture-data.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static PrintWriter out = new PrintWriter(System.out, true);
    private DataSource ds;
    private static Logger logger;

    public static void main (String[] args) throws IOException {
        PictureDatasource dataSource = new FilePictureDatasource(PICTUREDB);
        Picture picture = createPicture();
        dataSource.insert(picture);
        Picture readPicture = dataSource.findById(picture.getId());

        logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("handout/PictureDB/logger/picturedb.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("My first log");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (readPicture != null) {
            logger.info("The following pictures has been saved: ");
            out.println(readPicture);
        } else {
            logger.warning("Picture with id=" + picture.getId() + " not found.");
        }

        Collection<Picture> pictures = dataSource.findAll();
        logger.info("Pictures:");
        for (Picture pict : pictures) {
            logger.fine(picture.toString());
        }
    }

    static Picture createPicture() {
        // asks the values for the objects
        System.out.println("** Create a new picture **");
        String urlString = prompt("Picture URL: ");
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        String title = prompt("Picture title: ");

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date(); // now
        try {
            date = df.parse(prompt("Picture time ("+DATE_FORMAT+"): "));
        } catch (ParseException e) {
            System.out.println("Unknown date format. Using "+date.toString());
        }

        float longitude = 0.0f;
        try {
            longitude = Float.parseFloat(prompt("Picture position longitude: "));
        } catch (NumberFormatException e) {
            System.out.println("Unknown number format. Using " + longitude);
        }
        float latitude = 0.0f;
        try {
            latitude = Float.parseFloat(prompt("Picture position latitude: "));
        } catch (NumberFormatException e) {
            System.out.println("Unknown number format. Using " + latitude);
        }



        return new Picture(url, new Date(), title, longitude, latitude);
    }

    // prompt function -- to read input string
    static String prompt(String prompt) {
        try {
            StringBuffer buffer = new StringBuffer();
            System.out.println(prompt);
            System.out.println();
            InputStreamReader in = new InputStreamReader(System.in);
            int c = in.read();
            while (c != '\n' && c != -1) {
                buffer.append((char) c);
                c = in.read();
            }
            return buffer.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
}

