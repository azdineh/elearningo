package com.sim.elearning;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private Context mContext;
    private List<Session> mSessions;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SessionAdapter(Context mContext, List<Session> mSessions) {
        this.mContext = mContext;
        this.mSessions = mSessions;
    }

    public void add(Session s) {
        mSessions.add(s);
    }

    public void add(int pos,Session s){
        mSessions.add(pos,s);
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.session_item, parent, false);
        return new SessionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {

        Session currentSession = mSessions.get(position);
        holder.textViewModuleName.setText(currentSession.getModulename());
        int snumber = mSessions.size() - position;
        holder.textViewSessionName.setText("Session " + snumber+": "+currentSession.getTitle());
        int scount=currentSession.getFilesContent().size();
        if(scount==1) holder.textviewnafiles.setText(currentSession.getFilesContent().size()+" document included");
        else holder.textviewnafiles.setText(currentSession.getFilesContent().size()+" documents included");
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(Long.parseLong(currentSession.getDate()));
        holder.textViewDate.setText(currentDate);
        holder.textViewDescription.setText(currentSession.getDescription());
        holder.textViewAbsentStudends.setText(currentSession.getAbsentStudents().size()+" absent students marked");

    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    public class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewModuleName;
        public TextView textViewSessionName;
        public TextView textViewDate;
        public TextView textviewnafiles;
        public TextView textViewDescription;
        public TextView textViewAbsentStudends;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewModuleName = itemView.findViewById(R.id.moduleName_textview);
            textViewSessionName = itemView.findViewById(R.id.sessionName_textview);
            textViewDate = itemView.findViewById(R.id.date_textview);
            textviewnafiles = itemView.findViewById(R.id.textviewnafiles);
            textViewDescription = itemView.findViewById(R.id.textview_desc);
            textViewAbsentStudends=itemView.findViewById(R.id.text_view_absent_students);

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

            textViewAbsentStudends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent absenceIntent = new Intent(mContext, AbsenceActivity.class);
                    absenceIntent.putExtra("session", mSessions.get(getAdapterPosition()));
                    mContext.startActivity(absenceIntent);
                    //finish();
                }
            });
        }
    }


}
