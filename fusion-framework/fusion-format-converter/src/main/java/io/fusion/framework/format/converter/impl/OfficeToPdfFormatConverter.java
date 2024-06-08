package io.fusion.framework.format.converter.impl;

import io.fusion.framework.format.converter.FileFormat;
import io.fusion.framework.format.converter.FormatConversionMetadata;
import io.fusion.framework.format.converter.FormatConvertException;
import io.fusion.framework.format.converter.FormatConverter;
import lombok.SneakyThrows;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.local.LocalConverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author enhao
 */
public class OfficeToPdfFormatConverter implements FormatConverter {


    @Override
    @SneakyThrows
    public OutputStream convert(InputStream source, FormatConversionMetadata metadata) {
        DocumentFormat sourceDocumentFormat = DefaultDocumentFormatRegistry.getFormatByExtension(metadata
                .getSourceFormat().getExtension());
        DocumentFormat targetDocumentFormat = DefaultDocumentFormatRegistry.getFormatByExtension(metadata
                .getTargetFormat().getExtension());
        if (null == sourceDocumentFormat || null == targetDocumentFormat) {
            throw new FormatConvertException("格式不支持转换");
        }
        OutputStream target = new ByteArrayOutputStream();
        LocalConverter localConverter = resolveLocalConverter();
        localConverter.convert(source)
                .as(sourceDocumentFormat)
                .to(target)
                .as(targetDocumentFormat)
                .execute(); // TODO 异常包装

        return target;
    }

    @Override
    public byte[] convert(byte[] source, FormatConversionMetadata metadata) {
        return new byte[0];
    }

    @Override
    public File convert(File source, FormatConversionMetadata metadata) {
        return null;
    }

    private LocalConverter resolveLocalConverter() {
        return LocalConverter.builder()
                .build();
    }

    @Override
    public List<FileFormat> getSourceFormat() {
        return Arrays.asList(FileFormat.DOC, FileFormat.DOCX, FileFormat.PPT, FileFormat.PPTX, FileFormat.XLS, FileFormat.XLSX);
    }

    @Override
    public FileFormat getTargetFormat() {
        return FileFormat.PDF;
    }
}
