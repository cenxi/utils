package common.utils.sql;

public enum DbColType {

    VARCHAR("varchar"), INT("int"), TIMESTAMP("timestamp"), TEXT("text");

    public String getType() {
        return type;
    }

    private String type;

    private static DbColType[] containeLengthList = {VARCHAR};

    DbColType(String type) {
        this.type = type;
    }

    public boolean containLength() {
        for (DbColType type : containeLengthList) {
            if (this == type) {
                return true;
            }
        }
        return false;
    }


}
