/**
 *
 */
package test.excel;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common2.excel.ExcelPerfModeReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年1月3日
 */
public class ExcelTest {

    /**
     * @param args
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void main(String[] args) throws InvalidFormatException, IOException {
        //普通方式读取
        String excelFilePath = "/Users/jiangwei/Desktop/invorderdet_template.xlsx";

        //大文件读取防止内存溢出
        List<SimpleSalaryInfo> list = new ExcelPerfModeReader(excelFilePath).read(SimpleSalaryInfo.class);

        System.out.println(JsonUtils.toPrettyJson(list));

    }

}
