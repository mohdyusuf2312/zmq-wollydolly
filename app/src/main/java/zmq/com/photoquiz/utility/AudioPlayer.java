package zmq.com.photoquiz.utility;

import android.content.Context;
import android.media.MediaPlayer;


public class AudioPlayer {
    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static void playSound(Context context, int mp3File) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, mp3File);
//        mediaPlayer.setVolume(1.0f,0.0f);
        mediaPlayer.start();
    }

    public static void playBackground(Context context, int mp3File) {
        mediaPlayer = MediaPlayer.create(context, mp3File);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f,0.0f);
        mediaPlayer.start();
    }

    public static void stopSound() {
        mediaPlayer.stop();
    }
}