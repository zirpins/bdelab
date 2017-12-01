package manning.tap;

import com.backtype.cascading.tap.PailTap;
import com.backtype.hadoop.pail.PailSpec;
import com.backtype.hadoop.pail.PailStructure;

public class DataPailTap extends PailTap {
    public static class DataPailTapOptions {
        public PailSpec spec = null;
        public String fieldName = "data";

        public DataPailTapOptions() {

        }

        public DataPailTapOptions(PailSpec spec, String fieldName) {
            this.spec = spec;
            this.fieldName = fieldName;
        }
    }

    public DataPailTap(String root, DataPailTapOptions options) {
        super(root, new PailTapOptions(PailTap.makeSpec(options.spec, getSpecificStructure()), options.fieldName, null, null));
    }

    public DataPailTap(String root) {
        this(root, new DataPailTapOptions());
    }

    protected static PailStructure getSpecificStructure() {
        return new DataPailStructure();
    }
}