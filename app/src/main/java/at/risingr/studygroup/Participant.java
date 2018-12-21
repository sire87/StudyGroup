package at.risingr.studygroup;

public class Participant {

    private String uid;
    private String comment;
    private int knowledge;
    private boolean isCreator;

    public Participant(String uid, String comment, int knowledge, boolean isCreator) {
        this.uid = uid;
        this.comment = comment;
        this.knowledge = knowledge;
        this.isCreator = isCreator;
    }

    ///////////////////////
    // getter and setter //
    ///////////////////////

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }
}
