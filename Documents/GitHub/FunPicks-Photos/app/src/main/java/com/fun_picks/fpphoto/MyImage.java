/***********************************************************************
 *        FILE:
 * DESCRIPTION:
 *        DATE:
 *          BY: Darren Ting
 **********************************************************************/
package com.fun_picks.fpphoto;

public class MyImage extends Object {
	private long sequence;
	private long mode;
	private long group;
	private long groupSeq;
	private long rating;
	private String imageFile;
	private String commentFile;
	private String comments;
	private boolean ready;
	private boolean winner;
	private boolean selected;
    private int width;
    private int height;
    private float latitude;
    private float longitude;
	private String location;

	public void MyImage(){
		
	}
	
	public MyImage(MyImage source) {
		super();
		this.sequence = source.sequence;
		this.mode = source.mode;
		this.group = source.group;
		this.groupSeq = source.groupSeq;
		this.rating = source.rating;
		this.imageFile = source.imageFile;
		this.commentFile = source.commentFile;
		this.comments = source.comments;
		this.ready = source.ready;
		this.winner = source.winner;
        this.selected = source.selected;
        this.width = source.width;
        this.height = source.height;
        this.latitude = source.latitude;
        this.longitude = source.longitude;
		this.location = source.location;
	}

    public MyImage(long sequence, long group, long groupSeq, long rating,
                   String imageFile, String commentFile, String comments) {
        super();
        this.sequence = sequence;
        this.group = group;
        this.groupSeq = groupSeq;
        this.rating = rating;
        this.imageFile = imageFile;
        this.commentFile = commentFile;
        this.comments = comments;
        this.ready = false;
        this.winner = false;
        this.selected = false;
        this.latitude = 0;
        this.longitude=0;
		this.location="";
    }

    public MyImage(long sequence, long mode, long group, long groupSeq, long rating,
                   String imageFile, String commentFile, String comments) {
        super();
        this.sequence = sequence;
        this.mode = mode;
        this.group = group;
        this.groupSeq = groupSeq;
        this.rating = rating;
        this.imageFile = imageFile;
        this.commentFile = commentFile;
        this.comments = comments;
        this.ready = false;
        this.winner = false;
        this.selected = false;
        this.latitude = 0;
        this.longitude=0;
		this.location="";

	}
	
	public MyImage(long sequence, long group, long groupSeq, long rating,
			String imageFile, String commentFile, String comments, Float latitude, Float longitude, String location) {
		super();
		this.sequence = sequence;
		this.group = group;
		this.groupSeq = groupSeq;
		this.rating = rating;
		this.imageFile = imageFile;
		this.commentFile = commentFile;
		this.comments = comments;
		this.ready = false;
		this.winner = false;
		this.selected = false;
        this.latitude = latitude;
        this.longitude = longitude;
		this.location = location;

	}
	
	public MyImage(long sequence, long mode, long group, long groupSeq, long rating,
			String imageFile, String commentFile, String comments, Float latitude, Float longitude) {
		super();
		this.sequence = sequence;
		this.mode = mode;
		this.group = group;
		this.groupSeq = groupSeq;
		this.rating = rating;
		this.imageFile = imageFile;
		this.commentFile = commentFile;
		this.comments = comments;
		this.ready = false;
		this.winner = false;
		this.selected = false;
        this.latitude = latitude;
        this.longitude = longitude;
		this.location="";

	}


	public void setMyImage(long sequence, long group, long groupSeq, long rating,
                                String imageFile, String commentFile, String comments) {
        this.sequence = sequence;
        this.group = group;
        this.groupSeq = groupSeq;
        this.rating = rating;
        this.imageFile = imageFile;
        this.commentFile = commentFile;
        this.comments = comments;
        this.ready = false;
        this.winner = false;
        this.selected = false;
        this.latitude = 0;
        this.longitude=0;
		this.location="";

	}

    public void setMyImage(long sequence, long group, long groupSeq, long rating,
                           String imageFile, String commentFile, String comments, Float latitude, Float longitude, String location) {
        this.sequence = sequence;
        this.group = group;
        this.groupSeq = groupSeq;
        this.rating = rating;
        this.imageFile = imageFile;
        this.commentFile = commentFile;
        this.comments = comments;
        this.ready = false;
        this.winner = false;
        this.selected = false;
        this.latitude = latitude;
        this.longitude = longitude;
		this.location = location;

	}
	
	public void setMyImage(long sequence, long mode, long group, long groupSeq, long rating,
			String imageFile, String commentFile, String comments) {
		this.sequence = sequence;
		this.mode = mode;
		this.group = group;
		this.groupSeq = groupSeq;
		this.rating = rating;
		this.imageFile = imageFile;
		this.commentFile = commentFile;
		this.comments = comments;
		this.ready = false;
		this.winner = false;
		this.selected = false;
        this.latitude = 0;
        this.longitude=0;
		this.location="";

	}

	public long getSequence() {
		return sequence;
	}
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	public long getMode() {
		return mode;
	}
	public void setMode(long mode) {
		this.mode = mode;
	}
	public long getGroup() {
		return group;
	}
	public void setGroup(long group) {
		this.group = group;
	}
	public long getGroupSeq() {
		return groupSeq;
	}
	public void setGroupSeq(long groupSeq) {
		this.groupSeq = groupSeq;
	}
	public long getRating() {
		return rating;
	}
	public void setRating(long rating) {
		this.rating = rating;
	}
	public String getImageFile() {
		return imageFile;
	}
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}
	public String getCommentFile() {
		return commentFile;
	}
	public void setCommentFile(String commentFile) {
		this.commentFile = commentFile;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public boolean getReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public boolean getWinner() {
		return winner;
	}
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void reset() {
		this.sequence = 0;
		this.mode = 0;
		this.group = 0;
		this.groupSeq = 0;
		this.rating = 0;
		this.imageFile = "";
		this.commentFile = "";
		this.comments = "";
		this.ready = false;
		this.winner = false;
		this.selected = false;
        this.latitude = 0;
        this.longitude=0;
		this.location = "";

	}
	
	@Override
	public String toString() {
		return "MyImage [sequence=" + sequence + ", group=" + group
				+ ", groupSeq=" + groupSeq + ", rating=" + rating
				+ ", imageFile=" + imageFile + ", commentFile=" + commentFile
				+ ", comments=" + comments + "]";
	}


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
