package manning.tap;

import de.hska.iwi.bdelab.schema.Data;

public class DataPailStructure extends ThriftPailStructure<Data> {
  @Override
  protected Data createThriftObject() {
    return new Data();
  }

  public Class getType() {
    return Data.class;
  }
}
