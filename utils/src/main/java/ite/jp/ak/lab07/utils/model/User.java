package ite.jp.ak.lab07.utils.model;

import ite.jp.ak.lab07.utils.enums.UserRole;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private UserRole role;
}
