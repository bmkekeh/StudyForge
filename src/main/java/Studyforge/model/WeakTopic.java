package Studyforge.model;

public class WeakTopic {

    private String topic;
    private long mistakeCount;

    public WeakTopic(
            String topic,
            long mistakeCount
    ) {
        this.topic = topic;
        this.mistakeCount = mistakeCount;
    }

    public String getTopic() {
        return topic;
    }

    public long getMistakeCount() {
        return mistakeCount;
    }
}