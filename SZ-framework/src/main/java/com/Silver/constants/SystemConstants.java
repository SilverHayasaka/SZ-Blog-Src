package com.Silver.constants;

public class SystemConstants {
    /**
     * 文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     * 文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;

    public static final String STATUS_NORMAL = "0";


    public static final String NORMAL = "0";

    public static final String ADMIN = "1";

    //友链审核通过
    public static final String LINK_STATUS_NORMAL = "0";

    //根评论
    public static final int ROOT_COMMENT = -1;

    //1表删除
    public static final int DEL_FLAG = 1;

    public static final String ARTICLE_COMMENT = "0";
    public static final String LINK_COMMENT = "1";

    public static final String MENU = "C";
    public static final String BUTTON = "F";
    public static final String Redis_VIEWCOUNT_KEY = "article:viewCount";

    public static final String REDIS_CATEGORY_CACHE = "cache:category";

    public static final String HOT_ARTICLE_CACHE = "cache:hotArticle";

    public static final String ARTICLE_LIST_CACHE = "cache:articleList";

    public static final String ARTICLE_CACHE = "cache:article:";

    public static final String FRIEND_LINK_CACHE = "cache:friendLink";

    public static final String USER_INFO_CACHE = "cache:userInfo:";

    public static final String USER_AVATAR_CACHE = "cache:avatar:";
}