package mattin.parakeet;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceInfoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TOP_DUMMY_ROW = 0;
    private static final int VOICE_INFO_ROW = 1;
    private static final int BOTTOM_DUMMY_ROW = 2;

    private IRecyclerViewClickListener clickListener;
    private List<VoiceInfo> voiceInfoList;
    private VoiceInfo selectedVoiceInfo;
    private VoiceInfoViewHolder selectedVoiceInfoViewHolder;
    @DimenRes private int topDummyHeight;
    @DimenRes private int bottomDummyHeight;

    public VoiceInfoRecyclerAdapter(List<VoiceInfo> voiceInfoList,
                                    @DimenRes int topDummyHeight,
                                    @DimenRes int bottomDummyHeight,
                                    IRecyclerViewClickListener clickListener) {
        this.voiceInfoList = new ArrayList<>(voiceInfoList);
        this.clickListener = clickListener;
        this.topDummyHeight = topDummyHeight;
        this.bottomDummyHeight = bottomDummyHeight;
        selectedVoiceInfo = null;
        selectedVoiceInfoViewHolder = null;

        //Nulls will represent dummy views
        voiceInfoList.add(0, null);
        voiceInfoList.add(voiceInfoList.size(), null);
    }

    @Override
    public  int getItemViewType(int position) {
        int viewType = VOICE_INFO_ROW;
        if(position == 0) {
            viewType = TOP_DUMMY_ROW;
        } else if (position == voiceInfoList.size() - 1) {
            viewType = BOTTOM_DUMMY_ROW;
        }
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        ViewHolder viewHolder;
        if(viewType == TOP_DUMMY_ROW || viewType == BOTTOM_DUMMY_ROW) {
            itemView = new View(parent.getContext());
            viewHolder = new EmptyRowViewHolder(itemView);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_voice_info, parent, false);
            viewHolder = new VoiceInfoViewHolder(itemView, null);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder.getItemViewType() == VOICE_INFO_ROW) {
            VoiceInfo voiceInfo = voiceInfoList.get(position);
            if(voiceInfo != null) {
                VoiceInfoViewHolder viewHolder = (VoiceInfoViewHolder) holder;
                viewHolder.voiceInfo = voiceInfo;
                viewHolder.name.setText(voiceInfo.getName());
                viewHolder.gender.setText(voiceInfo.getGender());
                viewHolder.language.setText(voiceInfo.getLanguage());
                viewHolder.radioButton.setChecked(voiceInfo.equals(selectedVoiceInfo));
                if (position == 0 || position == voiceInfoList.size() - 2) {
                    viewHolder.divider.setVisibility(View.GONE);
                }
            }
        } else {
            @DimenRes int rowHeight = holder.getItemViewType() == TOP_DUMMY_ROW ? topDummyHeight : bottomDummyHeight;
            EmptyRowViewHolder viewHolder = (EmptyRowViewHolder) holder;
            viewHolder.setViewHeight(rowHeight);
        }
    }

    @Override
    public int getItemCount() {
        return voiceInfoList.size();
    }

    private class VoiceInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        VoiceInfo voiceInfo;
        final TextView name;
        final TextView gender;
        final TextView language;
        final RadioButton radioButton;
        final  View divider;

        public VoiceInfoViewHolder(View itemView, VoiceInfo voiceInfo) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.voiceInfo = voiceInfo;
            name = itemView.findViewById(R.id.voice_name);
            gender = itemView.findViewById(R.id.gender_output);
            language = itemView.findViewById(R.id.language_output);
            radioButton = itemView.findViewById(R.id.voice_info_item_radio_button);
            divider = itemView.findViewById(R.id.divider);
        }

        @Override
        public void onClick(View v) {
            if(selectedVoiceInfoViewHolder != null) {
                selectedVoiceInfoViewHolder.radioButton.setChecked(false);
            }
            selectedVoiceInfoViewHolder = VoiceInfoViewHolder.this;
            selectedVoiceInfo = voiceInfo;
            radioButton.setChecked(true);
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    private class EmptyRowViewHolder extends ViewHolder {
        final View view;

        public EmptyRowViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setViewHeight(@DimenRes int heightRes) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = view.getContext().getResources().getDimensionPixelSize(heightRes);
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        }
    }
}
