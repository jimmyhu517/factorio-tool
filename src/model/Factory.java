package model;

import java.math.BigDecimal;

/**
 * 工厂
 *
 * @author huyiqi
 * @date 2018/5/14
 */
public class Factory extends BaseModel {
    private BigDecimal efficiency;
    private BigDecimal output;

    public BigDecimal getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(BigDecimal efficiency) {
        this.efficiency = efficiency;
    }

    public BigDecimal getOutput() {
        return output;
    }

    public void setOutput(BigDecimal output) {
        this.output = output;
    }
}
