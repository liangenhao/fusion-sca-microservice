package io.fusion.framework.format.converter;

import lombok.Data;

import java.io.File;
import java.io.InputStream;

/**
 * @author enhao
 */
@Data
public class FormatConversionWrapper {

    private FormatConversionMetadata metadata;

    private InputStream inputStream;

    private byte[] input;

    private File inputFile;
}
