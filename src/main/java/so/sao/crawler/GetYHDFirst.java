package so.sao.crawler;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.System.*;

/**
 * 
 * @author xzhang
 * @create 2017-09-30 10:24
 **/
public class GetYHDFirst implements PageProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //static List firstUrls = new ArrayList();
    //static Jedis jedis = new Jedis("10.100.50.55", 7001, 10000);
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
        //System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe");
        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\ChromeDriver.exe")).usingAnyFreePort().build();
        WebDriver driver = null;
        try{
            service.start();
            //driver = new ChromeDriver();
            driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
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
            try{
                jedis = new Jedis("10.100.50.55", 7001, 10000);
                for(String url:urls){
                    Boolean e = jedis.sismember("firstUrls","http:"+url);
                    Boolean ig = jedis.sismember("ig","http:"+url);
                    if(e!=null&&!e&&!ig){
                        Long r = jedis.sadd("firstUrls","http:"+url);
                        if(r>0)
                        UrlQueue.addFirst("http:"+url);
                    }
                }
            }catch (Exception ee){
                ee.printStackTrace();
                logger.error(ee.getMessage());
            }finally {
                try{
                    if(jedis!=null){
                        jedis.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            //System.out.println(urls);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
            try{
                driver.quit();
                service.stop();
            }catch (Exception ee){
                ee.printStackTrace();
                logger.error(ee.getMessage());
            }
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //CustomThreadPoolExecutor exec = new CustomThreadPoolExecutor();
        // 1.初始化
        //exec.init();

        //ExecutorService pool = exec.getCustomThreadPoolExecutor();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        Future<Spider> firstSpiders = pool.submit(new Callable<Spider>(){
            @Override
            public Spider call() throws Exception {
                Spider firstSpider = Spider.create(new GetYHDFirst())
                        .addUrl(url)
                        .addPipeline(new ConsolePipeline());
                firstSpider.start();
                return firstSpider;
            }
        });
        pool.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        if("Stopped".equals(firstSpiders.get().getStatus().name())){
                            /*firstSpiders.get().getScheduler().push(new Request(url),firstSpiders.get());
                            firstSpiders.get().start();*/
                        }
                        out.println("firstSpiders:"+firstSpiders.get().getStatus().name());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //fileName = args[0];
        /*new Thread(new Runnable(){
            @Override
            public void run() {
                Spider.create(new GetYHDFirst())
                        .addUrl(url)
                        .addPipeline(new ConsolePipeline()).run();
            }
        }).start();*/
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        final Future<Spider> secondSpiders = pool.submit(new Callable<Spider>(){
            @Override
            public Spider call() throws Exception {
                while(UrlQueue.getFirstSize()==0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String firstUrl = UrlQueue.getFirst();
                Spider secondSpider=Spider.create(new GetYHDSecond())
                        .addUrl(firstUrl)
                        .addPipeline(new ConsolePipeline());
                secondSpider.start();
                return secondSpider;
            }
        });
        pool.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        if("Stopped".equals(secondSpiders.get().getStatus().name())){
                            secondSpiders.get().getScheduler().push(new Request(UrlQueue.getFirst()),secondSpiders.get());
                            secondSpiders.get().start();
                        }
                        out.println("secondSpiders:"+secondSpiders.get().getStatus().name());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
       /* new Thread(new Runnable(){
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
        }).start();*/
        final Future<Spider> threeSpiders = pool.submit(new Callable<Spider>(){
            @Override
            public Spider call() throws Exception {
                while(UrlQueue.getSecondSize()==0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String secondUrl = UrlQueue.getSecond();
                Spider threeSpider = Spider.create(new GetYHDThree())
                        .addUrl(secondUrl)
                        .addPipeline(new ConsolePipeline());
                threeSpider.start();
                return threeSpider;
            }
        });
        pool.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        if("Stopped".equals(threeSpiders.get().getStatus().name())){
                            threeSpiders.get().getScheduler().push(new Request(UrlQueue.getSecond()),threeSpiders.get());
                            threeSpiders.get().start();
                        }
                        out.println("threeSpiders:"+threeSpiders.get().getStatus().name());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*pool.execute(new Runnable(){
            @Override
            public void run() {
                while(UrlQueue.getSecondSize()==0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String secondUrl = UrlQueue.getSecond();
                Spider.create(new GetYHDThree())
                        .addUrl(secondUrl)
                        .addPipeline(new ConsolePipeline()).run();
            }
        });*/
        /*new Thread(new Runnable(){
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
        }).start();*/
         Future<Spider> fourSpiders = pool.submit(new Callable<Spider>(){
            @Override
            public Spider call() throws Exception {
                while(UrlQueue.getThreeSize()==0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String threeUrl = UrlQueue.getThree();
                Spider fourSpider = Spider.create(new GetYHDFour())
                        .addUrl(threeUrl)
                        .addPipeline(new ConsolePipeline());
                fourSpider.start();
                return fourSpider;
            }
        });
        pool.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        out.println("fourSpiders:"+fourSpiders.get().getStatus().name());
                        if("Stopped".equals(fourSpiders.get().getStatus().name())){
                            //TODO 404被丢弃问题
                            fourSpiders.get().getScheduler().push(new Request(UrlQueue.getThree()),fourSpiders.get());
                            fourSpiders.get().start();
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*pool.execute(new Runnable(){
            @Override
            public void run() {
                while(UrlQueue.getThreeSize()==0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String threeUrl = UrlQueue.getThree();
                Spider.create(new GetYHDFour())
                        .addUrl(threeUrl)
                        .addPipeline(new ConsolePipeline()).run();
            }
        });*/
       /* new Thread(new Runnable(){
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
        }).start();*/

        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
