package com.hcmut.admin.utrafficsystem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class HealthFacility implements Parcelable {

    private String _id;
    private String name;
    private String address;
    private String work_time;
    private String[] specialisation;
    private String service;
    private String phone_number;
    private float latitude;
    private float longitude;
    //private HealthFacilityComment[] comments;
    private List<Comment> comment;
    private Number[] location;
    private float rate;
    private int numberOfRate;

    public static class Comment{

        private String userId;
        private String nameUser;
        private String message;
        private Integer statusSend = 0;
        private Integer like = 0;

        public static final Creator<Comment> CREATOR = new Creator<Comment>() {
            @Override
            public Comment createFromParcel(Parcel in) {
                return new Comment(in);
            }

            @Override
            public Comment[] newArray(int size) {
                return new Comment[size];
            }
        };
        public Comment(String userId, String nameUser, String message){
            this.nameUser = nameUser;
            this.userId = userId;
            this.message = message;
        }
        public Comment(String userId, String nameUser, String message, Integer like){
            this.userId = userId;
            this.nameUser = nameUser;
            this.message = message;
            this.like = like;
        }
        public Comment(Parcel in) {
            userId = in.readString();
            nameUser = in.readString();
            message = in.readString();
            statusSend = in.readInt();
            like = in.readInt();
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNameUser() {
            return nameUser;
        }

        public void setNameUser(String nameUser) {
            this.nameUser = nameUser;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getStatusSend() {
            return statusSend;
        }

        public void setStatusSend(Integer statusSend) {
            this.statusSend = statusSend;
        }

        public Integer getLike() {
            return like;
        }

        public void setLike(Integer like) {
            this.like = like;
        }



        public static class LikeComment{
            private Integer indexComment;

            public static final Creator<LikeComment> CREATOR = new Creator<LikeComment>() {
                @Override
                public LikeComment createFromParcel(Parcel in) {
                    return new LikeComment(in);
                }

                @Override
                public LikeComment[] newArray(int size) {
                    return new LikeComment[size];
                }
            };
            public LikeComment(Integer indexComment){
                this.indexComment = indexComment;
            }

            public LikeComment(Parcel in) {
                indexComment = in.readInt();
            }

            public Integer getIndexComment() {
                return indexComment;
            }

            public void setIndexComment(Integer indexComment) {
                this.indexComment = indexComment;
            }

        }

    }

    protected HealthFacility(Parcel in) {
        _id = in.readString();
        name = in.readString();
        address = in.readString();
        work_time = in.readString();
        specialisation = in.createStringArray();
        service = in.readString();
        phone_number = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        comment = in.createTypedArrayList(Comment.CREATOR);
        rate = in.readFloat();
        numberOfRate = in.readInt();
    }

    public static final Creator<HealthFacility> CREATOR = new Creator<HealthFacility>() {
        @Override
        public HealthFacility createFromParcel(Parcel in) {
            return new HealthFacility(in);
        }

        @Override
        public HealthFacility[] newArray(int size) {
            return new HealthFacility[size];
        }


    };

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public String[] getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String[] specialisation) {
        this.specialisation = specialisation;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getNumberOfRate() {
        return numberOfRate;
    }

    public void setNumberOfRate(int numberOfRate) {
        this.numberOfRate = numberOfRate;
    }


    /*public HealthFacilityComment[] getComments() {
        return comments;
    }

    public void setComments(HealthFacilityComment[] comments) {
        this.comments = comments;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(work_time);
        dest.writeStringArray(specialisation);
        dest.writeString(service);
        dest.writeString(phone_number);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeList(comment);
        dest.writeFloat(rate);
        dest.writeInt(numberOfRate);
    }
}