package at.risingr.studygroup;

import java.util.ArrayList;

public class StudyGroup {

    private String groupName;
    private String groupDetails;
    private int participantCount;
    private int participantsMax;
    private String dateFrom;
    private String dateTo;
    private String timeFrom;
    private String timeTo;
    private String location;
    private String locationDetail;
    private ArrayList<Participant> participants;

    public StudyGroup() {
        // needed for firebase
    }

    public StudyGroup(String groupName, String groupDetails, int participantsMax, String dateFrom,
                      String dateTo, String timeFrom, String timeTo, String location,
                      String locationDetail, Participant creator) {

        this.groupName = groupName;
        this.groupDetails = groupDetails;
        this.participantCount = 1;
        this.participantsMax = participantsMax;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.location = location;
        this.locationDetail = locationDetail;
        this.participants = new ArrayList<Participant>();
        participants.add(creator);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participantCount = participants.size();
    }

    ///////////////////////
    // getter and setter //
    ///////////////////////

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDetails() {
        return groupDetails;
    }

    public void setGroupDetails(String groupDetails) {
        this.groupDetails = groupDetails;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public int getParticipantsMax() {
        return participantsMax;
    }

    public void setParticipantsMax(int participantsMax) {
        this.participantsMax = participantsMax;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}
