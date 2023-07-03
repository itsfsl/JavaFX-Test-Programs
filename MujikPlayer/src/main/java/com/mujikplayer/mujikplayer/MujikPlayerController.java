package com.mujikplayer.mujikplayer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.Scene;

public class MujikPlayerController implements Initializable {

    @FXML
    Button playButton, stopButton, prevButton, nextButton;
    @FXML
    Label songTitleLabel;
    @FXML
    Label artistLabel;
    @FXML
    ProgressBar songProgressBar;
    @FXML
    ComboBox<String> speedComboBox;
    @FXML
    CheckBox muteButton;
    @FXML
    Slider volumeSlider;
    @FXML
    private Button colorMode;
    @FXML
    private void colorMode() {
        Scene scene = colorMode.getScene();
        String currentCSS = scene.getStylesheets().get(0);
        File cssFile = new File(currentCSS);
        String cssFileName = cssFile.getName();
        if (cssFileName.equals("style.css")) {

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("styleDark.css").toExternalForm());
            colorMode.setText("Light Mode");
            colorMode.setTextFill(Color.WHITE);
        }
        else {

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            colorMode.setText("Dark Mode");
            colorMode.setTextFill(Color.BLACK);
        }
    }
    @FXML
    Button minimizeButton;
    @FXML
    Button exitButton;
    @FXML
    private void minimizeWindow() {

        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML
    private void exitWindow() {

        Platform.exit();
        System.exit(0);
    }

    private Media media;
    private MediaPlayer mediaplayer;

    private File[] files;
    private File fileDirectory;
    private ArrayList<File> songList;

    private int songNumber;

    private final int[] speeds = {50, 80, 90, 100, 110, 120, 150};

    Timer timer;
    TimerTask timerTask;

    private boolean songRunning;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        songList = new ArrayList<File>();

        //fileDirectory = new File("C:\\Users\\direc\\Desktop\\final songs");
        //String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        //String filePath = desktopPath + File.separator + "my songs";
        String filePath = "My Music";
        fileDirectory = new File(filePath);

        files = fileDirectory.listFiles();

        if (files != null) {

            for (File file : files) {

                songList.add(file);
            }
        }

        media = new Media(songList.get(songNumber).toURI().toString());
        mediaplayer = new MediaPlayer(media);
        mediaplayer.setVolume(volumeSlider.getValue() * 0.01);
        try {
            AudioFile audioFile = AudioFileIO.read(songList.get(songNumber));
            Tag tag = audioFile.getTag();
            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            songTitleLabel.setText(title);
            artistLabel.setText(artist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //songTitleLabel.setText(songList.get(songNumber).getName().substring(0, songList.get(songNumber).getName().length() - 4));

        for (int i = 0; i < speeds.length; i++) {

            speedComboBox.getItems().add(speeds[i] + "%");
        }

        speedComboBox.setOnAction(this::setSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                mediaplayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    public void playSong() {

        if (!songRunning) {

            setSpeed(null);
            checkMute();
            beginTimer();
            mediaplayer.play();
            playButton.setText("Pause");
        }
        else {

            stopTimer();
            mediaplayer.pause();
            playButton.setText("Play");
        }

    }

    public void stopSong() {

        if (songRunning) {

            stopTimer();
            songProgressBar.setProgress(0);
            mediaplayer.stop();
            playButton.setText("Play");
        }
    }

    public void prevSong() {

        if (songNumber > 0) {

            mediaplayer.stop();
            songNumber--;

            if (songRunning) {

                stopTimer();
            }

            media = new Media(songList.get(songNumber).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            try {
                AudioFile audioFile = AudioFileIO.read(songList.get(songNumber));
                Tag tag = audioFile.getTag();
                String title = tag.getFirst(FieldKey.TITLE);
                String artist = tag.getFirst(FieldKey.ARTIST);
                songTitleLabel.setText(title);
                artistLabel.setText(artist);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //songTitleLabel.setText(songList.get(songNumber).getName().substring(0, songList.get(songNumber).getName().length() - 4));
            playSong();
        }
        else {

            songNumber = songList.size() - 1;
            mediaplayer.stop();

            if (songRunning) {

                stopTimer();
            }

            media = new Media(songList.get(songNumber).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            try {
                AudioFile audioFile = AudioFileIO.read(songList.get(songNumber));
                Tag tag = audioFile.getTag();
                String title = tag.getFirst(FieldKey.TITLE);
                String artist = tag.getFirst(FieldKey.ARTIST);
                songTitleLabel.setText(title);
                artistLabel.setText(artist);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //songTitleLabel.setText(songList.get(songNumber).getName().substring(0, songList.get(songNumber).getName().length() - 4));
            playSong();
        }
    }

    public void nextSong() {

        if (songNumber < songList.size() - 1) {

            mediaplayer.stop();
            songNumber++;

            if (songRunning) {

                stopTimer();
            }

            media = new Media(songList.get(songNumber).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            Platform.runLater(() -> {
                try {
                    AudioFile audioFile = AudioFileIO.read(songList.get(songNumber));
                    Tag tag = audioFile.getTag();
                    String title = tag.getFirst(FieldKey.TITLE);
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    songTitleLabel.setText(title);
                    artistLabel.setText(artist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //songTitleLabel.setText(songList.get(songNumber).getName().substring(0, songList.get(songNumber).getName().length() - 4));
            });
            playSong();
        }
        else {

            songNumber = 0;
            mediaplayer.stop();

            if (songRunning) {

                stopTimer();
            }

            media = new Media(songList.get(songNumber).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            Platform.runLater(() -> {
                try {
                    AudioFile audioFile = AudioFileIO.read(songList.get(songNumber));
                    Tag tag = audioFile.getTag();
                    String title = tag.getFirst(FieldKey.TITLE);
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    songTitleLabel.setText(title);
                    artistLabel.setText(artist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //songTitleLabel.setText(songList.get(songNumber).getName().substring(0, songList.get(songNumber).getName().length() - 4));
            });
            playSong();
        }
    }

    public void checkMute() {

        if (muteButton.isSelected()) {

            mediaplayer.setVolume(0);
        }
        else {

            mediaplayer.setVolume(volumeSlider.getValue() * 0.01);
        }
    }

    public void setSpeed(ActionEvent e) {

        if (speedComboBox.getValue() == null) {

            mediaplayer.setRate(1);
        }
        else {

            mediaplayer.setRate(Integer.parseInt(speedComboBox.getValue().substring(0, speedComboBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                songRunning = true;
                double currentTime, endTime;
                currentTime = mediaplayer.getCurrentTime().toSeconds();
                endTime = media.getDuration().toSeconds();
                songProgressBar.setProgress(currentTime / endTime);

                if (currentTime / endTime == 1) {

                    stopTimer();
                    nextSong();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1);
    }

    public void stopTimer() {

        songRunning = false;
        timer.cancel();
    }
}