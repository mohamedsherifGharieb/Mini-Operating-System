public class Var {
    private String Name;


    private String VALUE;

    public Var(String variable, String value) {
        this.Name = variable;
        this.VALUE = value;
    }

    public Var() {

    }

    public String getValue() {
        return VALUE;
    }

    public void setValue(String VALUE) {
        this.VALUE = VALUE;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

