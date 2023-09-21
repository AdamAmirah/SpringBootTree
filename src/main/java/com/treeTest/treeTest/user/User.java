package com.treeTest.treeTest.user;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private Date lastLoginTime;
}
