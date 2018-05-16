package model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 产品
 *
 * @author huyiqi
 * @date 2018/5/14
 */
public class Production extends BaseModel{
    private BigDecimal time;
    private BigDecimal output;
    private String factory;
    private Map<String, Integer> pre;

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public BigDecimal getOutput() {
        return output;
    }

    public void setOutput(BigDecimal output) {
        this.output = output;
    }

    public Map<String, Integer> getPre() {
        return pre;
    }

    public void setPre(Map<String, Integer> pre) {
        this.pre = pre;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }
}
