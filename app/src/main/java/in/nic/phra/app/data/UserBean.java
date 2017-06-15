package in.nic.phra.app.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ashvi on 06-06-2017.
 * Saves temporary User in.nic.phra.app.data from JSON Script at the time of login
 */

public class UserBean implements Parcelable {
    private String loginUsername;
    private String userFullName;
    private int userTypeCode;

    public String getLoginUsername() {
        return loginUsername;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public int getUserTypeCode() {
        return userTypeCode;
    }

    public void logout() {
        loginUsername = null;
        userFullName = null;
        userTypeCode = 0;
    }

    //Parcelable methods below
    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     */
    public static final Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public UserBean(String loginUsername, String userFullName, int userTypeCode) {
        this.loginUsername = loginUsername;
        this.userFullName = userFullName;
        this.userTypeCode = userTypeCode;
    }

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     *
     * @param in a parcel to read this object
     */
    private UserBean(Parcel in) {
        this.loginUsername = in.readString();
        this.userFullName = in.readString();
        this.userTypeCode = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(loginUsername);
        out.writeString(userFullName);
        out.writeInt(userTypeCode);
    }
}
