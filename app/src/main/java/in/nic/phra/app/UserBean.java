package in.nic.phra.app;

/**
 * Created by ashvi on 06-06-2017.
 * Saves temporary User in.nic.phra.app.data from JSON Script at the time of login
 */

class UserBean {
    private String loginUsername;
    private String userFullName;
    private int userTypeCode;

    UserBean(String loginUsername, String userFullName, int userTypeCode) {
        this.loginUsername = loginUsername;
        this.userFullName = userFullName;
        this.userTypeCode = userTypeCode;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public int getUserTypeCode() {
        return userTypeCode;
    }


}
