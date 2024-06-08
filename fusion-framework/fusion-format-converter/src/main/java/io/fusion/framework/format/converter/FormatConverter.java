package io.fusion.framework.format.converter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件格式转换器接口
 *
 * @author enhao
 */
public interface FormatConverter {

    OutputStream convert(InputStream source, FormatConversionMetadata metadata);

    byte[] convert(byte[] source, FormatConversionMetadata metadata);

    File convert(File source, FormatConversionMetadata metadata);

    List<FileFormat> getSourceFormat();

    FileFormat getTargetFormat();
}
