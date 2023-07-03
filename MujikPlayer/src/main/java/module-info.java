module com.mujikplayer.mujikplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires jaudiotagger;


    opens com.mujikplayer.mujikplayer to javafx.fxml;
    exports com.mujikplayer.mujikplayer;
}