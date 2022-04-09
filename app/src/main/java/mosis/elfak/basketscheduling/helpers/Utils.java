package mosis.elfak.basketscheduling.helpers;

public class Utils {

    public static boolean isPointInCircle(double centerX, double centerY, double pointX, double pointY, double radius){
        double _distance;
        _distance = Math.sqrt(Math.pow(centerX - pointX, 2) + Math.pow(centerY - pointY, 2));
        if (_distance <= radius){
            return true;
        }else{
            return false;
        }
    }
}
