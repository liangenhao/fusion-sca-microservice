package io.fusion.common.utils;

import io.fusion.api.annotation.FieldCompare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author enhao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @FieldCompare(name = "姓名")
    private String name;

    @FieldCompare(name = "年龄")
    private Integer age;

    @FieldCompare(name = "性别", enumClass = GenderEnum.class, enumCodeField = "code", enumDisplayField = "desc")
    private Integer gender;

    @FieldCompare(name = "性别", enumDisplayField = "desc")
    private GenderEnum genderEnum;

    @FieldCompare(name = "生日")
    private LocalDateTime birthday;

    @FieldCompare(name = "学校", nested = true, maxDepth = 3)
    private School school;


}
