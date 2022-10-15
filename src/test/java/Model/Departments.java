package Model;

import java.util.Map;

public class Departments {

    private String id;
    private String name;
    private String code;
    private boolean active;
    private String SchoolId;

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(Map<String, String> schoolId) {
        this.SchoolId = String.valueOf(schoolId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}

