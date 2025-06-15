package io.fusion.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.fusion.api.annotation.CompareField;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @CompareField(name = "姓名")
    private String name;

    @CompareField(name = "年龄")
    private Integer age;

    @CompareField(name = "性别", enumClass = GenderEnum.class, enumCodeField = "code", enumDisplayField = "desc")
    private Integer gender;

    @CompareField(name = "性别", enumDisplayField = "desc")
    private GenderEnum genderEnum;

    @CompareField(name = "生日")
    private LocalDateTime birthday;

    @CompareField(name = "学校", nested = true, maxDepth = 3)
    private School school;


}
