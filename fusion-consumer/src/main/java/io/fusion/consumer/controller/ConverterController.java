package io.fusion.consumer.controller;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author enhao
 */
@RestController
public class ConverterController {

    @Autowired
    @Qualifier("localOfficeManager")
    private OfficeManager localOfficeManager;

    @Autowired
    @Qualifier("localDocumentConverter")
    private DocumentConverter localConverter;

    @Autowired
    @Qualifier("remoteOfficeManager")
    private OfficeManager remoteOfficeManager;

    @Autowired
    @Qualifier("remoteDocumentConverter")
    private DocumentConverter remoteConverter;

    @PostMapping("localConvert")
    public void localConvert(@RequestParam String inputFilePath, @RequestParam String outputFileName) throws OfficeException {
        File inputFile = new File(inputFilePath);
        localConverter.convert(inputFile)
                .to(new File("/Users/enhao/Downloads/" + outputFileName))
                .execute();
    }

    @PostMapping("remoteConvert")
    public void remoteConvert(@RequestParam String inputFilePath, @RequestParam String outputFileName) throws OfficeException {
        File inputFile = new File(inputFilePath);
        remoteConverter.convert(inputFile)
                .to(new File("/Users/enhao/Downloads/" + outputFileName))
                .execute();
    }
}
