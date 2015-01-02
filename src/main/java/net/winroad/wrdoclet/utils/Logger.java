package net.winroad.wrdoclet.utils;

import org.apache.maven.plugin.logging.Log;

public class Logger {
	protected Log mvnLog;
	protected org.slf4j.Logger slf4jLog;
	
	public Log getMvnLog() {
		return this.mvnLog;
	}
	
    public void setMvnLog(Log mvnLog) {
        this.mvnLog = mvnLog;
    }	
    public org.slf4j.Logger getSlf4jLog() {
    	return this.slf4jLog;
    }
    
    public void setSlf4jLog(org.slf4j.Logger slf4jLog) {
        this.slf4jLog = slf4jLog;
    }	
    
	public boolean isDebugEnabled() {
		if(this.mvnLog != null) {
			return this.mvnLog.isDebugEnabled();			
		} else if(this.slf4jLog != null) {
			return this.slf4jLog.isDebugEnabled();
		} else {
			return false;
		}
	}

	public void debug(CharSequence paramCharSequence) {
		if(this.mvnLog != null) {
			this.mvnLog.debug(paramCharSequence);		
		} else {
			this.slf4jLog.debug(paramCharSequence.toString());
		}
	}

	public void debug(CharSequence paramCharSequence,
			Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.debug(paramCharSequence, paramThrowable);		
		} else {
			this.slf4jLog.debug(paramCharSequence.toString(), paramThrowable);
		}		
	}

	public void debug(Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.debug(paramThrowable);		
		} else {
			this.slf4jLog.debug("", paramThrowable);
		}	
	}

	public boolean isInfoEnabled() {
		if(this.mvnLog != null) {
			return this.mvnLog.isInfoEnabled();			
		} else if(this.slf4jLog != null) {
			return this.slf4jLog.isInfoEnabled();
		} else {
			return false;
		}		
	}

	public void info(CharSequence paramCharSequence) {
		if(this.mvnLog != null) {
			this.mvnLog.info(paramCharSequence);		
		} else {
			this.slf4jLog.info(paramCharSequence.toString());
		}
	}

	public void info(CharSequence paramCharSequence,
			Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.info(paramCharSequence, paramThrowable);		
		} else {
			this.slf4jLog.info(paramCharSequence.toString(), paramThrowable);
		}
	}

	public void info(Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.info(paramThrowable);		
		} else {
			this.slf4jLog.info("", paramThrowable);
		}
	}

	public boolean isWarnEnabled() {
		if(this.mvnLog != null) {
			return this.mvnLog.isWarnEnabled();			
		} else if(this.slf4jLog != null) {
			return this.slf4jLog.isWarnEnabled();
		} else {
			return false;
		}				
	}

	public void warn(CharSequence paramCharSequence) {
		if(this.mvnLog != null) {
			this.mvnLog.warn(paramCharSequence);		
		} else {
			this.slf4jLog.warn(paramCharSequence.toString());
		}		
	}

	public void warn(CharSequence paramCharSequence,
			Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.warn(paramCharSequence, paramThrowable);		
		} else {
			this.slf4jLog.warn(paramCharSequence.toString(), paramThrowable);
		}
	}

	public void warn(Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.warn(paramThrowable);		
		} else {
			this.slf4jLog.warn("", paramThrowable);
		}
	}

	public boolean isErrorEnabled() {
		if(this.mvnLog != null) {
			return this.mvnLog.isErrorEnabled();			
		} else if(this.slf4jLog != null) {
			return this.slf4jLog.isErrorEnabled();
		} else {
			return false;
		}
	}

	public void error(CharSequence paramCharSequence) {
		if(this.mvnLog != null) {
			this.mvnLog.error(paramCharSequence);		
		} else {
			this.slf4jLog.error(paramCharSequence.toString());
		}		
	}

	public void error(CharSequence paramCharSequence,
			Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.error(paramCharSequence, paramThrowable);		
		} else {
			this.slf4jLog.error(paramCharSequence.toString(), paramThrowable);
		}		
	}

	public void error(Throwable paramThrowable) {
		if(this.mvnLog != null) {
			this.mvnLog.error(paramThrowable);		
		} else {
			this.slf4jLog.error("", paramThrowable);
		}
	}
}
