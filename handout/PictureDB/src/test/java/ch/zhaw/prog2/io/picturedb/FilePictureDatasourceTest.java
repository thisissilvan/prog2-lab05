package ch.zhaw.prog2.io.picturedb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilePictureDatasourceTest {
    PictureDatasource filePictureDatasource;

    @Mock
    Picture pictureOne;

    @Mock
    Picture pictureTwo;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(pictureOne.getId()).thenReturn(1);
        when(pictureTwo.getId()).thenReturn(0);
        when(pictureOne.getDate()).thenReturn(new Date(2020, 05, 17, 12, 2, 24));
        when(pictureTwo.getDate()).thenReturn(null);
        when(pictureOne.getLatitude()).thenReturn(3.343f);
        when(pictureTwo.getLatitude()).thenReturn(0f);
        when(pictureOne.getLongitude()).thenReturn(4.344f);
        when(pictureTwo.getLongitude()).thenReturn(0f);
        when(pictureOne.getUrl()).thenReturn(new URL("https://www.dasbild.de"));
        when(pictureTwo.getUrl()).thenReturn(null);
        when(pictureOne.getTitle()).thenReturn("Chihuahuas");
        when(pictureTwo.getTitle()).thenReturn(null);

        filePictureDatasource = new FilePictureDatasource("handout/PictureDB/src/test/resources/test-picture-db.csv");
    }

    @Test
    void insertShouldPass() {
    }

    @Test
    void insertShouldFail() {
    }

    @Test
    void updateShouldPass() {
    }

    @Test
    void updateShouldFail() {
    }

    @Test
    void deleteShouldPass() {
    }

    @Test
    void deleteShouldFail() {
    }

    @Test
    void countShouldPass() {
    }

    @Test
    void countShouldFail() {
    }

    @Test
    void findByIdShouldPass() {
    }

    @Test
    void findByIdShouldFail() {
    }

    @Test
    void findAllShouldPass() {
    }

    @Test
    void findAllShouldFail() {
    }

    @Test
    void findByPositionShouldPass() {
    }

    @Test
    void findByPositionShouldFail() {
    }
}
