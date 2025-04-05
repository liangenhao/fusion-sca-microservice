package io.fusion.common.generic;

import io.fusion.common.generic.converter.AttributeConverterRegistry;
import io.fusion.common.generic.converter.AttributeTypeConverter;
import io.fusion.common.utils.User;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author enhao
 */
public class AttributeTypeTest {

    @Test
    public void test() {
        AttributeConverterRegistry registry = new AttributeConverterRegistry();
        registry.init();

        AttributeValueManager attributeValueManager = new AttributeValueManager(registry);

        AttributeTypeConverter<User> converter = registry.getConverter(User.class.getName()); // 1
        User user = converter.convertToJavaType("{\"name\":\"enhao\",\"school\":{\"name\":\"小学\"}}");

        String value = converter.convertToDatabaseValue(user);
        System.out.println(value);

        AttributeTypeConverter<List<User>> listConverter = registry.getConverter(attributeValueManager.getListTypeName(User.class));
        List<User> users = listConverter.convertToJavaType("[{\"name\":\"enhao\",\"school\":{\"name\":\"小学\"}}]");

        String value1 = listConverter.convertToDatabaseValue(users);
        System.out.println(value1);


    }
}
