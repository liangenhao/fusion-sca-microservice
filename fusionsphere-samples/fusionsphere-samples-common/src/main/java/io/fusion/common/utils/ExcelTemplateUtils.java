package io.fusion.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel模板生成工具类
 *
 * @author enhao
 */
public class ExcelTemplateUtils {

    /**
     * 下拉框最大字符长度限制
     * Excel下拉框的字符串总长度限制为255个字符
     */
    private static final int MAX_DROP_DOWN_LENGTH = 255;

    /**
     * 生成Excel模板
     *
     * @param outputStream 输出流
     * @param sheetName    工作表名称
     * @param cls          实体类
     * @param dropDownMap  下拉框配置，key：字段名：value：下拉框选项列表
     * @param <T>          实体类类型
     */
    public static <T> void generateTemplate(OutputStream outputStream, String sheetName, Class<T> cls,
                                            Map<String, List<String>> dropDownMap) {
        Map<Integer, List<String>> indexOptionsMap = resolveIndexDropDowmMap(cls, dropDownMap);

        // 分离长枚举和普通枚举
        Map<Integer, List<String>> longEnumMap = new HashMap<>();
        Map<Integer, List<String>> normalEnumMap = new HashMap<>();
        indexOptionsMap.forEach((index, options) -> {
            int totalLength = options.stream()
                    .mapToInt(String::length)
                    .sum();
            if (totalLength > MAX_DROP_DOWN_LENGTH) {
                longEnumMap.put(index, options);
            } else {
                normalEnumMap.put(index, options);
            }
        });

        ExcelWriterBuilder builder = EasyExcel.write(outputStream, cls);
        if (MapUtils.isNotEmpty(longEnumMap)) {
            builder.registerWriteHandler(new LongEnumDropDownHandler(longEnumMap));
        }

        if (MapUtils.isNotEmpty(normalEnumMap)) {
            builder.registerWriteHandler(new DropDownSheetWriteHandler(normalEnumMap));
        }

        builder.sheet(sheetName)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(Collections.emptyList());
    }

    /**
     * 生成Excel模板
     *
     * @param outputStream   输出流
     * @param sheetName      工作表名称
     * @param cls            实体类
     * @param dropDownConfig 下拉框配置
     * @param <T>            实体类类型
     */
    public static <T> void generateTemplateLambda(OutputStream outputStream, String sheetName, Class<T> cls,
                                                  Map<FieldFunction<T, ?>, List<String>> dropDownConfig) {
        // 将Lambda表达式转换为字段名
        Map<String, List<String>> fieldNameDropDownMap = new HashMap<>();
        if (dropDownConfig != null) {
            for (Map.Entry<FieldFunction<T, ?>, List<String>> entry : dropDownConfig.entrySet()) {
                String fieldName = LambdaUtils.getFieldName(entry.getKey());
                fieldNameDropDownMap.put(fieldName, entry.getValue());
            }
        }

        generateTemplate(outputStream, sheetName, cls, fieldNameDropDownMap);
    }


    /**
     * 生成模板
     *
     * @param outputStream 输出流
     * @param sheetName    工作表名称
     * @param headers      表头列表
     * @param dropDownMap  下拉框配置，key为表头名称，value为下拉选项列表
     */
    public static void generateTemplate(OutputStream outputStream, String sheetName, List<String> headers,
                                        Map<String, List<String>> dropDownMap) {
        // 将基于表头名称的下拉框配置转换为基于列索引的配置
        Map<Integer, List<String>> indexOptionsMap = new HashMap<>();
        if (dropDownMap != null && !dropDownMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : dropDownMap.entrySet()) {
                String headerName = entry.getKey();
                List<String> options = entry.getValue();

                // 查找表头名称对应的列索引
                int columnIndex = headers.indexOf(headerName);
                if (columnIndex < 0) {
                    continue;
                }
                indexOptionsMap.put(columnIndex, options);
            }
        }

        // 分离长枚举和普通枚举
        Map<Integer, List<String>> longEnumMap = new HashMap<>();
        Map<Integer, List<String>> normalEnumMap = new HashMap<>();
        indexOptionsMap.forEach((index, options) -> {
            int totalLength = options.stream()
                    .mapToInt(String::length)
                    .sum();
            if (totalLength > MAX_DROP_DOWN_LENGTH) {
                longEnumMap.put(index, options);
            } else {
                normalEnumMap.put(index, options);
            }
        });

        ExcelWriterBuilder builder = EasyExcel.write(outputStream);
        if (MapUtils.isNotEmpty(longEnumMap)) {
            builder.registerWriteHandler(new LongEnumDropDownHandler(longEnumMap));
        }

        if (MapUtils.isNotEmpty(normalEnumMap)) {
            builder.registerWriteHandler(new DropDownSheetWriteHandler(normalEnumMap));
        }

        builder.head(convertToHead(headers))
                .sheet(sheetName)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(Collections.emptyList());
    }

    private static List<List<String>> convertToHead(List<String> headers) {
        List<List<String>> result = new ArrayList<>();
        for (String header : headers) {
            result.add(Collections.singletonList(header));
        }
        return result;
    }


    private static <T> Map<Integer, List<String>> resolveIndexDropDowmMap(Class<T> cls, Map<String, List<String>> dropDownMap) {
        Map<Integer, List<String>> indexOptionsMap = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty == null) {
                continue;
            }

            String fieldName = field.getName();
            int index = excelProperty.index();
            int columnIndex = index > 0 ? index : i - 1;

            // 自动添加 @ExcelEnum 中的枚举值
            // ExcelEnum excelEnum = field.getAnnotation(ExcelEnum.class);
            // if (excelEnum != null) {
            //     Class<? extends BaseEnum<?>> enumClass = excelEnum.value();
            //     BaseEnum<?>[] enumConstants = enumClass.getEnumConstants();
            //     List<String> enumDescList = Arrays.stream(enumConstants)
            //             .map(BaseEnum::getDesc)
            //             .collect(Collectors.toList());
            //
            //     indexOptionsMap.put(columnIndex, enumDescList);
            // }

            // 如果 dropDownMap 中有配置，优先使用 dropDownMap 中的配置
            if (dropDownMap != null && dropDownMap.containsKey(fieldName)) {
                indexOptionsMap.put(columnIndex, dropDownMap.get(fieldName));
            }
        }
        return indexOptionsMap;
    }


    /**
     * 下拉框处理器
     */
    private static class DropDownSheetWriteHandler implements SheetWriteHandler {
        private final Map<Integer, List<String>> dropDownMap;

        public DropDownSheetWriteHandler(Map<Integer, List<String>> dropDownMap) {
            this.dropDownMap = dropDownMap;
        }

        @Override
        public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            // 在创建Sheet前不需要操作
        }

        @Override
        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            Sheet sheet = writeSheetHolder.getSheet();

            // 为指定列添加下拉框
            for (Map.Entry<Integer, List<String>> entry : dropDownMap.entrySet()) {
                Integer columnIndex = entry.getKey();
                List<String> options = entry.getValue();

                // 设置下拉框的范围，从第二行开始（索引为1，第一行是表头）
                // 这里设置了1000行的范围，可以根据需要调整
                CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);

                // 创建下拉框数据验证
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createExplicitListConstraint(
                        options.toArray(new String[0]));
                DataValidation dataValidation = helper.createValidation(constraint, addressList);

                // 设置错误提示
                dataValidation.setShowErrorBox(true);
                dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidation.createErrorBox("错误", "请从下拉列表中选择有效的值");

                // 添加验证到工作表
                sheet.addValidationData(dataValidation);
            }
        }
    }


    /**
     * 长枚举下拉框处理器
     * 通过在隐藏的Sheet中创建枚举值列表，然后通过引用的方式创建下拉框
     */
    private static class LongEnumDropDownHandler implements SheetWriteHandler {
        private final Map<Integer, List<String>> longEnumMap;

        public LongEnumDropDownHandler(Map<Integer, List<String>> longEnumMap) {
            this.longEnumMap = longEnumMap;
        }

        @Override
        public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            // 在创建Sheet前不需要操作
        }

        @Override
        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            // 获取工作簿和工作表
            Workbook workbook = writeWorkbookHolder.getWorkbook();
            Sheet mainSheet = writeSheetHolder.getSheet();

            // 为每个长枚举列创建一个隐藏的Sheet
            for (Map.Entry<Integer, List<String>> entry : longEnumMap.entrySet()) {
                Integer columnIndex = entry.getKey();
                List<String> options = entry.getValue();

                // 创建隐藏的Sheet，用于存储枚举值
                String hiddenSheetName = "EnumValues_" + columnIndex;
                Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);

                // 将枚举值写入隐藏Sheet
                for (int i = 0; i < options.size(); i++) {
                    Row row = hiddenSheet.createRow(i);
                    Cell cell = row.createCell(0);
                    cell.setCellValue(options.get(i));
                }

                // 设置隐藏Sheet的列宽
                hiddenSheet.setColumnWidth(0, 256 * 60); // 设置足够宽以容纳长文本

                // 隐藏Sheet
                workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

                // 创建名称管理器
                String namedRangeName = "Range_" + columnIndex;
                Name name = workbook.createName();
                name.setNameName(namedRangeName);
                name.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + options.size());

                // 设置下拉框的范围，从第二行开始（索引为1，第一行是表头）
                CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);

                // 创建下拉框数据验证
                DataValidationHelper helper = mainSheet.getDataValidationHelper();
                // 使用正确的引用格式
                DataValidationConstraint constraint = helper.createFormulaListConstraint(namedRangeName);
                DataValidation dataValidation = helper.createValidation(constraint, addressList);

                // 设置错误提示
                dataValidation.setShowErrorBox(true);
                dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidation.createErrorBox("错误", "请从下拉列表中选择有效的值");

                // 添加验证到工作表
                mainSheet.addValidationData(dataValidation);
            }
        }
    }

}