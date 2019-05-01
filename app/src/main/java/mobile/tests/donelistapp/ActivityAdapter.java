package mobile.tests.donelistapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder>{
    private Context context;

    public ActivityAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<ActivityModel> getListActivity() {
        return listActivity;
    }

    private ArrayList<ActivityModel>listActivity;

    public void setListActivity(ArrayList<ActivityModel> listActivity) {
        this.listActivity = listActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String id = getListActivity().get(position).getId();
        String aktivitas = getListActivity().get(position).getActivity();

        holder.txtAktivitas.setText(aktivitas);
    }

    @Override
    public int getItemCount() {
        return getListActivity().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtAktivitas;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAktivitas = (TextView)itemView.findViewById(R.id.txtAktivitas);
        }
    }
}
