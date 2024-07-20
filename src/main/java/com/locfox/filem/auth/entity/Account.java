package com.locfox.filem.auth.entity;

import jakarta.persistence.*;

@Entity
@Table
public class Account {
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = false)
    private String passwordHash;

    // ==========================================================================

    public Account(int id, String nickname, String email, String passwordHash) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Account() {}

    public static Builder builder() {
        return new Builder(new Account());
    }

    public static final class Builder {

        private Account instance;

        public Builder(Account instance) {
            this.instance = instance;
        }

        public Builder nickname(String nickname) {
            this.instance.setNickname(nickname);
            return this;
        }

        public Builder password(String passwordHash) {
            this.instance.setPasswordHash(passwordHash);
            return this;
        }

        public Builder email(String email) {
            this.instance.setEmail(email);
            return this;
        }

        public Account build() {
            return this.instance;
        }
    }
}
