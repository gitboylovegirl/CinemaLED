package com.cinemaled.bean;

import java.util.List;

/**
 * created by fred
 * on 2019/12/6
 */
public class MovieBean {

    /**
     * status : 200
     * md5 : 9c578e07200c87f9e86ba6139dd03c60
     * data : [{"begin_date":1574092800,"end_date":1576771199,"order_id":"100927939","duration":null,"src":"https://img.dianjiutong.cn/files/201911/15741530845dd3ab7c45ac4.mp4","md5":"355e9a26ab4f42c8c9622cc6032ca06e"},{"begin_date":1575519840,"end_date":1577247720,"order_id":"100927967","duration":null,"src":"https://img.dianjiutong.cn/files/201912/15755194365de884cc8d092.mp4","md5":"99a09bde3825857f6088dcf3ff3191a7"},{"begin_date":1575475200,"end_date":1578239999,"order_id":"100927968","duration":null,"src":"https://img.dianjiutong.cn/files/201912/15755207825de88a0ebb9b9.mp4","md5":"346ddb567d800c31196f92ba7e28100a"},{"begin_date":1575475200,"end_date":1578239999,"order_id":"100927969","duration":null,"src":"https://img.dianjiutong.cn/files/201912/15755215705de88d229039b.mp4","md5":"364e0c5e0ca5d7efdd0b80543f157145"}]
     */

    private int status;
    private String md5;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * begin_date : 1574092800
         * end_date : 1576771199
         * order_id : 100927939
         * duration : null
         * src : https://img.dianjiutong.cn/files/201911/15741530845dd3ab7c45ac4.mp4
         * md5 : 355e9a26ab4f42c8c9622cc6032ca06e
         */

        private int begin_date;
        private int end_date;
        private String order_id;
        private Object duration;
        private String src;
        private String md5;

        public int getBegin_date() {
            return begin_date;
        }

        public void setBegin_date(int begin_date) {
            this.begin_date = begin_date;
        }

        public int getEnd_date() {
            return end_date;
        }

        public void setEnd_date(int end_date) {
            this.end_date = end_date;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public Object getDuration() {
            return duration;
        }

        public void setDuration(Object duration) {
            this.duration = duration;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}
