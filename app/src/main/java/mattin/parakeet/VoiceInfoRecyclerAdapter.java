package mattin.parakeet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceInfoRecyclerAdapter extends RecyclerView.Adapter<VoiceInfoRecyclerAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        VoiceInfo voiceInfo;
        TextView name;
        TextView gender;
        TextView language;
        RadioButton radioButton;

        public ViewHolder(View itemView, VoiceInfo voiceInfo) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.voiceInfo = voiceInfo;
            name = itemView.findViewById(R.id.voice_name);
            gender = itemView.findViewById(R.id.gender_output);
            language = itemView.findViewById(R.id.language_output);
            radioButton = itemView.findViewById(R.id.voice_info_item_radio_button);
        }

        @Override
        public void onClick(View v) {
            if(selectedViewHolder != null) {
                selectedViewHolder.radioButton.setChecked(false);
            }
            selectedViewHolder = ViewHolder.this;
            selectedVoiceInfo = voiceInfo;
            radioButton.setChecked(true);
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    private IRecyclerViewClickListener clickListener;
    private List<VoiceInfo> voiceInfoList;
    private VoiceInfo selectedVoiceInfo;
    private ViewHolder selectedViewHolder;

    public VoiceInfoRecyclerAdapter(List<VoiceInfo> voiceInfoList, IRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
        this.voiceInfoList = voiceInfoList;
        selectedVoiceInfo = null;
        selectedViewHolder = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()). inflate(R.layout.recycler_item_voice_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView, null);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VoiceInfo voiceInfo = voiceInfoList.get(position);
        holder.voiceInfo = voiceInfo;
        holder.name.setText(voiceInfo.getName());
        holder.gender.setText(voiceInfo.getGender());
        holder.language.setText(voiceInfo.getLanguage());
        holder.radioButton.setChecked(voiceInfo.equals(selectedVoiceInfo));
    }

    @Override
    public int getItemCount() {
        return voiceInfoList.size();
    }
}
