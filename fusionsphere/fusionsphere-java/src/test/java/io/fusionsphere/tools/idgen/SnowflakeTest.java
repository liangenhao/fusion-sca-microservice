package io.fusionsphere.tools.idgen;

import io.fusionsphere.idgen.snowflake.Snowflake;
import io.fusionsphere.idgen.snowflake.SnowflakeGenerator;
import org.junit.jupiter.api.Test;

/**
 * @author enhao
 */
public class SnowflakeTest {

    @Test
    public void testSnowflake() {
        Snowflake snowflake0 = new Snowflake(0, 0);
        Snowflake snowflake1 = new Snowflake(1, 1);
        long id0 = snowflake0.nextId();
        System.out.println(id0);
        System.out.println(snowflake0.getGenerateDateTime(id0));
        System.out.println(snowflake0.getWorkerId(id0));
        System.out.println(snowflake0.getDataCenterId(id0));

        System.out.println(snowflake1.nextId());
        System.out.println(snowflake1.nextIdStr());
    }

    @Test
    public void testDefaultSnowflake() {
        Snowflake snowflake = new Snowflake();
        long id = snowflake.nextId();
        System.out.println(id);
        System.out.println(snowflake.getDataCenterId(id));
        System.out.println(snowflake.getWorkerId(id));
    }

    @Test
    public void testSnowflakeGenerator() {
        Snowflake snowflake0 = SnowflakeGenerator.getSnowflake(0);
        Snowflake snowflake1 = SnowflakeGenerator.getSnowflake(1);
        long seq1 = snowflake1.nextId();
        long seq0 = snowflake0.nextId();
        System.out.println(seq0);
        System.out.println(seq1);

        System.out.println(snowflake1.getWorkerId(seq1) + " " + snowflake1.getDataCenterId(seq1) + " " + snowflake1.getGenerateDateTime(seq1));
        System.out.println(snowflake0.getWorkerId(seq0) + " " + snowflake0.getDataCenterId(seq0) + " " + snowflake0.getGenerateDateTime(seq0));
    }
}
