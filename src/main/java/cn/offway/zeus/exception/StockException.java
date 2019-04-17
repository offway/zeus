package cn.offway.zeus.exception;

public class StockException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6950222553892145808L;
	
    public StockException() {
        super();
    }

    public StockException(String message) {
        super(message);
    }

}
