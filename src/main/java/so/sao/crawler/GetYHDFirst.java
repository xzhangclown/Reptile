package so.sao.crawler;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.*;

/**
 * 
 * @author guangpu.yan
 * @create 2017-09-30 10:24
 **/
public class GetYHDFirst implements PageProcessor {
    //static List firstUrls = new ArrayList();
    //static Jedis jedis = new Jedis("10.100.50.55", 6379, 10000);
    static String fileName = null;
    static String key = null;
    static String url = "http://www.yhd.com/";
    private static Site site = Site.me().setTimeOut(200000).setCycleRetryTimes(5).setDomain("www.yhd.com").setCharset("utf-8");
              /*.addCookie("3AB9D23F7A4B3C9B",	"EWN36RRRCKHO7QJIQLUZHLFNMQHL67PJLZWSLN2QNFEMNO6XU5NHFSVBXSWCWPHYZXXU3IEVDBLNHSDP4YU3DLP7BE")
                .addCookie("__jda","122270672.193823978.1500130744.1506738733.1506747940.5")
                .addCookie("__jdb","122270672.3.193823978|5.1506747940")
                .addCookie("__jdc","122270672")
                .addCookie("__jdu","193823978")
                .addCookie("__jdv","122270672|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_327c74f8c71b4cee92813c3e1b995688|1506736337005")
                .addCookie("ipLoc-djd","1-72-2799-0")
                .addCookie("qrsc","3")
                .addCookie("rkv","V0800")
                .addCookie("unpl","V2_ZzNtbUBURUdzDRFdKR9cVmJRRglKVEoVd1pEAHtKVA1jBBtUclRCFXMUR1xnG10UZgsZWUFcQxBFCHZXchBYAWcCGllyBBNNIEwHDCRSBUE3XHxcFVUWF3RaTwEoSVoAYwtBDkZUFBYhW0IAKElVVTUFR21yVEMldQl2VHMbVQZlCxRUcmdEJUU4Qld6G14AVwIiXHIVF0lzD05dfR0RBW8BG15AX0UcRQl2Vw%3d%3d");*/
    public void process(Page page) {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe");
        WebDriver driver = null;
        try{
            driver = new ChromeDriver();
            driver.get(page.getUrl().get());
            Actions action = new Actions(driver);
            List<WebElement> webElements = driver.findElement(By.className("hd_more_allsort")).findElements(By.tagName("li"));
            for(WebElement o:webElements){
                Thread.sleep(5000);
                action.moveToElement(o).perform();
            };
            List<WebElement> webElementList = driver.findElements(By.className("hd_show_sort"));
            List<String>urls = new ArrayList();
            webElementList.forEach(o-> {urls.addAll(new Html(o.getAttribute("outerHTML")).xpath("//div[@class='hd_good_category']/dl/dd").links().all());});
            Jedis jedis = null;
            for(String url:urls){
                try{
                    jedis = new Jedis("10.100.50.55", 6379, 10000);
                    Boolean e = jedis.sismember("firstUrls","http:"+url);
                    Boolean ig = jedis.sismember("ig","http:"+url);
                    if(e!=null&&!e&&!ig){
                        Long r = jedis.sadd("firstUrls","http:"+url);
                        if(r>0)
                        UrlQueue.addFirst("http:"+url);
                    }
                }catch (Exception ee){
                    ee.printStackTrace();
                }finally {
                    jedis.close();
                }
            }
            //System.out.println(urls);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                driver.quit();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //fileName = args[0];
        new Thread(new Runnable(){
            @Override
            public void run() {
                Spider.create(new GetYHDFirst())
                        .addUrl(url)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }).start();
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(UrlQueue.getFirstSize()==0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String firstUrl = UrlQueue.getFirst();
                Spider.create(new GetYHDSecond())
                        .addUrl(firstUrl)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }).start();

        new Thread(new Runnable(){
            @Override
            public void run() {
                while(UrlQueue.getSecondSize()==0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String secondUrl = UrlQueue.getSecond();
                Spider.create(new GetYHDThree())
                        .addUrl(secondUrl)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }).start();

        new Thread(new Runnable(){
            @Override
            public void run() {
                while(UrlQueue.getThreeSize()==0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String threeUrl = UrlQueue.getThree();
                Spider.create(new GetYHDFour())
                        .addUrl(threeUrl)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }).start();

        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
