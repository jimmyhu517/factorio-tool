import model.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author huyiqi
 */
public class Main {

    private static final String RAW_ELEMENT_KEY = "raw";
    private static final String PRODUCTION_ELEMENT_KEY = "production";
    private static final String MINER_ELEMENT_KEY = "miner";
    private static final String FACTORY_ELEMENT_KEY = "factory";

    private static Map<String, Raw> rawMap = new HashMap<>();
    private static Map<String, Production> productionMap = new HashMap<>();
    private static Map<String, Miner> minerMap = new HashMap<>();
    private static Map<String, Factory> factoryMap = new HashMap<>();
    /**
     * 需求的最终产物，单位为（个/分钟）
     */
    private static Map<String, Integer> requirement = new HashMap<>(4);

    private static Map<String, Map<String, Integer>> factoryResult = new HashMap<>();

    public static void main(String[] args) {
        readXML();
        requirement.put("p4", 45);
        requirement.put("p5", 45);
        calculate(requirement, new BigDecimal(1), 0);
        printFactoryResult();
    }

    /**
     * 计算用量情况
     * @param map 需求map
     * @param fatherNum 父节点数量
     * @param level 层级
     * @return 以树的形式返回用量情况
     */
    private static List<ResultTree> calculate(Map<String, Integer> map, BigDecimal fatherNum, int level) {
        List<ResultTree> children = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            BigDecimal num = new BigDecimal(map.get(key));
            try {
                ResultTree child = getProductInfo(key, num.multiply(fatherNum), level);
                if(child != null) {
                    children.add(child);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return children;
    }

    /**
     * 执行计算的方法
     * @param key 产品id
     * @param num 产品每分钟需求量
     * @param level 层级
     * @return 返回计算结果
     * @throws Exception 产品信息中没有配置用于建造的工厂，会报错
     */
    private static ResultTree getProductInfo(String key, BigDecimal num, int level) throws Exception {
        int scale = 10;
        int type = BigDecimal.ROUND_HALF_DOWN;
        BigDecimal secPerMin = new BigDecimal(60);
        if(productionMap.containsKey(key)) {
            Production production = productionMap.get(key);
            String proId = production.getId();
            String proName = production.getName();
            String factoryId = production.getFactory();
            if(factoryMap.containsKey(factoryId)) {
                Factory factory = factoryMap.get(factoryId);
                BigDecimal proTime = production.getTime();
                BigDecimal proOutput = production.getOutput();
                BigDecimal facEfficiency = factory.getEfficiency();
                BigDecimal facOutput = factory.getOutput();
                String facName = factory.getName();
                String msgTemp = "-%s每分钟【%s】个【%s】，需要【%s】个【%s】";
                BigDecimal factoryNum = num.divide(secPerMin, scale, type).multiply(proTime).divide(proOutput, scale, type).divide(facOutput, scale, type).divide(facEfficiency, scale, type);
                factoryNum = factoryNum.setScale(0, BigDecimal.ROUND_UP);
                //输出需求信息
                String msg = String.format(msgTemp, tabs(level), num, proName, factoryNum, facName);
                System.out.println(msg);
                //将工厂数量放到map中累加，用于统计
                setResultMap(factoryId, factoryNum.intValue(), proId);
                //递归计算子节点信息
                ResultTree tree = new ResultTree(proId, String.format(msg, num, proName, factoryNum, facName));
                Map<String, Integer> preMap = production.getPre();
                if(!preMap.isEmpty()) {
                    nextRow();
                    List<ResultTree> children = calculate(preMap, num, level + 1);
                    tree.setChildren(children);
                }
                return tree;
            } else {
                String msg = "无法找到%s中配置的工厂%s";
                throw new Exception(String.format(msg, proId, factoryId));
            }
        }
        return null;
    }

    /**
     * 根据层级返回前面的空格
     * @param level 层级
     * @return 返回空格
     */
    private static String tabs(int level) {
        char c = '-';
        int space = 2;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < level * space; i ++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 打印竖线以换行
     */
    private static void nextRow() {
        System.out.println("|");
    }

    /**
     * 打印工厂统计结果
     */
    private static void printFactoryResult() {
        if(!factoryResult.isEmpty()) {
            String msg = "共需要：\r\n%s";
            StringBuilder sb = new StringBuilder();
            String facMsgTemp = "【%s】个【%s】，其中：\r\n";
            String proMsgTemp = "   【%s】个用于生产【%s】";
            for (String key: factoryResult.keySet()) {
                Map<String, Integer> facMap = factoryResult.get(key);
                //工厂总量
                int num = facMap.get("total");
                String facName = factoryMap.get(key).getName();
                sb.append(String.format(facMsgTemp, num, facName));
                //分类
                for(String proKey: facMap.keySet()) {
                    if(!"total".equals(proKey)) {
                        int proNum = facMap.get(proKey);
                        String proName = productionMap.get(proKey).getName();
                        sb.append(String.format(proMsgTemp, proNum, proName));
                    }
                }
                sb.append("\r\n");
            }
            System.out.println(String.format(msg, sb.toString()));
        }
    }

    /**
     * 设置工厂统计结果
     * @param facId 工厂id
     * @param facNum 需要的工厂数量
     */
    private static void setResultMap(String facId, int facNum, String proId) {
        if(factoryResult.containsKey(facId)) {
            Map<String, Integer> facMap = factoryResult.get(facId);
            if(facMap.containsKey(proId)) {
                int proNum = facMap.get(proId);
                facMap.put(proId, proNum + facNum);
            } else {
                facMap.put(proId, facNum);
            }
            int totalNum = facMap.get("total");
            facMap.put("total", totalNum + facNum);
        } else {
            Map<String, Integer> facMap = new HashMap<>(8);
            facMap.put("total", facNum);
            facMap.put(proId, facNum);
            factoryResult.put(facId, facMap);
        }
    }

    /**
     * 读取xml文件
     */
    private static void readXML() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File("resource/item-data.xml"));
            //根节点
            Element database = document.getRootElement();
            Iterator it = database.elementIterator();
            while (it.hasNext()) {
                //种类
                Element type = (Element) it.next();
                Iterator itemIt = type.elementIterator();
                String typeName = type.getName();
                while (itemIt.hasNext()) {
                    Element item = (Element) itemIt.next();
                    String id = item.attribute("id").getValue();
                    String name = item.attribute("name").getValue();
                    switch (typeName) {
                        //原材料
                        case RAW_ELEMENT_KEY:
                            Raw raw = new Raw();
                            raw.setId(id);
                            raw.setName(name);
                            raw.setTime(new BigDecimal(item.attribute("time").getValue()));
                            raw.setDifficulty(new BigDecimal(item.attribute("difficulty").getValue()));
                            rawMap.put(id, raw);
                            break;
                        //产品
                        case PRODUCTION_ELEMENT_KEY:
                            Production pro = new Production();
                            pro.setId(id);
                            pro.setName(name);
                            pro.setFactory(item.attribute("factory").getValue());
                            pro.setOutput(new BigDecimal(item.attribute("output").getValue()));
                            pro.setTime(new BigDecimal(item.attribute("time").getValue()));
                            Map<String, Integer> preMap = new HashMap<>(4);
                            Iterator preIt = item.elementIterator();
                            while (preIt.hasNext()) {
                                Element pre = (Element) preIt.next();
                                String preId = pre.attribute("id").getValue();
                                int num = Integer.parseInt(pre.attribute("num").getValue());
                                preMap.put(preId, num);
                            }
                            pro.setPre(preMap);
                            productionMap.put(id, pro);
                            break;
                        //采掘器
                        case MINER_ELEMENT_KEY:
                            Miner miner = new Miner();
                            miner.setId(id);
                            miner.setName(name);
                            miner.setOutput(new BigDecimal(item.attribute("output").getValue()));
                            miner.setSpeed(new BigDecimal(item.attribute("speed").getValue()));
                            miner.setStrength(new BigDecimal(item.attribute("strength").getValue()));
                            minerMap.put(id, miner);
                            break;
                        //工厂
                        case FACTORY_ELEMENT_KEY:
                            Factory factory = new Factory();
                            factory.setId(id);
                            factory.setName(name);
                            factory.setOutput(new BigDecimal(item.attribute("output").getValue()));
                            factory.setEfficiency(new BigDecimal(item.attribute("efficiency").getValue()));
                            factoryMap.put(id, factory);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
