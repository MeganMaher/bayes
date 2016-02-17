/**
 * Created by maherm on 2/12/16.
 */
public class Patient {
    private int id;
    private int gender;
    private String bloodType;
    private double weight;
    private boolean hasVirus;

    public static final int MALE = 0;
    public static final int FEMALE = 1;

    public Patient(int id, int gender, String bloodType, double weight, boolean hasVirus) {
        this.id = id;
        this.gender = gender;
        this.bloodType = bloodType;
        this.weight = weight;
        this.hasVirus = hasVirus;
    }

    public int getId() {
        return id;
    }

    public int getGender() {
        return gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public double getWeight() {
        return weight;
    }

    public boolean hasVirus() {
        return hasVirus;
    }
}