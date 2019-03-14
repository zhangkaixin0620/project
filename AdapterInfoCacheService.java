/*
 * 文 件 名:  AdapterInfoCache.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  HJL
 * 修改时间:  2009-10-19
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.huawei.iread.cache.service;
import java.util.List;

import com.huawei.iread.cache.util.CacheUtil;
import com.huawei.iread.portal.oscache.cache.PortalCacheManager;
import com.huawei.iread.server.constant.Types;
import com.huawei.iread.terminal.logic.adapter.ContentResolution;
/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author h00101670
 * @version [版本号, 2009-10-19]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class AdapterInfoCacheService extends BaseCacheService
{

    /** 单实例对象 */
    private static AdapterInfoCacheService instance = new AdapterInfoCacheService();

    public final static String CACHE_NAME = "ADAPTERINFOCACHE";
    /**
     * 单实例方法
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static AdapterInfoCacheService getInstance()
    {
        return instance;
    }

    /**
     * 根据内容类型和手机屏幕，适配展示的内容屏幕大小
     * 
     * @param contentType
     * @param screen
     * @return
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public String getAdaptScreen(String contentType, String screen)
    {
        String key = this.genKey(contentType, screen);
        String[] param = screen.split("[*]");
        // modify by kf41851 at 2012-2-6 for IRD-22251 begin
        int adapterWidth = 0;
        int adapterHigh = 0;
        if (param != null && param.length >= 2)
        {
            adapterWidth = Integer.parseInt(param[0]);
            adapterHigh = Integer.parseInt(param[1]);
        }
        int area = adapterWidth * adapterHigh;

        ContentResolution content = (ContentResolution)CacheUtil.getCachedData(CACHE_NAME, key);
        content = (content == null) ? new ContentResolution() : content;
        String adaptScreen = this.genScreen(content.getBaseWidth() + "", content.getBaseHigh() + "");

        if ("0*0".equals(adaptScreen))
        {
            if (Types.CARTOON_TYPE.equals(contentType))
            {
                //List<ContentResolution> list = (List<ContentResolution>) this.getDataFromCache(Types.CARTOON_TYPE);
                List<ContentResolution> list = (List<ContentResolution>)CacheUtil.getCachedData(CACHE_NAME, Types.CARTOON_TYPE);
                adaptScreen = getAdaptScreen(list, adapterWidth, adapterHigh, area);
                if (adaptScreen == null)
                {
                    adaptScreen = PortalCacheManager.getConfigValue("client_default_screen");
                }
            }
            else if (Types.MAGAZINE_TYPE.equals(contentType))
            {
                //List<ContentResolution> list = (List<ContentResolution>) this.getDataFromCache(Types.MAGAZINE_TYPE);
                List<ContentResolution> list = (List<ContentResolution>) (List<ContentResolution>)CacheUtil.getCachedData(CACHE_NAME, Types.MAGAZINE_TYPE);
                adaptScreen = getAdaptScreen(list, adapterWidth, adapterHigh, area);
                if (adaptScreen == null)
                {
                    adaptScreen = PortalCacheManager.getConfigValue("client_default_magezine_screen");
                }
            }
        }

        return adaptScreen;
    }

    /**
     * 根据内容类型和屏幕大小构造缓存的key
     * 
     * @param contentType
     * @param screen
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String genKey(String contentType, String screen)
    {
        return contentType + "|" + screen;
    }

    /**
     * 手机屏幕大小表示方式
     * 
     * @param width
     * @param height
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String genScreen(String width, String height)
    {
        return width + "*" + height;
    }


    /**
     * 对List进行遍历，获取第一个符合条件的，内容宽小于屏幕宽，内容高小于屏幕高
     * 
     * @param list
     *            ，adapterWidth，adapterHigh，area
     * @return String
     * @see [类、类#方法、类#成员]
     */
    private String getAdaptScreen(List<ContentResolution> list, int adapterWidth, int adapterHigh, int area)
    {
        boolean bool = false;
        int count = -1;
        String adaptScreen = null;
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).getArea() <= area)
                {
                    bool = true;
                }
                if (bool)
                {
                    if (list.get(i).getBaseWidth() <= adapterWidth && list.get(i).getBaseHigh() <= adapterHigh)
                    {
                        count = i;
                        bool = false;
                        break;
                    }
                    bool = false;
                }
            }
        }
        if (count != -1)
        {
            adaptScreen = this.genScreen(list.get(count).getBaseWidth() + "", list.get(count).getBaseHigh() + "");
        }
        return adaptScreen;
    }
    
    public void init()
    {
        
    }
    
    @Override
    protected String getCacheName()
    {
        return CACHE_NAME;
    }

}
