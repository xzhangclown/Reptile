package so.sao.crawler;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author guangpu.yan
 * @create 2017-10-01 21:34
 **/
public class UrlQueue {
    //单机跑
    /*public static ArrayBlockingQueue<String>firstQueue;
    public static ArrayBlockingQueue<String>secondQueue;
    public static ArrayBlockingQueue<String>threeQueue;
    static {
        firstQueue = new ArrayBlockingQueue(1000);
        secondQueue = new ArrayBlockingQueue(1000);
        threeQueue = new ArrayBlockingQueue(1000);
    }*/
    public static void addThree( String url){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            jedis.lpush("threeUrlList",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
            jedis.close();
        }
    }

    public static String getThree(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.lpop("threeUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
            return getThree();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }
    public static void addSecond(String url){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            jedis.lpush("secondUrlList",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    public static String getSecond(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.lpop("secondUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
            return getSecond();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    public static void addFirst(String url){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            jedis.lpush("firstUrlList",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    public static String getFirst(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.lpop("firstUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
            return getFirst();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    public static Long getFirstSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.llen("firstUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0L;
    }

    public static Long getSecondSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.llen("secondUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0L;
    }

    public static Long getThreeSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 6379, 10000);
            return jedis.llen("threeUrlList");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0L;
    }
}
