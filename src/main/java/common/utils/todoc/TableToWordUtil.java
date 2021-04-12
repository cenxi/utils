package common.utils.todoc;

import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import com.megvii.galaxy.analysis.offline.response.OfflineTaskResponse;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> 创建word文档 </p>
 *
 * @author : zhengqing
 * @description : 步骤:
 *                  1、建立文档
 *                  2、创建一个书写器
 *                  3、打开文档
 *                  4、向文档中写入数据
 *                  5、关闭文档
 * @date : 2019/11/8 15:45
 */
@Service
public class TableToWordUtil {

    private static String path = "/Users/fengxi/Desktop/demo";

    public static void main(String[] args) {
        Class[] classes={OfflineTaskResponse.class};

        List<List<com.megvii.sng.insight.openapi.todoc.TableFiled>> tableFiledList = new ArrayList<>();
        for (Class clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            List<com.megvii.sng.insight.openapi.todoc.TableFiled> tableFileds = new ArrayList<>();
            for (Field field : fields) {
                com.megvii.sng.insight.openapi.todoc.TableFiled tableFiled = new com.megvii.sng.insight.openapi.todoc.TableFiled();
                tableFiled.setCatagory(clazz.getName());
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null) {
                    tableFiled.setField(field.getName());
                    tableFiled.setType(field.getType().getSimpleName());
                    tableFiled.setComment(apiModelProperty.value());
                    tableFileds.add(tableFiled);
                }
            }
            tableFiledList.add(tableFileds);
        }
        TableToWordUtil util=new TableToWordUtil();
        String fileName = path + File.separator + "aaa.doc";
        for (int i = 0; i < tableFiledList.size(); i++) {
            util.toWord(tableFiledList.get(i),fileName,"bbb",i);
        }


    }
    /**
     * 生成word文档
     *
     * @param fileName：生成文件地址
     * @param title:文件内容标题
     * @return: void
     */
    public void toWord(List<com.megvii.sng.insight.openapi.todoc.TableFiled> fileds, String fileName, String title, int no) {
        Document document = new Document(PageSize.A4);
        try {
            // 创建文件夹
            File dir = new File(path);
            dir.mkdirs();

            // 创建文件
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            file.createNewFile();

            // 写入文件信息
            RtfWriter2.getInstance(document, new FileOutputStream(fileName));
            document.open();
            Paragraph ph = new Paragraph();
            Font f = new Font();
            Paragraph p = new Paragraph(title, new Font(Font.NORMAL, 24, Font.BOLDITALIC, new Color(0, 0, 0)));
            p.setAlignment(1);
            document.add(p);
            ph.setFont(f);
            String all = "" + (no + 1) + " 表名称:" + fileds.get(0).getCatagory();
            Table table = new Table(3);
            for (int i = 0; i < 1; i++) {
                com.megvii.sng.insight.openapi.todoc.TableFiled filed = fileds.get(i);


//                document.add(new Paragraph(""));

                table.setBorderWidth(1);
                table.setPadding(0);
                table.setSpacing(0);

                //添加表头的元素，并设置表头背景的颜色
                Color chade = new Color(176, 196, 222);
                Color chad1 = new Color(241, 241, 241);
                Color chad2 = new Color(255, 255, 255);
                Color[] colors = new Color[]{chad1,chad2};

                Cell cell = null;
//                Cell cell = new Cell("编号");
//                addCell(table, cell, chade);
                cell = new Cell("字段");
                addCell(table, cell, chad2);
                cell = new Cell("类型");
                addCell(table, cell, chad2);
//                cell = new Cell("是否非空");
//                addCell(table, cell, chade);
//                cell = new Cell("是否主键");
//                addCell(table, cell, chade);
                cell = new Cell("说明");
                addCell(table, cell, chad2);

                table.endHeaders();

                // 表格的主体
                for (int k = 0; k < fileds.size(); k++) {
//                    addContent(table, fileds.get(k).getField());
//                    addContent(table,  fileds.get(k).getType());
//                    addContent(table, fileds.get(k).getComment());
                    addContent(table, fileds.get(k).getField(),colors[k%2]);
                    addContent(table, fileds.get(k).getType(),colors[k%2]);
                    addContent(table, fileds.get(k).getComment(),colors[k%2]);
                }
            }
            Paragraph pheae = new Paragraph(all);
            //写入表说明
            document.add(pheae);
            //生成表格
            document.add(table);
            document.close();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加表头到表格
     *
     * @param table
     * @param cell
     * @param chade
     */
    private void addCell(Table table, Cell cell, Color chade) {
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(chade);
        cell.setWidth(146.3f);
        table.addCell(cell);
    }

    /**
     * 添加内容到表格
     *
     * @param table
     * @param content
     */
    private void addContent(Table table,  String content,Color chade) {
        Cell cell = new Cell(content);
        cell.setWidth(146.3f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(chade);
        table.addCell(cell);
    }

}
