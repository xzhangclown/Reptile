package so.sao.crawler;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author xzhang
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
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            jedis.sadd("threeUrlList1",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getThree(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.spop("threeUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
            return getThree();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void addSecond(String url){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            jedis.sadd("secondUrlList1",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getSecond(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.spop("secondUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
            return getSecond();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void addFirst(String url){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            jedis.sadd("firstUrlList1",url);
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getFirst(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.spop("firstUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
            return getFirst();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static Long getFirstSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.scard("firstUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0L;
    }

    public static Long getSecondSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.scard("secondUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0L;
    }

    public static Long getThreeSize(){
        Jedis jedis = null;
        try{
            jedis = new Jedis("10.100.50.55", 7001, 10000);
            return jedis.scard("threeUrlList1");
        }catch (Exception ee){
            ee.printStackTrace();
        }finally {
            try{
                if(jedis!=null){
                    jedis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0L;
    }
}
