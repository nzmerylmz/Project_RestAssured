package Model;
import Model.School;

import java.util.Arrays;
import java.util.Map;

public class Departments {

    private boolean active;
    private String code;
    private String[] constans;
    private String id;
    private String name;
    private School school;
    private String[] sections;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String[] getConstans() {
        return constans;
    }

    public void setConstans(String[] constans) {
        this.constans = constans;
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public String[] getSections() {
        return sections;
    }

    public void setSections(String[] sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "Departments{" +
                "active=" + active +
                ", code='" + code + '\'' +
                ", constans=" + Arrays.toString(constans) +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", school=" + school +
                ", sections=" + Arrays.toString(sections) +
                '}';
    }
}

