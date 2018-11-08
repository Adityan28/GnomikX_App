package com.gnomikx.www.gnomikx.Data;

/**
 * Class to act as the object for registering patients (POJO)
 */

public class RegisterPatients {

    private int patientNumber;
    private String patientName;
    private UserDetail doctorDetails;

    public RegisterPatients(){
        //necessary empty public constructor
    }

    public RegisterPatients(int patientNumber,String patientName, UserDetail doctorDetails){
        this.patientNumber = patientNumber;
        this.patientName = patientName;
        this.doctorDetails = doctorDetails;
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public UserDetail getDoctorDetails() {
        return doctorDetails;
    }

    public void setPatientNumber(int patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorDetails(UserDetail doctorDetails) {
        this.doctorDetails = doctorDetails;
    }
}
