package com.mipt.semengolodniuk.hw6;

import com.mipt.semengolodniuk.hw6.annotations.*;

public class User {
    @NotNull
    private String name;

    @Min(0) @Max(150)
    private Integer age;

    @NotNull
    private String email;

    public User(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getEmail() { return email; }
}
