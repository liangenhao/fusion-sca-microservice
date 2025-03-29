package io.fusion.common.utils;

import io.fusion.api.annotation.FieldCompare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author enhao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {

    @FieldCompare(name = "学校名称")
    private String name;

    @FieldCompare(name = "年级")
    private String level;

    @FieldCompare(name = "孩子", nested = true)
    private List<User> children;
}
