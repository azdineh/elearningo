package com.sim.elearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class InterventionAdapter extends RecyclerView.Adapter<InterventionAdapter.InterventionViewHolder> {

    private Context mContext;
    private List<Intervention> mInterventions;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener linstener) {
        mListener = linstener;
    }



    public InterventionAdapter(Context mContext, List<Intervention> interventions) {
        this.mContext = mContext;
        this.mInterventions = interventions;
    }

    public void add(Intervention v) {
        mInterventions.add(v);
    }


    @NonNull
    @Override
    public InterventionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.intervention_item , parent, false);
        return new InterventionViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull InterventionViewHolder holder, int position) {

        Intervention cIntervention = mInterventions.get(position);

        holder.textViewFullname.setText(cIntervention.getFullname());
        holder.textViewIntervention.setText(cIntervention.getIntervention());
        holder.editTextIntevention.setText(cIntervention.getIntervention());

        SimpleDateFormat sFormatter=new SimpleDateFormat("hh:mm");
        String interventionTime = sFormatter.format(Long.parseLong(cIntervention.getUnixtimestamp()));

        holder.textViewTime.setText(interventionTime);

    }

    @Override
    public int getItemCount() {
        return mInterventions.size();
    }




    public class InterventionViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFullname;
        public TextView textViewIntervention;
        public EditText editTextIntevention;
        public TextView textViewTime;


        public InterventionViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewFullname = itemView.findViewById(R.id.intervention_textViewFullname);
            textViewIntervention = itemView.findViewById(R.id.intervention_textViewIntervention);
            editTextIntevention = itemView.findViewById(R.id.intervention_editTextIntervention);
            textViewTime = itemView.findViewById(R.id.intervention_textViewTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(pos);
                        }
                    }
                }
            });


        }

    }
}
