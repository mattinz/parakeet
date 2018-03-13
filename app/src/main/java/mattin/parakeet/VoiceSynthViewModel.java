package mattin.parakeet;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceSynthViewModel extends AndroidViewModel {
    private VoiceSynthesizer synthesizer;
    private AudioStreamMediaPlayer audioStreamMediaPlayer;

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<VoiceInfo>> voiceInfoResultList;
    private List<VoiceInfo> voiceInfoList;
    private int selectedVoiceIndex;
    private String currentMessage;
    private String currentSearch;

    public VoiceSynthViewModel(@NonNull Application application) {
        super(application);

        synthesizer = new VoiceSynthesizer(application, new VoiceSynthesizer.ReadinessListener() {
            @Override
            public void onVoiceSynthesizerReady() {
                isLoading.setValue(false);
                List<VoiceInfo> voiceInfos = synthesizer.getVoiceInfoList();
                if(!voiceInfos.isEmpty()) {
                    voiceInfoList = voiceInfos;
                    setSearchQuery(currentSearch);
                }
            }
        });
        audioStreamMediaPlayer = new AudioStreamMediaPlayer();
        selectedVoiceIndex = 0;
        currentMessage = application.getString(R.string.default_message);
        currentSearch = "";
    }

    public boolean playMessageWithVoice() {
        boolean didPlay = false;
        List<VoiceInfo> list = voiceInfoResultList.getValue();
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
        if(voiceInfoResultList == null) {
            voiceInfoResultList = new MutableLiveData<>();
            voiceInfoResultList.setValue(new LinkedList<VoiceInfo>());
        }
        return voiceInfoResultList;
    }

    public int getSelectedVoiceIndex() {
        return selectedVoiceIndex;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public String getCurrentSearch() {
        return currentSearch;
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

    public void setSearchQuery(String query) {
        currentSearch = query.trim();
        if(TextUtils.isEmpty(currentSearch)) {
            voiceInfoResultList.setValue(voiceInfoList);
        } else {
            List<VoiceInfo> results = new LinkedList<>();
            results.clear();
            for(VoiceInfo voiceInfo : voiceInfoList) {
                if(voiceInfo.getName().toLowerCase().contains(currentSearch.toLowerCase())
                        || voiceInfo.getLanguage().toLowerCase().contains(currentSearch.toLowerCase())
                        || voiceInfo.getGender().toLowerCase().contains(currentSearch.toLowerCase())) {
                    results.add(voiceInfo);
                }
            }
            voiceInfoResultList.setValue(results);
        }
    }
}
