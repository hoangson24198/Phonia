package com.example.krahs.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krahs.model.Chat;
import com.example.krahs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public ChatAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, final int position) {

        Chat chat = mChat.get(position);
        String time = mChat.get(position).getTime();
        //convert time stamp to do dd/MM/yy
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.show_message.setText(chat.getMessage());
        holder.time.setText(datetime);

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(imageurl).into(holder.profile_image);
        }

        //click to show delete dialog
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //show delete message dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Xóa tin nhắn");
                builder.setMessage("Bạn có muốn xóa tin nhắn này không?");
                //delete button
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                //cancel button
                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                //tạo và show dialog
                builder.create().show();
                return false;
            }
        });

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen"); //trạng thái đã đọc
            } else {
                holder.txt_seen.setText("Delivered"); //trạng thái đã nhận
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    //hàm xóa tin nhắn
    private void deleteMessage(int position) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*Logic
         * Lấy time stamp của tin nhắn được click vào
         * So sánh time stamp đó với time stamp của tất cả tin nhắn trong List "Chats"
         * Thời gian nào bằng thì xóa
         * */
        String msgTimeStamp = mChat.get(position).getTime();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = databaseReference.orderByChild("time").equalTo(msgTimeStamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    /*Chỉ có người gửi được quyền xóa tin nhắn
                     * Ý tưởng:so sánh id sender của tin nhắn với id user hiện tại,giống nhau thì cho phép*/
                    if (ds.child("sender").getValue().equals(currentUserId)) {
                        //Thiết lại lại giá trị của tin nhắn đó thành "Tin nhắn đã xóa"

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "Tin nhắn đã xóa...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(mContext, "Đã xóa", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Bạn chỉ được xóa tin nhắn của bạn!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen, time;
        public LinearLayout messageLayout; //for click listener to show delete

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            time = itemView.findViewById(R.id.time);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
