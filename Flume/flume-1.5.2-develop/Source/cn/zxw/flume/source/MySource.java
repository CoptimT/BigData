package cn.zxw.flume.source;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;

public class MySource extends AbstractSource implements Configurable,PollableSource {
	private String myProp;
	
	@Override
	public void configure(Context context) {
		String myProp = context.getString("myProp", "defaultValue");
		// Process the myProp value (e.g. validation, convert to another type,
		// ...)
		// Store myProp for later retrieval by process() method
		this.myProp = myProp;
	}

	@Override
	public void start() {
		// Initialize the connection to the external client
	}

	@Override
	public void stop() {
		// Disconnect from external client and do any additional cleanup
		// (e.g. releasing resources or nulling-out field values) ..
	}

	@Override
	public Status process() throws EventDeliveryException {
		Status status = null;
		try {
			// This try clause includes whatever Channel/Event operations you want to do
			// Receive new data
			Event e = null;// getSomeData();
			// Store the Event into this Source's associated Channel(s)
			getChannelProcessor().processEvent(e);
			status = Status.READY;
		} catch (Throwable t) {
			// Log exception, handle individual exceptions as needed
			status = Status.BACKOFF;
			// re-throw all Errors
			if (t instanceof Error) {
				throw (Error) t;
			}
		} finally {
			// txn.close();
		}
		return status;
	}

}
