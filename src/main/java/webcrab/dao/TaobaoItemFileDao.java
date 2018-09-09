package webcrab.dao;

import webcrab.model.TaobaoItem;

import java.io.FileWriter;
import java.io.IOException;

public class TaobaoItemFileDao {
    private FileWriter writer;

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
}
