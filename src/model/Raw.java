package model;

import java.math.BigDecimal;

/**
 * 原材料
 *
 * @author huyiqi
 * @date 2018/5/14
 */
public class Raw extends BaseModel{
    private BigDecimal time;
    private BigDecimal difficulty;

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public BigDecimal getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BigDecimal difficulty) {
        this.difficulty = difficulty;
    }
}
