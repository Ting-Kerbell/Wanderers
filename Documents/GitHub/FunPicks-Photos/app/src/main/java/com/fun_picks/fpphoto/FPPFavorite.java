package com.fun_picks.fpphoto;

/**
 * Created by james on 11/4/15.
 */
public class FPPFavorite {

    long sequenceNumber;
    String jpgFileName;

    public FPPFavorite(long sequenceNumber, String jpgFileName) {
        this.sequenceNumber = sequenceNumber;
        this.jpgFileName = jpgFileName;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getJpgFileName() {
        return jpgFileName;
    }

    public void setJpgFileName(String jpgFileName) {
        this.jpgFileName = jpgFileName;
    }
}
