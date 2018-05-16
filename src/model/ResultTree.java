package model;

import java.util.List;

/**
 *
 * @author huyiqi
 * @date 2018/5/16
 */
public class ResultTree {
    private String id;
    private String message;
    private List<ResultTree> children;

    public ResultTree(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultTree> getChildren() {
        return children;
    }

    public void setChildren(List<ResultTree> children) {
        this.children = children;
    }
}
