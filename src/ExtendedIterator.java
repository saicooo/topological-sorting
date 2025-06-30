import java.util.Iterator;

public interface ExtendedIterator<T> extends Iterator<T> {
    T prev();
}
