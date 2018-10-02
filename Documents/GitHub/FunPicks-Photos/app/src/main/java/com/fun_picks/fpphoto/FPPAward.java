
package com.fun_picks.fpphoto;

public class FPPAward {
    long awardId;
    private boolean awardOn;
	long gameMode;
    long creationTime;
    private long awardPercentDone;
	long awardLevel;
    String userName;
    private String awardTitle;
    private String awardDescription;

	public FPPAward(String userName, long creationTime, long gameMode,
                    long awardId, long awardLevel) {
		super();
		this.userName = userName;
		this.creationTime = creationTime;
		this.gameMode = gameMode;
		this.awardId = awardId;
		this.awardLevel = awardLevel;
	}

	public FPPAward() {

	}

    public FPPAward(long awardId, boolean awardOn, long gameMode, long awardPercentDone, String userName, String awardTitle, String awardDescription) {
        this.awardId = awardId;
        this.awardOn = awardOn;
        this.gameMode = gameMode;
        this.awardPercentDone = awardPercentDone;
        this.userName = userName;
        this.awardTitle = awardTitle;
        this.awardDescription = awardDescription;
    }

    public boolean isAwardOn() {

        return awardOn;
    }

    public void setAwardOn(boolean awardOn) {
        this.awardOn = awardOn;
    }

    public long getAwardPercentDone() {
        return awardPercentDone;
    }

    public void setAwardPercentDone(long awardPercentDone) {
        this.awardPercentDone = awardPercentDone;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getAwardDescription() {
        return awardDescription;
    }

    public void setAwardDescription(String awardDescription) {
        this.awardDescription = awardDescription;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getGameMode() {
		return gameMode;
	}

	public void setGameMode(long gameMode) {
		this.gameMode = gameMode;
	}

	public long getAwardId() {
		return awardId;
	}

	public void setAwardId(long awardId) {
		this.awardId = awardId;
	}

	public long getAwardLevel() {
		return awardLevel;
	}

	public void setAwardLevel(long awardLevel) {
		this.awardLevel = awardLevel;
	}

    @Override
    public String toString() {
        return "FPPAward{" +
                "awardId=" + awardId +
                ", awardOn=" + awardOn +
                ", gameMode=" + gameMode +
                ", creationTime=" + creationTime +
                ", awardPercentDone=" + awardPercentDone +
                ", awardLevel=" + awardLevel +
                ", userName='" + userName + '\'' +
                ", awardTitle='" + awardTitle + '\'' +
                ", awardDescription='" + awardDescription + '\'' +
                '}';
    }

}