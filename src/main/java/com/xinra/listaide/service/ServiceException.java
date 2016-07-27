package com.xinra.listaide.service;

/**
 * Checked exception that is thrown by services in case of any error. All other exceptions
 * a service wants to throw should be wrapped in a ServiceException.
 * 
 * @author erikhofer
 */
public class ServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see Exception#Exception()
	 */
    public ServiceException() {
        super();
    }

    /**
     * @see Exception#Exception(String)
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

}
