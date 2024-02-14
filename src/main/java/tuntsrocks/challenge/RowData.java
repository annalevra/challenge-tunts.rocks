package tuntsrocks.challenge;

public class RowData {
    private int registration;
    private String student;
    private int absence;
    private int testScore1;
    private int testScore2;
    private int testScore3;

    public int getRegistration() {
        return registration;
    }

    public void setRegistration(int registration) {
        this.registration = registration;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getAbsence() {
        return absence;
    }

    public void setAbsence(int absence) {
        this.absence = absence;
    }

    public int getTestScore1() {
        return testScore1;
    }

    public void setTestScore1(int testScore1) {
        this.testScore1 = testScore1;
    }

    public int getTestScore2() {
        return testScore2;
    }

    public void setTestScore2(int testScore2) {
        this.testScore2 = testScore2;
    }

    public int getTestScore3() {
        return testScore3;
    }

    public void setTestScore3(int testScore3) {
        this.testScore3 = testScore3;
    }

    @Override
    public String toString() {
        return "RowData{" +
                "registration=" + registration +
                ", student='" + student + '\'' +
                ", absence=" + absence +
                ", testScore1=" + testScore1 +
                ", testScore2=" + testScore2 +
                ", testScore3=" + testScore3 +
                '}';
    }
}
