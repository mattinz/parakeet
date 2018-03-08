package mattin.parakeet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.amazonaws.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceSynthViewModel extends AndroidViewModel {
    private VoiceSynthesizer synthesizer;
    private AudioStreamMediaPlayer audioStreamMediaPlayer;

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<VoiceInfo>> voiceInfoList;
    private VoiceInfo selectedVoice;
    private String currentMessage;

    public VoiceSynthViewModel(@NonNull Application application) {
        super(application);

        synthesizer = new VoiceSynthesizer(application, new VoiceSynthesizer.ReadinessListener() {
            @Override
            public void onVoiceSynthesizerReady() {
                isLoading.setValue(false);
                List<VoiceInfo> voiceInfos = synthesizer.getVoiceInfoList();
                if(!voiceInfos.isEmpty()) {
                    voiceInfoList.setValue(voiceInfos);
                    selectedVoice = voiceInfos.get(0);
                }
            }
        });
        audioStreamMediaPlayer = new AudioStreamMediaPlayer();
        selectedVoice = null;
        currentMessage = "";
    }

    public boolean playMessageWithVoice() {
        boolean didPlay = false;
        if(selectedVoice != null && !TextUtils.isEmpty(currentMessage)) {
            URL voiceStream = synthesizer.getSynthesizedVoiceStream(currentMessage, selectedVoice);
            if(voiceStream != null) {
                audioStreamMediaPlayer.playFromStream(voiceStream);
                didPlay = true;
            }
        }
        return didPlay;
    }

    public LiveData<Boolean> getIsLoading() {
        if(isLoading == null) {
            isLoading = new MutableLiveData<>();
            isLoading.setValue(true);
        }
        return isLoading;
    }

    public LiveData<List<VoiceInfo>> getVoiceInfoList() {
        if(voiceInfoList == null) {
            voiceInfoList = new MutableLiveData<>();
            voiceInfoList.setValue(new LinkedList<VoiceInfo>());
        }
        return voiceInfoList;
    }

    public void setSelectedVoice(int index) {
        if(voiceInfoList != null && !voiceInfoList.getValue().isEmpty()) {
            selectedVoice = voiceInfoList.getValue().get(index);
        }
    }

    public void setMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            currentMessage = message;
        }
    }
}
