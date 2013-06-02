package repoll.mappers;

import repoll.core.Answer;
import repoll.core.Poll;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AnswerMapper extends AbstractMapper<Answer> {
    private static final AnswerMapper INSTANCE = new AnswerMapper();

    public static AnswerMapper getInstance() {
        return INSTANCE;
    }

    public static final String SEARCH_QUERY = "select * from \"Answer\" where id = ?";
    public static final String UPDATE_QUERY = "update \"Answer\" set poll_id = ?, description = ? where id = ?";
    public static final String INSERT_QUERY = "insert into \"Answer\" (poll_id, description) values (?, ?)";
    public static final String DELETE_QUERY = "delete from \"Answer\" where id = ?";

    private AnswerMapper() {
    }

    @Override
    protected PreparedStatement getLoadByIdStatement(long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SEARCH_QUERY);
        try {
            statement.setLong(1, id);
            return statement;
        } catch (SQLException e) {
            statement.close();
            throw e;
        }
    }

    @Override
    protected PreparedStatement getUpdateStatement(Answer answer) throws SQLException {
        validate(answer);
        PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY);
        try {
            statement.setLong(1, answer.getPoll().getId());
            statement.setString(2, answer.getDescription());
            statement.setLong(3, answer.getId());
            return statement;
        } catch (SQLException e) {
            statement.close();
            throw e;
        }
    }

    @Override
    protected PreparedStatement getInsertStatement(Answer answer) throws SQLException {
        validate(answer);
        PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setLong(1, answer.getPoll().getId());
            statement.setString(2, answer.getDescription());
            statement.setLong(3, answer.getId());
            return statement;
        } catch (SQLException e) {
            statement.close();
            throw e;
        }
    }

    @Override
    protected PreparedStatement getDeleteStatement(Answer answer) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_QUERY);
        try {
            statement.setLong(1, answer.getId());
        } catch (SQLException e) {
            statement.close();
            throw e;
        }
        return null;
    }

    @Override
    protected Answer loadObject(ResultSet resultSet) throws SQLException, MapperException {
        return new Answer(
                Mappers.getForClass(Poll.class).loadById(resultSet.getLong("poll_id")),
                resultSet.getString("description")
        );
    }

    private void validate(Answer answer) {
        if (answer.getPoll() == null) {
            throw new NullPointerException("Poll of " + answer + " is undefined");
        } else if (!answer.getPoll().isSaved()) {
            throw new IllegalStateException("Poll of " + answer + " should be saved first");
        }
    }
}