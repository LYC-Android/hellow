package bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by mr.cheng on 2016/9/10.
 */
public class DowmloadTable extends BmobObject {
    private MyUser author;
    private List<String> times;
    private List<String> urls;

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
