package at.risingr.studygroup;

public class Participant {

    private String uid;
    private String name;
    private String comment;
    private int knowledge;
    private boolean isCreator;

    public Participant() {
        // needed for firebase
    }

    public Participant(String uid, String name, String comment, int knowledge, boolean isCreator) {
        this.uid = uid;
        this.name = name;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
