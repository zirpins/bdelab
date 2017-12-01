package manning.tap2;

import de.hska.iwi.bdelab.schema2.Data;
import manning.tap.ThriftPailStructure;

public class DataPailStructure extends ThriftPailStructure<Data> {
  @Override
  protected Data createThriftObject() {
    return new Data();
  }

  public Class getType() {
    return Data.class;
  }
}
