package action.hadoop.chapter6.section5.udaf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

@Description(name = "max", value = "_FUNC_(expr) - Returns the maximum value of expr")
public class GenericUDAFMax extends AbstractGenericUDAFResolver {

  static final Log LOG = LogFactory.getLog(GenericUDAFMax.class.getName());

  @Override
  public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
    throws SemanticException {
    if (parameters.length != 1) {
      throw new UDFArgumentTypeException(parameters.length - 1,
          "Exactly one argument is expected.");
    }
    ObjectInspector oi = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(parameters[0]);
    if (!ObjectInspectorUtils.compareSupported(oi)) {
      throw new UDFArgumentTypeException(parameters.length - 1,
          "Cannot support comparison of map<> type or complex type containing map<>.");
    }
    return new GenericUDAFMaxEvaluator();
  }

  @UDFType(distinctLike=true)
  public static class GenericUDAFMaxEvaluator extends GenericUDAFEvaluator {

    ObjectInspector inputOI;
    ObjectInspector outputOI;

    @Override
    public ObjectInspector init(Mode m, ObjectInspector[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      super.init(m, parameters);
      inputOI = parameters[0];
      // Copy to Java object because that saves object creation time.
      // Note that on average the number of copies is log(N) so that's not
      // very important.
      outputOI = ObjectInspectorUtils.getStandardObjectInspector(inputOI,
          ObjectInspectorCopyOption.JAVA);
      return outputOI;
    }

    /** class for storing the current max value */
    static class MaxAgg implements AggregationBuffer {
      Object o;
    }

    @Override
    public AggregationBuffer getNewAggregationBuffer() throws HiveException {
      MaxAgg result = new MaxAgg();
      return result;
    }

    @Override
    public void reset(AggregationBuffer agg) throws HiveException {
      MaxAgg myagg = (MaxAgg) agg;
      myagg.o = null;
    }

    boolean warned = false;

    @Override
    public void iterate(AggregationBuffer agg, Object[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      merge(agg, parameters[0]);
    }

    @Override
    public Object terminatePartial(AggregationBuffer agg) throws HiveException {
      return terminate(agg);
    }

    @Override
    public void merge(AggregationBuffer agg, Object partial)
        throws HiveException {
      if (partial != null) {
        MaxAgg myagg = (MaxAgg) agg;
        int r = ObjectInspectorUtils.compare(myagg.o, outputOI, partial, inputOI);
        if (myagg.o == null || r < 0) {
          myagg.o = ObjectInspectorUtils.copyToStandardObject(partial, inputOI,
              ObjectInspectorCopyOption.JAVA);
        }
      }
    }

    @Override
    public Object terminate(AggregationBuffer agg) throws HiveException {
      MaxAgg myagg = (MaxAgg) agg;
      return myagg.o;
    }

  }

}
