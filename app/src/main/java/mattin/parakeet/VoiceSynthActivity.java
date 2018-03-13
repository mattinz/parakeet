package mattin.parakeet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class VoiceSynthActivity extends AppCompatActivity {

    private VoiceSynthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_synthesizer);

        final RecyclerView voiceInfoRecyclerView = findViewById(R.id.voiceInfoRecyclerView);
        setupVoiceInfoRecyclerView(voiceInfoRecyclerView);

        viewModel = ViewModelProviders.of(this).get(VoiceSynthViewModel.class);
        setViewModelObservers(voiceInfoRecyclerView);
        setButtonClickListeners();
        setupEditTexts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null) {
            List<String> messagesList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String message = messagesList.get(0);
            ((EditText) findViewById(R.id.messageInput)).setText(message);
            viewModel.setMessage(message);
            viewModel.playMessageWithVoice();
        }
    }

    private void setupVoiceInfoRecyclerView(RecyclerView voiceInfoRecyclerView) {
        voiceInfoRecyclerView.setHasFixedSize(true);
        voiceInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        voiceInfoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void setViewModelObservers(final RecyclerView voiceInfoRecyclerView) {
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                setLoadingScreenVisibility(isLoading);
            }
        });

        viewModel.getVoiceInfoList().observe(this, new Observer<List<VoiceInfo>>() {
            @Override
            public void onChanged(@Nullable List<VoiceInfo> voiceInfos) {
                boolean isPortrait = getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
                @DimenRes int topDummyHeight = isPortrait ? R.dimen.voice_list_top_dummy_row_height : R.dimen.voice_list_dummy_row_height_landscape;
                @DimenRes int bottomDummyHeight = isPortrait ? R.dimen.voice_list_bottom_dummy_row_height : R.dimen.voice_list_dummy_row_height_landscape;
                VoiceInfoRecyclerAdapter adapter = new VoiceInfoRecyclerAdapter(voiceInfos, topDummyHeight, bottomDummyHeight,
                        new IRecyclerViewClickListener() {
                            @Override
                            public void onItemClick(int adapterPosition) {
                                viewModel.setSelectedVoice(adapterPosition);
                            }
                        });
                adapter.setSelectedVoiceInfo(viewModel.getSelectedVoiceIndex());
                setNoResultsPromptVisibility(voiceInfos.isEmpty());
                voiceInfoRecyclerView.setAdapter(adapter);
            }
        });
    }

    private void setButtonClickListeners() {
        ImageButton playButton = findViewById(R.id.start_stop_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText messageInput = VoiceSynthActivity.this.findViewById(R.id.messageInput);
                viewModel.setMessage(messageInput.getText().toString());
                viewModel.playMessageWithVoice();
            }
        });

        ImageButton micButton = findViewById(R.id.microphone_button);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_listener_prompt));
                startActivityForResult(intent, 0);
            }
        });
    }

    private void setupEditTexts() {
        ((EditText) findViewById(R.id.messageInput)).setText(viewModel.getCurrentMessage());

        EditText searchInput = findViewById(R.id.search_input);
        searchInput.setText(viewModel.getCurrentSearch());
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setSearchQuery(editable.toString());
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

    private void setNoResultsPromptVisibility(boolean isVisible) {
        findViewById(R.id.no_search_results_prompt).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }
}
