package id.ac.umn.uas_aplikasi.login;

public class UserHelper {
    String userID;
    int targetCal;
    int weightTarget;

    public UserHelper(String userID, int targetCal, int weightTarget) {
        this.userID = userID;
        this.targetCal = targetCal;
        this.weightTarget = weightTarget;
    }

    public int getWeightTarget() {
        return weightTarget;
    }

    public void setWeightTarget(int weightTarget) {
        this.weightTarget = weightTarget;
    }

    public UserHelper() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getTargetCal() {
        return targetCal;
    }

    public void setTargetCal(int targetCal) {
        this.targetCal = targetCal;
    }
}
