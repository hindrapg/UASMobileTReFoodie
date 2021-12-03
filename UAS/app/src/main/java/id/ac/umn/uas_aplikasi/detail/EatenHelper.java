package id.ac.umn.uas_aplikasi.detail;

import java.util.Date;

public class EatenHelper {
    public EatenHelper() {
    }

    String idUser, idEaten, foodName, foodCal;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodCal() {
        return foodCal;
    }

    public void setFoodCal(String foodCal) {
        this.foodCal = foodCal;
    }

    public Date getEatenDate() {
        return eatenDate;
    }

    public void setEatenDate(Date eatenDate) {
        this.eatenDate = eatenDate;
    }

    Date eatenDate;
    public EatenHelper(String idUser, String foodName, String foodCal) {
        this.idUser = idUser;
        this.foodName = foodName;
        this.foodCal = foodCal;
    }


}
