public class Data {
    private final String data;
    private final DataType dataType;

    public Data(String data_, DataType dataType_){
        this.data = data_;
        this.dataType = dataType_;
    }

    public String toString(){
        return data + " - " + dataType;
    }
}
