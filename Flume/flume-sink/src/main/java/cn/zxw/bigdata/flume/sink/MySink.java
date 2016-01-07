package cn.zxw.bigdata.flume.sink;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

public class MySink extends AbstractSink implements Configurable {
	  private String myProp;
	  
	  public void configure(Context context) {
	    String myProp = context.getString("myProp", "defaultValue");

	    // Process the myProp value (e.g. validation)

	    // Store myProp for later retrieval by process() method
	    this.myProp = myProp;
	  }

	  @Override
	  public void start() {
	    // Initialize the connection to the external repository (e.g. HDFS) that
	    // this Sink will forward Events to ..
	  }

	  @Override
	  public void stop () {
	    // Disconnect from the external respository and do any
	    // additional cleanup (e.g. releasing resources or nulling-out
	    // field values) ..
	  }
	  
	  public Status process() throws EventDeliveryException {
	    Status status = null;

	    // Start transaction
	    Channel ch = getChannel();
	    Transaction txn = ch.getTransaction();
	    txn.begin();
	    try {
	      // This try clause includes whatever Channel operations you want to do

	      Event event = ch.take();

	      // Send the Event to the external repository.
	      // storeSomeData(e);

	      txn.commit();
	      status = Status.READY;
	    } catch (Throwable t) {
	      txn.rollback();

	      // Log exception, handle individual exceptions as needed

	      status = Status.BACKOFF;

	      // re-throw all Errors
	      if (t instanceof Error) {
	        throw (Error)t;
	      }
	    }
	    return status;
	  }
	}
