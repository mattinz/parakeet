package mattin.parakeet;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.net.URL;
import java.util.List;

public class VoiceSynthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_synthesizer);

        final RecyclerView voiceInfoRecyclerView = findViewById(R.id.voiceInfoRecyclerView);
        setupVoiceInfoRecyclerView(voiceInfoRecyclerView);

        final VoiceSynthViewModel viewModel = ViewModelProviders.of(this).get(VoiceSynthViewModel.class);
        setViewModelObservers(viewModel, voiceInfoRecyclerView);

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText messageInput = VoiceSynthActivity.this.findViewById(R.id.messageInput);
                viewModel.setMessage(messageInput.getText().toString());
                viewModel.playMessageWithVoice();
            }
        });
    }

    private void setupVoiceInfoRecyclerView(RecyclerView voiceInfoRecyclerView) {
        voiceInfoRecyclerView.setHasFixedSize(true);
        voiceInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setViewModelObservers(final VoiceSynthViewModel viewModel, final RecyclerView voiceInfoRecyclerView) {
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                setLoadingScreenVisibility(isLoading);
            }
        });

        viewModel.getVoiceInfoList().observe(this, new Observer<List<VoiceInfo>>() {
            @Override
            public void onChanged(@Nullable List<VoiceInfo> voiceInfos) {
                voiceInfoRecyclerView.setAdapter(new VoiceInfoRecyclerAdapter(voiceInfos, new IRecyclerViewClickListener() {
                    @Override
                    public void onItemClick(int adapterPosition) {
                        viewModel.setSelectedVoice(adapterPosition);
                    }
                }));
            }
        });
    }

    private void setLoadingScreenVisibility(boolean isVisible) {

    }
}
