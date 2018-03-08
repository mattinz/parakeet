package mattin.parakeet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceInfoRecyclerAdapter extends RecyclerView.Adapter<VoiceInfoRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView gender;
        TextView language;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.voice_name);
            gender = itemView.findViewById(R.id.gender_output);
            language = itemView.findViewById(R.id.language_output);
        }
    }

    private List<VoiceInfo> voiceInfoList;

    public VoiceInfoRecyclerAdapter(List<VoiceInfo> voiceInfoList) {
        this.voiceInfoList = voiceInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()). inflate(R.layout.recycler_item_voice_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VoiceInfo voiceInfo = voiceInfoList.get(position);
        holder.name.setText(voiceInfo.getName());
        holder.gender.setText(voiceInfo.getGender());
        holder.language.setText(voiceInfo.getLanguage());
    }

    @Override
    public int getItemCount() {
        return voiceInfoList.size();
    }
}
