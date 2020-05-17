package ch.zhaw.prog2.io.picturedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/*
Fragen 4. b)

1. Wenn für maxId 0 gewählt wird und dann mittels Math.max
die grössere der beiden ids gewählt wird (MaxId=0, id), kann
man die maximale id finden.

2. Man iteriert das File durch und vergleicht die ID's der records,
bis man den findet, welcher updated werden muss. Sobald bald man den
gefunden hat kann man raus aus der Schlaufe und die Zeile ersetzen.

3. Wenn die Zeile mit der Nummer(record Id) und dem DELIMETER; (Bspw. 1;)
beginnt, dann wird die gesamte Zeile gelöscht.
 */

/**
 * Implements the PictureDatasource Interface storing the data in
 * Character Separated Values (CSV) format, where each line consists of a record
 * whose fields are separated by the DELIMITER ";"
 * See example file: db/picture-data.csv
 */
public class FilePictureDatasource implements PictureDatasource {
    private static final String DELIMITER = ";";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat DF = new SimpleDateFormat(DATE_FORMAT);
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private File newFile;
    private File tempFile;


    /**
     * Creates the FilePictureDatasource with the given file as datafile.
     *
     * @param filepath of the file to use as database file.
     * @throws IOException if accessing or creating the file failes
     */
    public FilePictureDatasource(String filepath) throws IOException {
        super();
        this.newFile = new File(filepath);
        checkAndCreateFile();
        this.tempFile = new File("handout/PictureDB/db/" + "temp_" + newFile.getName());
    }

    @Override
    public void insert(Picture picture) {
        boolean inserted = false;

        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET));
             BufferedWriter out = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(tempFile), CHARSET)))
        {
            int maxId = 0;
            String record;
            // copy entries to tempSource and find maxId
            while ((record = in.readLine()) != null) {
                int id = parseId(record);
                maxId = Math.max(id, maxId);
                out.write(record);
                out.newLine();
            }
            // set new Id to picture and add new picture record
            picture.setId(maxId+1);
            out.write(writePicture(picture));
            out.newLine();
            inserted = true;
        } catch (IOException e) {
            throw new RuntimeException("File operation failed: insert(item): " + picture, e);
        } finally {
            // if the insert was successful move tempSource to dataSource file
            if (inserted) {
                swapFiles();
            } else {
                cleanupTemp();
            }
        }
    }

    @Override
    public void update(Picture picture) throws RecordNotFoundException {
        boolean updated = false;
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET));
             BufferedWriter out = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(tempFile), CHARSET)))
        {
            String prefix = "" + picture.getId() + DELIMITER;
            String record;
            // find record to update and write new record, otherwise simply copy old record
            while ((record = in.readLine()) != null) {
                if (!updated && record.startsWith(prefix)) {
                    out.write(writePicture(picture));
                    updated = true;
                } else {
                    out.write(record);
                }
                out.newLine();
            }
            if (!updated) {
                throw new IOException("Updateing picture failed, no rows affected.");
            }
        } catch (IOException e) {
            throw new RuntimeException("File operation failed: insert(item): " + picture, e);
        } finally {
            // if the update was successful move tempSource to dataSource file
            if (updated) {
                swapFiles();
            } else {
                cleanupTemp();
            }
        }
    }

    @Override
    public void delete(Picture picture) throws RecordNotFoundException {
        boolean deleted = false;
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET));
             BufferedWriter out = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(tempFile), CHARSET)))
        {
            String prefix = "" + picture.getId() + DELIMITER;
            String record;
            // find record to delete and do not write, otherwise simply copy old record
            while ((record = in.readLine()) != null) {
                if (!deleted && record.startsWith(prefix)) {
                    // do write nothing
                    deleted = true;

                } else {
                    out.write(record);
                    out.newLine();
                }
            }
            if (!deleted) {
                throw new IOException("Deleting picture failed, no rows affected.");
            }
        } catch (IOException e) {
            throw new RuntimeException("File operation failed: delete(item): " + picture, e);
        } finally {
            // if the delete was successful move tempSource to dataSource file
            if (deleted) {
                swapFiles();
            } else {
                cleanupTemp();
            }
        }
    }

    @Override
    public int count() {
        int count = 0;
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET)))
        {
            while ((in.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException("File operation failed: count(): ", e);
        }
        return count;
    }

    @Override
    public Picture findById(int id) {
        Picture pict = null;
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET)))
        {
            String prefix = "" + id + DELIMITER;
            String record;
            while ((record = in.readLine()) != null) {
                if (record.startsWith(prefix)) {
                    pict = parsePicture(record);
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("File operation failed: findById(id): " + id, e);
        }
        return pict;

    }

    @Override
    public Collection<Picture> findAll() {
        List<Picture> pictures = new ArrayList<Picture>();
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET)))
        {
            String record;
            while ((record = in.readLine()) != null) {
                Picture pict = parsePicture(record);
                pictures.add(pict);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("File operation failed: findAll(): ", e);
        }
        return pictures;
    }

    @Override
    public Collection<Picture> findByPosition(float longitude, float latitude, float deviation) {
        List<Picture> pictures = new ArrayList<Picture>();
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new FileInputStream(newFile), CHARSET)))
        {
            String record;
            while ((record = in.readLine()) != null) {
                Picture pict = parsePicture(record);
                if (pict.getLongitude() >= longitude-deviation
                    && pict.getLongitude() <= longitude+deviation
                    && pict.getLatitude() >= latitude-deviation
                    && pict.getLatitude() <= latitude+deviation)
                {
                    pictures.add(pict);
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("File operation failed: findByPosition(): ", e);
        }
        return pictures;
    }

    private int parseId(String record) {
        String[] fields = record.split(DELIMITER);
        return Integer.parseInt(fields[0]);
    }

    private Picture parsePicture(String record)
        throws MalformedURLException, ParseException {
        String[] fields = record.split(DELIMITER);
        Picture pict = new Picture(
            Integer.parseInt(fields[0]),
            new URL(fields[5].trim()),
            DF.parse(fields[1].trim()),
            fields[4].trim(),
            Float.parseFloat(fields[2]),
            Float.parseFloat(fields[3]));
        return pict;
    }

    private String writePicture(Picture pict) {
        if (pict == null) {
            throw new IllegalArgumentException("Picture must not be null");
        }
        return new StringBuilder()
            .append(pict.getId()).append(DELIMITER)
            .append(DF.format(pict.getDate())).append(DELIMITER)
            .append(pict.getLongitude()).append(DELIMITER)
            .append(pict.getLatitude()).append(DELIMITER)
            .append(pict.getTitle()).append(DELIMITER)
            .append(pict.getUrl().toExternalForm()).append(DELIMITER)
            .toString();
    }

    private void checkAndCreateFile() {
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void swapFiles(){
        if (tempFile.exists()){
            try {
                Files.move(Paths.get(tempFile.getAbsolutePath()),
                    Paths.get(newFile.getAbsolutePath()),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanupTemp() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

}

