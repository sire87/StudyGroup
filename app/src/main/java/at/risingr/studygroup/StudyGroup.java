package at.risingr.studygroup;

public class StudyGroup {

    public String groupName;
    public String groupDetails;
    public int participantCount;
    public int participantsMax;
    public String dateFrom;
    public String dateTo;
    public String timeFrom;
    public String timeTo;
    public String location;
    public String locationDetail;

    public StudyGroup(String groupName, String groupDetails, int participantsMax, String dateFrom,
                      String dateTo, String timeFrom, String timeTo, String location,
                      String locationDetail) {

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
    }
}
