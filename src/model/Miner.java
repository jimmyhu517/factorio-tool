package model;

import java.math.BigDecimal;

/**
 * 资源采集
 *
 * @author huyiqi
 * @date 2018/5/14
 */
public class Miner extends BaseModel{
    private BigDecimal strength;
    private BigDecimal speed;
    private BigDecimal output;

    public BigDecimal getStrength() {
        return strength;
    }

    public void setStrength(BigDecimal strength) {
        this.strength = strength;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getOutput() {
        return output;
    }

    public void setOutput(BigDecimal output) {
        this.output = output;
    }
}
