package com.locfox.filem.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jwt_black_list")
public class JwtBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String jwtToken;

//    ==============================================================

    public JwtBlackList() { }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public JwtBlackList(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
