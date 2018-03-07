package mattin.parakeet;

import android.content.Context;

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
import java.util.List;
import java.util.Map;

/**
 * Created by Mattin on 3/6/2018.
 */
//TODO: This might not need to be a single just because I only want one of them.
    // If it doesn't maintain some kind of expensive connection, don't make it a singleton.
public class VoiceSynthesizer {

    private static final Regions POLLY_REGION = Regions.US_EAST_1;

    private final AmazonPollyPresigningClient pollyClient;
    private Map<String, String> voiceNameToIdMap;

    public VoiceSynthesizer(Context context) {
        pollyClient = new AmazonPollyPresigningClient(new CognitoCachingCredentialsProvider(context,
                context.getString(R.string.aws_cognito_pool_id), POLLY_REGION));

        DescribeVoicesResult result  = pollyClient.describeVoices(new DescribeVoicesRequest());
        List<Voice> voiceList = result.getVoices();
        voiceNameToIdMap = new HashMap<>();
        for(Voice voice : voiceList) {
            voiceNameToIdMap.put(voice.getName(), voice.getId());
        }
    }

    public List<String> getVoiceNames() {
        return new ArrayList<>(voiceNameToIdMap.keySet());
    }

    public URL getSynthesizedVoiceStream(String message, String voiceName) {
        URL voiceStream = null;
        String voiceId = voiceNameToIdMap.get(voiceName);
        if(voiceId != null) {
            SynthesizeSpeechPresignRequest request = new SynthesizeSpeechPresignRequest()
                    .withText(message)
                    .withVoiceId(voiceId)
                    .withOutputFormat(OutputFormat.Mp3);
            voiceStream = pollyClient.getPresignedSynthesizeSpeechUrl(request);
        }
        return voiceStream;
    }
}
