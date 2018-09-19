package webcrab.taobao.dao;

import webcrab.taobao.model.TaobaoItem;

import java.io.*;
import java.util.Collection;

public class TaobaoItemFileDao {
    private static final String TEMPLATE_FILENAME = "/template/taobao-item.html";
    private FileWriter writer;

    //输出html模板的内容
    private String htmlTemplate;

    public TaobaoItemFileDao() {
        readTemplate();
    }

    public void open(String filename) throws Exception {
        writer = new FileWriter(filename);
    }
    public void close(){
        try {
            if (writer!=null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void write(TaobaoItem item){
        try {
            writer.write("taobaoUrl: "+item.getTaobaoUrl()+"\n");
            writer.write("id: "+item.getId()+"\n");
            writer.write("标题: "+item.getTitle()+"\n");
            writer.write("价格: "+item.getPrice()+"\n");
            writer.write("优惠价: "+item.getPricePromote()+"\n");
            writer.write("库存: "+item.getStock()+"\n");
            writer.write("基本信息: "+item.getBasicInfo()+"\n");
            writer.write("详情: "+item.getDetail() +"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将淘宝商品输出为网页文件
     * @param item 商品
     * @param filename 文件名
     */
    public void writeHtmlPage(TaobaoItem item, String filename) {
        FileWriter fw = null;
        try {
            // 拼接详情图片字符串
            StringBuffer sb = new StringBuffer();
            for (String img: item.getDetailImgs()) {
                sb.append(img+",");
            }
            String detailImgs = sb.substring(0, sb.length()-1);

            String temp = this.htmlTemplate;
            String html = temp.replace("###taobaoUrl###", item.getTaobaoUrl())
                    .replace("###id###", item.getId())
                    .replace("###title###", item.getTitle())
                    .replace("###price###", String.valueOf(item.getPrice()))
                    .replace("###pricePromote###", String.valueOf(item.getPricePromote()))
                    .replace("###stock###", String.valueOf(item.getStock()))
                    .replace("###basicInfo###", item.getBasicInfo())
                    .replace("###detailImgs###", detailImgs);

            File outfile = new File(filename);
            fw = new FileWriter(outfile);
            fw.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将淘宝商品输出为网页文件，指定输出路径，文件名为 id.html, id为商品编号
     * @param items 商品
     * @param outDir 输出路径
     */
    public void writeHtmlPages(Collection<TaobaoItem> items, String outDir) {
        for (TaobaoItem item: items) {
            String filename = outDir+"/"+item.getId()+".html";
            this.writeHtmlPage(item, filename);
        }
    }

    /**
     * 读取模板内容
     */
    private void readTemplate() {
        InputStreamReader ir = null;
        BufferedReader br = null;
        StringBuffer buf = new StringBuffer();
        try {
            InputStream ins = this.getClass().getResourceAsStream(TEMPLATE_FILENAME);
            ir = new InputStreamReader(ins);
            br = new BufferedReader(ir);

            String line = null;
            while ((line=br.readLine())!=null){
                buf.append(line+"\r\n");
            }

            this.htmlTemplate = buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
