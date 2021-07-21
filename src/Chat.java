import java.io.Serializable;

public class Chat implements Serializable {
    public Long id;
    public ChatMode mode;
    public Intern intern;

    public Chat(Long id) {
        this.id = id;
        this.mode = ChatMode.Start;
        this.intern = new Intern();
    }
}
