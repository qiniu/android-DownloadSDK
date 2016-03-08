package com.qiniu.android.download.v1;

/**
 * Created by Misty on 16/2/16.
 */
public final class DownloadOption {

    private static final String TAG = "DownloadOption";

    /**
     * 文件名
     */
    final String attname;

    /**
     * fop
     */
    final String fop;

    public DownloadOption(Builder builder)
    {
        this.attname = builder.attname;
        this.fop = builder.fop;
    }

    public static class Builder{
        private String attname;
        private String fop;

        public Builder()
        {
            attname = null;//默认文件名,使用key
            fop = null;//默认不使用数据处理
        }

        public Builder setAttname(String attname)
        {
            this.attname = attname;
            return this;
        }

        public Builder setFop(String fop)
        {
            this.fop = fop;
            return this;
        }

        private String filterFop(String fop)
        {
            if(fop == null || fop.equals("")) {
                return null;
            }
            String res = "";
            if(fop.indexOf("|") == -1)
            {
                //判断fop是否合法
            }
            else
            {
                String[] str = fop.split("|");
                for(int i = 0 ;i < str.length; i++)
                {

                }
            }
            return res;
        }

        public DownloadOption builder()
        {
            return new DownloadOption(this);
        }
    }
}
