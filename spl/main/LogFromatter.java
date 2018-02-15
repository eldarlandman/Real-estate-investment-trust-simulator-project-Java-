package spl.main;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * The Class LogFromatter.
 */
public class LogFromatter extends Formatter {

	
	
	/**our log formatter for more readable log format
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(record.getMillis());
		return (	"[" + calendar.get(Calendar.HOUR) + ":" + 
						  calendar.get(Calendar.MINUTE) + ":" + 
					  	  calendar.get(Calendar.SECOND) + ":" + 
						  calendar.get(Calendar.MILLISECOND) + "] " + 
				  	  record.getLevel() + ": " + 
					  record.getMessage() + " { " + record.getSourceClassName() + " }" + '\n');
		
	}

}
