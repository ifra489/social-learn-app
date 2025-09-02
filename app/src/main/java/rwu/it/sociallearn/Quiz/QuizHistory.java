package rwu.it.sociallearn.Quiz;

public class QuizHistory {

    private int score;
    private int total;
    private long timestamp;

    // Default constructor required for Firebase
    public QuizHistory() { }

    public QuizHistory(int score, int total, long timestamp) {
        this.score = score;
        this.total = total;
        this.timestamp = timestamp;
    }

    // ✅ Getter methods
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public long getTimestamp() { return timestamp; }

    // Optional: Setter methods (Firebase may need them)
    public void setScore(int score) { this.score = score; }
    public void setTotal(int total) { this.total = total; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
