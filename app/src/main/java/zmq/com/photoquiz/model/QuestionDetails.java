package zmq.com.photoquiz.model;

/**
 * Created by zmq181 on 11/4/19.
 */

public class QuestionDetails {
    private String image;
    private String[] question;
    private String answer;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getQuestion() {
        return question;
    }

    public void setQuestion(String[] question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
