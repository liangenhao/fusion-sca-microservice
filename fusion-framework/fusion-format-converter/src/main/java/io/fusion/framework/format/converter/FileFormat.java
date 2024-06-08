package io.fusion.framework.format.converter;

/**
 * @author enhao
 */
public enum FileFormat {
    PDF("pdf", "application/pdf"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    TXT("txt", "text/plain"),
    HTML("html", "text/html"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    ;

    private final String extension;

    private final String mineType;

    FileFormat(String extension, String mineType) {
        this.extension = extension;
        this.mineType = mineType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMineType() {
        return mineType;
    }
}
