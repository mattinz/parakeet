package mattin.parakeet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

        Button playButton = findViewById(R.id.start_stop_button);
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
        final View loadingBackground = findViewById(R.id.loading_background);
        final ProgressBar loadingSpinner = findViewById(R.id.loading_spinner);
        if(isVisible) {
            loadingBackground.setVisibility(View.VISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
        } else {
            loadingBackground.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadingBackground.setVisibility(View.GONE);
                }
            });
            loadingSpinner.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadingSpinner.setVisibility(View.GONE);
                }
            });
        }
    }
}
