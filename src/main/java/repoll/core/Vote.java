package repoll.core;

import java.util.Date;

/*
 * This object should be considered immutable
 */
public class Vote extends DomainObject {
    private final User author;
    private final Answer answer;
    private final Date creationDate;

    public Vote(User author, Answer answer) {
        this(author, answer, new Date(System.currentTimeMillis()));
    }

    public Vote(User author, Answer answer, Date creationDate) {
        this.author = author;
        this.answer = answer;
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return String.format("Vote(id=%d user=%s answer=%s)", getId(),
                author == null ? null : author.getLogin(),
                answer.getDescription()
        );
    }

    public User getAuthor() {
        return author;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Date getCreationDate() {
        return creationDate;
    }

}
