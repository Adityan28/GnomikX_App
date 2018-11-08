package com.gnomikx.www.gnomikx.Data;

/**
 * Class to act as Object for Genetic Tests Registration (POJO)
 */

public class GeneticTest {

    private int mTestImageID;
    private String mRegisterButtonText;

    public GeneticTest(int testimageID,String registerbuttonText){
        this.mTestImageID = testimageID;
        this.mRegisterButtonText = registerbuttonText;
    }

    public int getmTestImageID() {
        return mTestImageID;
    }

    public String getmRegisterButtonText() {
        return mRegisterButtonText;
    }

    public void setmTestImageID(int mTestImageID) {
        this.mTestImageID = mTestImageID;
    }

    public void setmRegisterButtonText(String mRegisterButtonText) {
        this.mRegisterButtonText = mRegisterButtonText;
    }
}
