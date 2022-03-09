package com.nway.spring.jdbc.performance.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_computer_user")
public class ComputerUser {
    @TableId
    private int pkId;
    private int foreignKey;
    private String user;
    private int idx;
}
