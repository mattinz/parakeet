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
    private int selectedVoiceIndex;
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
                }
            }
        });
        audioStreamMediaPlayer = new AudioStreamMediaPlayer();
        selectedVoiceIndex = 0;
        currentMessage = "";
    }

    public boolean playMessageWithVoice() {
        boolean didPlay = false;
        List<VoiceInfo> list = voiceInfoList.getValue();
        if(list != null && list.get(selectedVoiceIndex) != null && !TextUtils.isEmpty(currentMessage)) {
            URL voiceStream = synthesizer.getSynthesizedVoiceStream(currentMessage, list.get(selectedVoiceIndex));
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

    public int getSelectedVoiceIndex() {
        return selectedVoiceIndex;
    }

    public void setSelectedVoice(int index) {
        if(index >= 0) {
            selectedVoiceIndex = index;
        }
    }

    public void setMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            currentMessage = message;
        }
    }
}
