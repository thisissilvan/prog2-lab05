package ch.zhaw.prog2.io.picturedb;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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


    /**
     * Creates the FilePictureDatasource with the given file as datafile.
     *
     * @param filepath of the file to use as database file.
     * @throws IOException if accessing or creating the file failes
     */
    public FilePictureDatasource(String filepath) throws IOException {

    }


    @Override
    public void insert(Picture picture) {

    }

    @Override
    public void update(Picture picture) throws RecordNotFoundException {

    }

    @Override
    public void delete(Picture picture) throws RecordNotFoundException {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Picture findById(int id) {
        return null;
    }

    @Override
    public Collection<Picture> findAll() {
        return null;
    }

    @Override
    public Collection<Picture> findByPosition(float longitude, float latitude, float deviation) {
        return null;
    }

}
