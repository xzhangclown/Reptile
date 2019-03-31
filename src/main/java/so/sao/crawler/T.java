package so.sao.crawler;/**
 * Created by lenovo on 2017/10/16.
 */

import redis.clients.jedis.Jedis;
import java.util.*;
/**
 * 
 * @author xzhang
 * @create 2017-10-16 9:31
 **/
public class T {
    public static void main11111(String[] args) {
        Jedis jedis = new Jedis("10.100.50.55", 7001, 10000);
        //jedis.smembers("secondUrlList1").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        //jedis.smove("secondUrls","secondUrlss",o);
        //jedis.smembers("secondUrls").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        //jedis.smembers("firstUrls").forEach(o->{if(o.split("#").length>2){System.out.println(o);}});
        Long len = jedis.llen("secondUrlList1");
        jedis.lrange("secondUrlList1",0,len).forEach(o->{if(o.split("#").length>2){System.out.println(o);}});;
        //jedis.lrange("secondUrlList1",0,len).forEach(o->{if(o.split("#").length>2){jedis.lrem("secondUrlList1",1,o);}});;

        //jedis.sadd("firstUrlList11",jedis.lrange("firstUrlList1",0,-1).toArray(new String[jedis.llen("firstUrlList1").intValue()]));
        jedis.lrange("firstUrlList1",0,-1).forEach(o->jedis.sadd("firstUrlList11",o));
        jedis.lrange("secondUrlList1",0,-1).forEach(o->jedis.sadd("secondUrlList11",o));
        jedis.lrange("threeUrlList1",0,-1).forEach(o->jedis.sadd("threeUrlList11",o));
        //jedis.sadd("secondUrlList11",jedis.lrange("secondUrlList1",0,-1).toArray(new String[jedis.llen("secondUrlList1").intValue()]));
        //jedis.sadd("threeUrlList11",jedis.lrange("threeUrlList1",0,-1).toArray(new String[jedis.llen("threeUrlList1").intValue()]));
        jedis.close();
        // http://search.yhd.com/c0-0-1005398/mbname-b/a-s1-v4-p1-price-d0-f0-m1-rt0-pid-mid0-color-size-k/#page=50&sort=1#page=21&sort=1
    }
}
