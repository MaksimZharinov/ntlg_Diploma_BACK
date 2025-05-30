package ru.netology.constant;

public enum SqlQueries {

    //auth
    CHECK_PASSWORD("SELECT password " +
            "FROM netology.users WHERE login = ?"),
    GET_LOGIN_BY_TOKEN("SELECT user_name " +
            "FROM netology.auth_tokens WHERE token = ?"),

    //token
    CREATE_TOKEN("INSERT INTO netology.auth_tokens " +
            "(user_name, token) VALUES (?, ?)"),
    CHECK_TOKEN("SELECT COUNT(*) " +
            "FROM netology.auth_tokens WHERE token = ?"),
    DROP_TOKEN("DELETE FROM netology.auth_tokens " +
            "WHERE user_name = ?"),

    //file
    GET_FILE("SELECT filename, file_data, hash, size " +
            "FROM netology.files " +
            "WHERE user_name = ? AND filename = ?"),
    SAVE_FILE("INSERT INTO netology.files " +
            "(user_name, filename, file_data, hash, size) " +
            "VALUES (?, ?, ?, ?, ?)"),
    DELETE_FILE("DELETE FROM netology.files " +
            "WHERE user_name = ? AND filename = ?"),
    GET_FILE_LIST("SELECT filename, size " +
            "FROM netology.files " +
            "WHERE user_name = ? ORDER BY filename LIMIT ?"),
    FILE_IS_EXIST("SELECT COUNT(*) " +
            "FROM netology.files WHERE user_name = ? AND filename = ?"),
    RENAME_FILE("UPDATE netology.files " +
            "SET filename = ? WHERE user_name = ? AND filename = ?");

    public final String query;

    SqlQueries(String query) {
        this.query = query;
    }
}