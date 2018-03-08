package mattin.parakeet;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Debug;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Mattin on 3/6/2018.
 */

public class AudioStreamMediaPlayer {
    public void playFromStream(URL stream) {
        if (stream != null) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(stream.toString());
            } catch (IOException e) {
                Log.d(getClass().getName(), "Unable to stream.");
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            mediaPlayer.prepareAsync();
        }
    }
}
