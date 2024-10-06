package Server.model;

import java.time.LocalDateTime;

public class Account {
    private String accountID;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account() {}

    public Account(String accountID, String username, String password, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
