package so.sao.crawler;/**
 * Created by lenovo on 2017/10/16.
 */

import redis.clients.jedis.Jedis;

/**
 * 
 * @author guangpu.yan
 * @create 2017-10-16 9:31
 **/
public class T {
    public static void main1111111111(String[] args) {
        Jedis jedis = new Jedis("10.100.50.55", 6379, 10000);
        //jedis.smembers("secondUrlList").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        //jedis.smove("secondUrls","secondUrlss",o);
        //jedis.smembers("secondUrls").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        //jedis.smembers("firstUrls").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        Long len = jedis.llen("secondUrlList");
        jedis.lrange("secondUrlList",0,len).forEach(o->{if(o.split("#").length>2){System.out.println(o);}});;
        //jedis.lrange("secondUrlList",0,len).forEach(o->{if(o.split("#").length>2){jedis.lrem("secondUrlList",1,o);}});;
        jedis.close();
        // http://search.yhd.com/c0-0-1005398/mbname-b/a-s1-v4-p1-price-d0-f0-m1-rt0-pid-mid0-color-size-k/#page=50&sort=1#page=21&sort=1
    }
}
