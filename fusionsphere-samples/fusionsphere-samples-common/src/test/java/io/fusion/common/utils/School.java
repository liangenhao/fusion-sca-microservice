package io.fusion.common.utils;

import io.fusion.api.annotation.CompareField;
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

    @CompareField(name = "学校名称")
    private String name;

    @CompareField(name = "年级")
    private String level;

    @CompareField(name = "孩子", nested = true)
    private List<User> children;
}
