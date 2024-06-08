package io.fusion.framework.format.converter;

import lombok.Data;

import java.util.Map;

/**
 * @author enhao
 */
@Data
public class FormatConversionMetadata {

    private String fileName;

    private String fileFormat;

    private Long fileSize;

    private Long password;

    private FileFormat sourceFormat;

    private FileFormat targetFormat;

    private Map<String, Object> params;
}
