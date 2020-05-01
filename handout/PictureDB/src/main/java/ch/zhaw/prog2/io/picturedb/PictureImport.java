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

/* This test-application reads some picture data from terminal,
 * saves it to the datasource, read it from the DB and prints the result
 */

public class PictureImport {
    private static final String PICTUREDB = "db/picture-data.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static PrintWriter out = new PrintWriter(System.out, true);
    private DataSource ds;

    public static void main (String[] args) throws IOException {
        PictureDatasource dataSource = new FilePictureDatasource(PICTUREDB);
        Picture picture = createPicture();
        dataSource.insert(picture);
        Picture readPicture = dataSource.findById(picture.getId());
        if (readPicture != null) {
            out.println("The following pictures has been saved: ");
            out.println(readPicture);
        } else {
            out.println("Picture with id=" + picture.getId() + " not found.");
        }

        Collection<Picture> pictures = dataSource.findAll();
        System.out.println("Pictures:");
        for (Picture pict : pictures) {
            System.out.println(picture.toString());
        }
    }

    static Picture createPicture() {
        // asks the values for the objects
        out.println("** Create a new picture **");
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
            out.println("Unknown date format. Using "+date.toString());
        }

        float longitude = 0.0f;
        try {
            longitude = Float.parseFloat(prompt("Picture position longitude: "));
        } catch (NumberFormatException e) {
            out.println("Unknown number format. Using " + longitude);
        }
        float latitude = 0.0f;
        try {
            latitude = Float.parseFloat(prompt("Picture position latitude: "));
        } catch (NumberFormatException e) {
            out.println("Unknown number format. Using " + latitude);
        }

        return new Picture(url, new Date(), title, longitude, latitude);
    }

    // prompt function -- to read input string
    static String prompt(String prompt) {
        try {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
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

