package notifications;

public interface Subject {
    /**
     * @param o
     */
    void addObserver(Observer o);

    /**
     * @param o
     */
    void removeObserver(Observer o);

    /**
     * @param message
     */
    void notifyObservers(String message);
}
