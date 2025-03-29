package io.fusion.common.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author enhao
 */
public class ObjectDiffUtilsTest {

    @Test
    public void testCompare() {
        List<User> oldChildren = new ArrayList<>();
        oldChildren.add(User.builder().name("张三").school(School.builder().name("123").build()).build());
        // oldChildren.add(User.builder().name("张三2").build());
        // oldChildren.add(User.builder().name("张三3").build());
        School oldSchool = School.builder().name("abc")
                .children(oldChildren).build();
        User oldUser = User.builder()
                .name("小明")
                .age(18)
                .gender(1)
                .genderEnum(GenderEnum.MALE)
                .birthday(LocalDateTime.now())
                .school(oldSchool)
                .build();

        List<User> newChildren = new ArrayList<>();
        newChildren.add(User.builder().name("李四").school(School.builder().name("456").build()).build());
        newChildren.add(User.builder().name("王五").build());
        School newSchool = School.builder().name("abcd")
                .children(newChildren).level("5").build();
        User newUser = User.builder()
                .name("小王")
                .age(29)
                .gender(2)
                .genderEnum(GenderEnum.FEMALE)
                .birthday(LocalDateTime.now())
                .school(newSchool)
                .build();

        String s = ObjectDiffUtils.formatChanges(oldUser, newUser);
        System.out.println(s);
    }
}
