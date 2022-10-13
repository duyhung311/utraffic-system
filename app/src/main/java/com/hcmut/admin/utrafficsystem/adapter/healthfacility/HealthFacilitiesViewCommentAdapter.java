package com.hcmut.admin.utrafficsystem.adapter.healthfacility;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.HealthFacility;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities.LikeCommentRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities.StatusSendRequest;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthFacilitiesViewCommentAdapter extends RecyclerView.Adapter<HealthFacilitiesViewCommentAdapter.HealthFacilitiesViewCommentHolder>{

    private Context mContext;
    final ArrayList<HealthFacility.Comment> listComment;
    private ViewCommentAdapterOnClickHandler clickHandler;
    private Fragment HealthFacilitiesViewCommentFragment;
    private String idHF;

    public interface ViewCommentAdapterOnClickHandler {
    }

    public HealthFacilitiesViewCommentAdapter(Context mContext, ArrayList<HealthFacility.Comment> listComment,
                                              String idHF,
                                              ViewCommentAdapterOnClickHandler clickHandler,
                                              Fragment HealthFacilitiesViewCommentFragment) {
        this.mContext = mContext;
        this.listComment = listComment;
        this.clickHandler = clickHandler;
        this.idHF = idHF;
        this.HealthFacilitiesViewCommentFragment = HealthFacilitiesViewCommentFragment;
    }

    @NonNull
    @Override
    public HealthFacilitiesViewCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentView = inflater.inflate(R.layout.item_comment_health_facilities, parent, false);
        HealthFacilitiesViewCommentHolder viewHolder = new HealthFacilitiesViewCommentHolder(commentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HealthFacilitiesViewCommentHolder holder, @SuppressLint("RecyclerView") final int position) {
        final HealthFacility.Comment comment = listComment.get(position);
        holder.nameUserComment.setText(comment.getNameUser());
        holder.textMessageComment.setText(comment.getMessage());
        holder.likeUserComment.setText("Thích");
        holder.countUserComment.setText(comment.getLike().toString());

        holder.likeUserComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.countUserComment.setText(String.valueOf(comment.getLike() + 1));
                pressLikeComment(position, comment.getLike(), comment.getNameUser(), comment.getMessage());
                comment.setLike(comment.getLike() + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listComment == null) {
            return 0;
        }
        return listComment.size();
    }

    class HealthFacilitiesViewCommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameUserComment;
        public TextView textMessageComment;
        public TextView likeUserComment;
        public TextView countUserComment;

        public HealthFacilitiesViewCommentHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameUserComment = itemView.findViewById(R.id.nameUserComment);
            textMessageComment = itemView.findViewById(R.id.textMessageComment);
            likeUserComment = itemView.findViewById(R.id.likeUserComment);
            countUserComment = itemView.findViewById(R.id.countUserComment);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public void pressLikeComment(final int indexComment, final int like, final String userName, final String message){

        final ProgressDialog progressDialog = ProgressDialog.show(HealthFacilitiesViewCommentFragment.getContext(), "", "Đang xử lý..!", true);
        RetrofitClient.getAPIHealthFacilities().postLikeCommentHealthFacilities(new LikeCommentRequest(idHF, indexComment))
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        progressDialog.dismiss();
                        try {
                            if(like + 1 == 10){
                                notificationEmail(userName, message);
                                statusSend(indexComment);
                            }
                            MapActivity.androidExt.showMessageNoAction(
                                    HealthFacilitiesViewCommentFragment.getContext(),
                                        "Thông báo",
                                         "Yêu thích bình luận của bạn đã được hệ thống ghi nhận!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        progressDialog.dismiss();
                        try {
                            MapActivity.androidExt.showMessageNoAction(
                                    HealthFacilitiesViewCommentFragment.getContext(), "Thông báo", "Không thể yêu thích bình luận, vui lòng kiểm tra lại đường truyền");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void statusSend(final int indexComment){
        RetrofitClient.getAPIHealthFacilities().postStatusSendCommentHealthFacilities(new StatusSendRequest(idHF, indexComment))
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        try {
                            MapActivity.androidExt.showMessageNoAction(
                                    HealthFacilitiesViewCommentFragment.getContext(), "Thông báo", "Không thể gửi mail, vui lòng kiểm tra lại đường truyền");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void notificationEmail(String userName, String message){
        try{
            final String stringSenderEmail = "utraffic.ad.bk@gmail.com";
            String stringReceiverEmail = "utraffic.ad.bk@gmail.com";
            final String stringPasswordSenderEmail = "admin.123";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            final MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("[Utraffic] - Bình luận về cơ sở y tế");
            mimeMessage.setText("Bình luận người dùng " + userName + " có lượt thích nổi bật. \n\nID của cơ sở y tế: " + idHF + "\nNội dung bình luận: " + message);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
