
package com.fun_picks.fpphoto;

public class FPPItem {

	String userName;
	long creationTime;
	long gameMode;
	long gameScore;
	long scoreTime;

	public FPPItem(String userName, long creationTime, long gameMode,
				   long gameScore, long scoreTime) {
		super();
		this.userName = userName;
		this.creationTime = creationTime;
		this.gameMode = gameMode;
		this.gameScore = gameScore;
		this.scoreTime = scoreTime;
	}

	public FPPItem() {

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

	public long getGameScore() {
		return gameScore;
	}

	public void setGameScore(long gameScore) {
		this.gameScore = gameScore;
	}

	public long getScoreTime() {
		return scoreTime;
	}

	public void setScoreTime(long scoreTime) {
		this.scoreTime = scoreTime;
	}

	@Override
	public String toString() {
		return "FPPItem [userName=" + userName + ", creationTime="
				+ creationTime + ", gameMode=" + gameMode + ", gameScore="
				+ gameScore + ", scoreTime=" + scoreTime + "]";
	}
}