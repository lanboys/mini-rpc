package com.mini.rpc.example.api.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 主键ID
   */
  private Long id;

  /**
   * 姓名
   */
  private String name;

  /**
   * 年龄
   */
  private Integer age;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 生日
   */
  private LocalDate birthday;

  /**
   * 注册时间
   */
  private LocalDateTime registerTime;
}
