package mattin.parakeet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mattin on 3/6/2018.
 */
public class VoiceSynthesizer {

    public interface ReadinessListener {
        void onVoiceSynthesizerReady();
    }

    private final AmazonPollyPresigningClient pollyClient;
    private List<VoiceInfo> voiceInfoList;
    private boolean isReady;

    public VoiceSynthesizer(Context context, ReadinessListener listener) {
        pollyClient = new AmazonPollyPresigningClient(new CognitoCachingCredentialsProvider(context,
                context.getString(R.string.aws_cognito_pool_id), Regions.US_EAST_2));
        isReady = false;
        getVoiceInfoListAsync(listener);
    }

    public List<VoiceInfo> getVoiceInfoList() {
        return isReady ? voiceInfoList : null;
    }

    public URL getSynthesizedVoiceStream(String message, VoiceInfo voice) {
        URL voiceStream = null;
        if(isReady && voice != null) {
            SynthesizeSpeechPresignRequest request = new SynthesizeSpeechPresignRequest()
                    .withText(message)
                    .withVoiceId(voice.getId())
                    .withOutputFormat(OutputFormat.Mp3);
            voiceStream = pollyClient.getPresignedSynthesizeSpeechUrl(request);
        }
        return voiceStream;
    }

    private void getVoiceInfoListAsync(final ReadinessListener listener) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask fetchVoicesAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                DescribeVoicesResult result  = pollyClient.describeVoices(new DescribeVoicesRequest());
                List<Voice> voiceList = result.getVoices();
                voiceInfoList = new LinkedList<>();
                for(Voice voice : voiceList) {
                    voiceInfoList.add(new VoiceInfo(voice.getId(), voice.getName(), voice.getGender(), voice.getLanguageName()));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object object) {
                isReady = true;
                if(listener != null) {
                    listener.onVoiceSynthesizerReady();
                }
            }
        };
        fetchVoicesAsyncTask.execute();
    }
}
