package demo.okhttp.com.myapplication;

import java.io.Serializable;
import java.util.List;

import demo.okhttp.com.mylibrary.bean.BaseResult;

/**
 * Created by zhouyunfang on 17/6/14.
 */

public class CotegoryBean extends BaseResult implements Serializable {

    private List<Topics> topics;

    private List<Categorys> categorys;

    public List<Topics> getTopics() {
        return topics;
    }

    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }

    public List<Categorys> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<Categorys> categorys) {
        this.categorys = categorys;
    }

    public class Topics implements Serializable{
        private String title;
        private String color;
        private String clicks;
        private String image;
        private String url;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getClicks() {
            return clicks;
        }

        public void setClicks(String clicks) {
            this.clicks = clicks;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


    public class  Categorys implements Serializable{
        private String englishName;
        private String id;
        private String sort;
        private String name;
        private String shelfId;
        private String image;


        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShelfId() {
            return shelfId;
        }

        public void setShelfId(String shelfId) {
            this.shelfId = shelfId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
