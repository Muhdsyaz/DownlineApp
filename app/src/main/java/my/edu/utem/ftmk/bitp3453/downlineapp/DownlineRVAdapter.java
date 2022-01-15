package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownlineRVAdapter extends RecyclerView.Adapter<DownlineRVAdapter.ViewHolder> {

    private ArrayList<Downline> downlineArrayList;
    private DownlineRVAdapter.ItemClickListener mClickListener;
    private Context context;
    private AlphaAnimation buttonClicked = new AlphaAnimation(1F,0.8F);

    // creating constructor for our adapter class
    public DownlineRVAdapter(ArrayList<Downline> downlineArrayList, Context context) {
        this.downlineArrayList = downlineArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DownlineRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new DownlineRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.downline_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownlineRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Downline downline = downlineArrayList.get(position);
        holder.tvDownlineID.setText(downline.getId());
        holder.tvDownlineName.setText(downline.getName());
//        holder.tvDownlinePhone.setText(downline.getPhone());
//        holder.tvDownlineAddress.setText(downline.getAddress());

        Date date = downline.getDate().toDate();

//        holder.tvRegisterDate.setText(date.toString());

        //holder.tvRegisterDate.setText(downline.getDate());
//        holder.tvDownlineStatus.setText(downline.getStatus());

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return downlineArrayList.size();
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // creating variables for our text views.
        private final TextView tvDownlineID;
        private final TextView tvDownlineName;
//        private final TextView tvDownlinePhone;
//        private final TextView tvDownlineAddress;
//        private final TextView tvRegisterDate;
//        private final TextView tvDownlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            tvDownlineID = itemView.findViewById(R.id.tvDownlineID);
            tvDownlineName = itemView.findViewById(R.id.tvDownlineName);
//            tvDownlinePhone = itemView.findViewById(R.id.tvDownlinePhone);
//            tvDownlineAddress = itemView.findViewById(R.id.tvDownlineAddress);
//            tvRegisterDate = itemView.findViewById(R.id.tvRegisterDate);
//            tvDownlineStatus = itemView.findViewById(R.id.tvDownlineStatus);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            view.startAnimation(buttonClicked);
        }
    }

    // allows clicks events to be caught
    void setClickListener(DownlineRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public Downline getItem(int id) {
        return downlineArrayList.get(id);
    }

}
