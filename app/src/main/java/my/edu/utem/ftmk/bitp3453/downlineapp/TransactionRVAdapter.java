package my.edu.utem.ftmk.bitp3453.downlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionRVAdapter extends RecyclerView.Adapter<TransactionRVAdapter.ViewHolder>{

    private ArrayList<Transaction> transactionsArrayList;
    private Context context;

    // creating constructor for our adapter class
    public TransactionRVAdapter(ArrayList<Transaction> transactionsArrayList, Context context) {
        this.transactionsArrayList = transactionsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_history_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Transaction transaction = transactionsArrayList.get(position);
        holder.tvItemCode.setText(transaction.getItemcode());
        holder.tvCustName.setText(transaction.getCustomername());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = transaction.getTransactiondate().toDate();
        String transdate = formatter.format(date);
        //String sub = (date.toString()).substring(0,10);

        holder.tvTransactionDate.setText(transdate.substring(0,10));
        //holder.tvTransactionDate.setText(date.toString());
        //holder.tvTransactionDate.setText(transdate);

        holder.tvCustAddress.setText(transaction.getCustomeraddress());
        holder.tvCustPhone.setText(transaction.getCustomerphone());
        holder.tvQuantity.setText(transaction.getQuantity());

        String sales = "RM " + transaction.getSales();
        String profit = "RM " + transaction.getProfit();

        holder.tvSales.setText(sales);
        holder.tvProfit.setText(profit);
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return transactionsArrayList.size();
    }

    public interface ItemClickListener {
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView tvItemCode;
        private final TextView tvCustName;
        private final TextView tvCustAddress;
        private final TextView tvCustPhone;
        private final TextView tvTransactionDate;
        private final TextView tvQuantity;
        private final TextView tvSales;
        private final TextView tvProfit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvCustName = itemView.findViewById(R.id.tvCustName);
            tvCustAddress = itemView.findViewById(R.id.tvCustAddress);
            tvCustPhone = itemView.findViewById(R.id.tvCustPhone);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSales = itemView.findViewById(R.id.tvSales);
            tvProfit = itemView.findViewById(R.id.tvProfit);

        }
    }

    // convenience method for getting data at click position
    public Transaction getItem(int id) {
        return transactionsArrayList.get(id);
    }

}
